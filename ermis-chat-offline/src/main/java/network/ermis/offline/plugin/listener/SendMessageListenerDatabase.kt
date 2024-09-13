package network.ermis.offline.plugin.listener

import io.getstream.log.StreamLog
import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.plugin.listeners.SendMessageListener
import network.ermis.client.utils.extensions.enrichWithCid
import network.ermis.client.utils.extensions.internal.users
import network.ermis.core.errors.isPermanent
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import java.util.Date

private const val TAG = "Chat:SendMessageHandlerDB"
/**
 * Implementation of [SendMessageListener] that deals with updates of the database of the SDK.
 */
internal class SendMessageListenerDatabase(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
) : SendMessageListener {

    /**
     * Side effect to be invoked when the original request is completed with a response. This method updates the
     * database of the SDK.
     *
     * @param result [Result] response from the original request.
     * @param channelType The type of the channel in which message is sent.
     * @param channelId The id of the the channel in which message is sent.
     * @param message [Message] to be sent.
     */
    override suspend fun onMessageSendResult(
        result: Result<Message>,
        channelType: String,
        channelId: String,
        message: Message,
    ) {
        val cid = "$channelType:$channelId"
        if (messageRepository.selectMessage(message.id)?.syncStatus == SyncStatus.COMPLETED) return

        when (result) {
            is Result.Success -> handleSendMessageSuccess(cid, result.value)
            is Result.Failure -> handleSendMessageFailure(message, result.value)
        }
    }

    private suspend fun handleSendMessageSuccess(
        cid: String,
        processedMessage: Message,
    ) {
        processedMessage.enrichWithCid(cid)
            .copy(syncStatus = SyncStatus.COMPLETED)
            .also { message ->
                userRepository.insertUsers(message.users())
                messageRepository.insertMessage(message)
            }
    }

    private suspend fun handleSendMessageFailure(
        message: Message,
        error: Error,
    ) {
        val isPermanentError = error.isPermanent()
        StreamLog.w(TAG) { "[handleSendMessageFailure] isPermanentError: $isPermanentError" }

        message.copy(
            syncStatus = if (isPermanentError) {
                SyncStatus.FAILED_PERMANENTLY
            } else {
                SyncStatus.SYNC_NEEDED
            },
            updatedLocallyAt = Date(),
        ).also { parsedMessage ->
            userRepository.insertUsers(parsedMessage.users())
            messageRepository.insertMessage(parsedMessage)
        }
    }
}