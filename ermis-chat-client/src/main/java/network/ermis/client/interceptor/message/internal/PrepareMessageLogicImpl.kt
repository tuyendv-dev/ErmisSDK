package network.ermis.client.interceptor.message.internal

import network.ermis.client.channel.state.ChannelStateLogicProvider
import network.ermis.client.utils.extensions.EXTRA_UPLOAD_ID
import network.ermis.client.utils.extensions.enrichWithCid
import network.ermis.client.utils.extensions.internal.populateMentions
import network.ermis.client.utils.extensions.uploadId
import network.ermis.client.interceptor.message.PrepareMessageLogic
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.internal.getMessageType
import network.ermis.core.models.Attachment
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import network.ermis.core.models.User
import java.util.Date
import java.util.UUID

internal class PrepareMessageLogicImpl(
    private val clientState: ClientState,
    private val channelStateLogicProvider: ChannelStateLogicProvider?,
) : PrepareMessageLogic {

    /**
     * Prepares the message and its attachments but doesn't upload attachments.
     *
     * Following steps are required to initialize message properly before sending the message to the backend API:
     * 1. Message id is generated if the message doesn't have id.
     * 2. Message cid is updated if the message doesn't have cid.
     * 3. Message user is set to the current user.
     * 4. Attachments are prepared with upload state.
     * 5. Message timestamp and sync status is set.
     *
     * Then this message is inserted in database (Optimistic UI update) and final message is returned.
     */
    @Suppress("ComplexMethod")
    override fun prepareMessage(message: Message, channelId: String, channelType: String, user: User): Message {
        val channel = channelStateLogicProvider?.channelStateLogic(channelType, channelId)

        val attachments = message.attachments.map {
            when (it.upload) {
                null -> it.copy(uploadState = Attachment.UploadState.Success)
                else -> it.copy(
                    extraData = it.extraData + mapOf(EXTRA_UPLOAD_ID to (it.uploadId ?: generateUploadId())),
                    uploadState = Attachment.UploadState.Idle,
                )
            }
        }
        return message.copy(
            id = message.id.takeIf { it.isNotBlank() } ?: generateMessageId(user.id),
            user = user,
            attachments = attachments,
            type = getMessageType(message),
            createdLocallyAt = message.createdAt ?: message.createdLocallyAt ?: Date(),
            syncStatus = when {
                attachments.any { it.uploadState is Attachment.UploadState.Idle } -> SyncStatus.AWAITING_ATTACHMENTS
                clientState.isNetworkAvailable -> SyncStatus.IN_PROGRESS
                else -> SyncStatus.SYNC_NEEDED
            },
        )
            .let { copiedMessage ->
                copiedMessage.takeIf { it.cid.isBlank() }
                    ?.enrichWithCid("$channelType:$channelId")
                    ?: copiedMessage
            }
            .let { copiedMessage ->
                channel
                    ?.listenForChannelState()
                    ?.toChannel()
                    ?.let(copiedMessage::populateMentions)
                    ?: copiedMessage
            }
            .also { preparedMessage ->
                if (preparedMessage.replyMessageId != null) {
                    channel?.replyMessage(null)
                }
            }
    }

    /**
     * Returns a unique message id prefixed with user id.
     */
    private fun generateMessageId(userId: String): String {
        return UUID.randomUUID().toString()
    }

    private fun generateUploadId(): String {
        return "upload_id_${UUID.randomUUID()}"
    }
}
