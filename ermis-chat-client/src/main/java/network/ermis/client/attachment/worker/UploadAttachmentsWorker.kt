package network.ermis.client.attachment.worker

import android.content.Context
import network.ermis.client.ErmisClient
import network.ermis.client.attachment.AttachmentUploader
import network.ermis.client.attachment.AttachmentsUploadStates
import network.ermis.client.channel.ChannelMessagesUpdateLogic
import network.ermis.client.utils.extensions.uploadId
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.utils.ProgressCallback
import network.ermis.core.models.Attachment
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import io.getstream.log.StreamLog
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.recover

private const val TAG = "Chat:UploadWorker"

public class UploadAttachmentsWorker(
    appContext: Context,
    private val channelType: String,
    private val channelId: String,
    private val channelStateLogic: ChannelMessagesUpdateLogic?,
    private val messageRepository: MessageRepository,
    private val chatClient: ErmisClient,
    private val attachmentUploader: AttachmentUploader = AttachmentUploader(appContext, chatClient),
) {

    private val logger by taggedLogger(TAG)

    @Suppress("TooGenericExceptionCaught")
    public suspend fun uploadAttachmentsForMessage(messageId: String): Result<Unit> {
        val message = channelStateLogic?.listenForChannelState()?.getMessageById(messageId)
            ?: messageRepository.selectMessage(messageId)

        return try {
            message?.let { sendAttachments(it) } ?: Result.Failure(
                Error.GenericError("The message with id $messageId could not be found."),
            )
        } catch (e: Exception) {
            logger.e { "[uploadAttachmentsForMessage] #uploader; couldn't upload attachments ${e.message}" }
            message?.let { updateMessages(it) }
            Result.Failure(
                Error.ThrowableError(
                    message = "Could not upload attachments for message $messageId",
                    cause = e,
                ),
            )
        }
    }

    private suspend fun sendAttachments(message: Message): Result<Unit> {
        if (chatClient.getCurrentUser() == null) {
            logger.d { "[sendAttachments] #uploader; current user is not set. Restoring credentials" }
            if (!chatClient.containsStoredCredentials()) {
                logger.e { "[sendAttachments] #uploader; user's credentials are not available" }
                return Result.Failure(Error.GenericError("Could not set user"))
            }

            chatClient.setUserWithoutConnectingIfNeeded()
        }

        val hasPendingAttachment = message.attachments.any { attachment ->
            attachment.uploadState is Attachment.UploadState.InProgress ||
                attachment.uploadState is Attachment.UploadState.Idle
        }

        return if (!hasPendingAttachment) {
            logger.d { "[sendAttachments] #uploader; message ${message.id} doesn't have pending attachments" }
            Result.Success(Unit)
        } else {
            val attachments = uploadAttachments(message)
            updateMessages(message.copy(attachments = attachments))

            if (attachments.all { it.uploadState == Attachment.UploadState.Success }) {
                logger.d { "[sendAttachments] #uploader; all attachments for message ${message.id} uploaded" }
                Result.Success(Unit)
            } else {
                logger.e { "[sendAttachments] #uploader; unable to upload attachments for message ${message.id}" }
                Result.Failure(Error.GenericError("Unable to upload attachments for message ${message.id}"))
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun uploadAttachments(message: Message): List<Attachment> {
        return try {
            message.attachments.map { attachment ->
                if (attachment.uploadState != Attachment.UploadState.Success) {
                    logger.d {
                        "[uploadAttachments] #uploader; uploading attachment ${attachment.uploadId} " +
                            "for message ${message.id}"
                    }
                    val progressCallback = channelStateLogic?.let { logic ->
                        ProgressCallbackImpl(
                            message.id,
                            attachment.uploadId!!,
                            logic,
                        )
                    }

                    attachmentUploader.uploadAttachment(channelType, channelId, attachment, progressCallback)
                        .recover { error -> attachment.copy(uploadState = Attachment.UploadState.Failed(error)) }
                        .value
                } else {
                    logger.i {
                        "[uploadAttachments] #uploader; attachment ${attachment.uploadId}" +
                            " for message ${message.id} already uploaded"
                    }
                    attachment
                }
            }.toMutableList()
        } catch (e: Exception) {
            logger.e { "[uploadAttachments] #uploader; unable to upload attachments: ${e.message}" }
            message.attachments.map {
                it.copy(
                    uploadState = it.uploadState
                        .takeIf { it == Attachment.UploadState.Success }
                        ?: Attachment.UploadState.Failed(
                            Error.ThrowableError(message = "Could not upload attachments.", cause = e),
                        ),
                )
            }.toMutableList()
        }
    }

    private suspend fun updateMessages(message: Message) {
        val updatedMessage = message.copy(
            syncStatus = message.syncStatus
                .takeUnless {
                    message.attachments.any { attachment -> attachment.uploadState is Attachment.UploadState.Failed }
                } ?: SyncStatus.FAILED_PERMANENTLY,
        )
        channelStateLogic?.upsertMessage(updatedMessage)
        // RepositoryFacade::insertMessage is implemented as upsert, therefore we need to delete the message first
        messageRepository.deleteChannelMessage(updatedMessage)
        messageRepository.insertMessage(updatedMessage)
        AttachmentsUploadStates.updateMessageAttachments(updatedMessage)
    }

    private class ProgressCallbackImpl(
        private val messageId: String,
        private val uploadId: String,
        private val channelStateLogic: ChannelMessagesUpdateLogic,
    ) :
        ProgressCallback {
        override fun onSuccess(url: String?) {
            StreamLog.i(TAG) { "[Progress.onSuccess] #uploader; url: $url" }
            updateAttachmentUploadState(messageId, uploadId, Attachment.UploadState.Success)
        }

        override fun onError(error: Error) {
            StreamLog.e(TAG) { "[Progress.onError] #uploader; error: $error" }
            updateAttachmentUploadState(messageId, uploadId, Attachment.UploadState.Failed(error))
        }

        override fun onProgress(bytesUploaded: Long, totalBytes: Long) {
            updateAttachmentUploadState(
                messageId,
                uploadId,
                Attachment.UploadState.InProgress(bytesUploaded, totalBytes),
            )
        }

        private fun updateAttachmentUploadState(messageId: String, uploadId: String, newState: Attachment.UploadState) {
            val message = channelStateLogic.listenForChannelState().messages.value.firstOrNull { it.id == messageId }
            if (message != null) {
                val newAttachments = message.attachments.map { attachment ->
                    if (attachment.uploadId == uploadId) {
                        attachment.copy(uploadState = newState)
                    } else {
                        attachment
                    }
                }
                val updatedMessage = message.copy(attachments = newAttachments.toMutableList())
                channelStateLogic.upsertMessage(updatedMessage)
            }
        }
    }
}
