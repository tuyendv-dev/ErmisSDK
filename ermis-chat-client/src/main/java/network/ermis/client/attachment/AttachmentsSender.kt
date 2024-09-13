package network.ermis.client.attachment

import android.content.Context
import network.ermis.client.attachment.worker.UploadAttachmentsAndroidWorker
import network.ermis.client.utils.extensions.internal.hasPendingAttachments
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Attachment
import network.ermis.core.models.Message
import network.ermis.core.models.UploadAttachmentsNetworkType
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Class responsible for sending attachments to the backend.
 *
 * @param context Context.
 * @param networkType [UploadAttachmentsNetworkType]
 * @param clientState [ClientState]
 * @param scope [CoroutineScope]
 */
internal class AttachmentsSender(
    private val context: Context,
    private val networkType: UploadAttachmentsNetworkType,
    private val clientState: ClientState,
    private val scope: CoroutineScope,
    private val verifier: AttachmentsVerifier = AttachmentsVerifier,
) {

    private var jobsMap: Map<String, Job> = emptyMap()
    private val uploadIds = mutableMapOf<String, UUID>()
    private val logger by taggedLogger("Chat:AttachmentsSender")

    internal suspend fun sendAttachments(
        message: Message,
        channelType: String,
        channelId: String,
        isRetrying: Boolean,
    ): Result<Message> {
        val result = if (!isRetrying) {
            if (message.hasPendingAttachments()) {
                logger.d {
                    "[sendAttachments] Message ${message.id}" +
                        " has ${message.attachments.size} pending attachments"
                }
                uploadAttachments(message, channelType, channelId)
            } else {
                logger.d { "[sendAttachments] Message ${message.id} without attachments" }
                Result.Success(message)
            }
        } else {
            logger.d { "[sendAttachments] Retrying Message ${message.id}" }
            retryMessage(message, channelType, channelId)
        }
        return AttachmentsVerifier.verifyAttachments(result)
    }

    /**
     * Tries to upload attachments of this [message] without preparing.
     *
     * It is used when we have some messages already pending in database (due to any non permanent error)
     *
     * @param message [Message] to be retried.
     *
     * @return [Result] having message with latest attachments state or error if there was any.
     */
    private suspend fun retryMessage(
        message: Message,
        channelType: String,
        channelId: String,
    ): Result<Message> =
        uploadAttachments(message, channelType, channelId)

    /**
     * Uploads the attachment of this message if there is any pending attachments and return the updated message.
     *
     * @param message [Message] whose attachments are to be uploaded.
     *
     * @return [Result] having message with latest attachments state or error if there was any.
     */
    private suspend fun uploadAttachments(
        message: Message,
        channelType: String,
        channelId: String,
    ): Result<Message> {
        return if (clientState.isNetworkAvailable) {
            waitForAttachmentsToBeSent(message, channelType, channelId)
        } else {
            enqueueAttachmentUpload(message, channelType, channelId)
            logger.d { "[uploadAttachments] Chat is offline, not sending message with id ${message.id}" }
            Result.Failure(
                Error.GenericError(
                    "Chat is offline, not sending message with id ${message.id} and text ${message.text}",
                ),
            )
        }
    }

    /**
     * Waits till all attachments are uploaded or either of them fails.
     *
     * @param newMessage Message whose attachments are to be uploaded.
     */
    private suspend fun waitForAttachmentsToBeSent(
        newMessage: Message,
        channelType: String,
        channelId: String,
    ): Result<Message> {
        jobsMap[newMessage.id]?.cancel()
        var allAttachmentsUploaded = false
        var messageToBeSent = newMessage

        AttachmentsUploadStates.updateMessageAttachments(messageToBeSent)

        jobsMap = jobsMap + (
            newMessage.id to scope.launch {
                AttachmentsUploadStates.observeAttachments(newMessage.id)
                    .filterNot(Collection<Attachment>::isEmpty)
                    .collect { attachments ->
                        when {
                            attachments.all { it.uploadState == Attachment.UploadState.Success } -> {
                                messageToBeSent = newMessage.copy(attachments = attachments.toMutableList())
                                allAttachmentsUploaded = true
                                jobsMap[newMessage.id]?.cancel()
                            }
                            attachments.any { it.uploadState is Attachment.UploadState.Failed } -> {
                                jobsMap[newMessage.id]?.cancel()
                            }
                            else -> Unit
                        }
                    }
            }
            )
        enqueueAttachmentUpload(newMessage, channelType, channelId)
        jobsMap[newMessage.id]?.join()
        return if (allAttachmentsUploaded) {
            logger.d { "[waitForAttachmentsToBeSent] All attachments for message ${newMessage.id} uploaded" }
            Result.Success(messageToBeSent.copy(type = Message.TYPE_REGULAR))
        } else {
            logger.i { "[waitForAttachmentsToBeSent] Could not upload attachments for message ${newMessage.id}" }
            Result.Failure(
                Error.GenericError("Could not upload attachments, not sending message with id ${newMessage.id}"),
            )
        }.also {
            AttachmentsUploadStates.removeMessageAttachmentsState(newMessage.id)
            uploadIds.remove(newMessage.id)
        }
    }

    /**
     * Enqueues attachment upload work.
     */
    private fun enqueueAttachmentUpload(message: Message, channelType: String, channelId: String) {
        val workId = UploadAttachmentsAndroidWorker.start(context, channelType, channelId, message.id, networkType)
        uploadIds[message.id] = workId
    }

    /**
     * Cancels all the running job.
     */
    fun cancelJobs() {
        AttachmentsUploadStates.clearStates()
        jobsMap.values.forEach { it.cancel() }
        uploadIds.values.forEach { UploadAttachmentsAndroidWorker.stop(context, it) }
    }
}
