package network.ermis.state.listener

import io.getstream.log.StreamLog
import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.plugin.listeners.SendMessageListener
import network.ermis.client.utils.extensions.enrichWithCid
import network.ermis.core.errors.isPermanent
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import network.ermis.state.plugin.logic.LogicRegistry
import java.util.Date

private const val TAG = "Chat:SendMessageHandler"

/**
 * Implementation of [SendMessageListener] that deals with updates of state of the SDK.
 */
internal class SendMessageListenerState(private val logic: LogicRegistry) :
    SendMessageListener {

    /**
     * Side effect to be invoked when the original request is completed with a response. This method updates the state
     * of the SDK.
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
        handleLastSentMessageDate(result, message)

        val cid = "$channelType:$channelId"

        if (logic.getMessageById(message.id)?.syncStatus == SyncStatus.COMPLETED) return

        when (result) {
            is Result.Success -> handleSendMessageSuccess(cid, logic, result.value)
            is Result.Failure -> handleSendMessageFailure(logic, message, result.value)
        }
    }

    /**
     * Updates the message object and local database with new message state after message is sent successfully.
     *
     * @param processedMessage [Message] returned from API response.
     * @return [Message] Updated message.
     */
    private fun handleSendMessageSuccess(
        cid: String,
        logic: LogicRegistry,
        processedMessage: Message,
    ) {
        processedMessage.enrichWithCid(cid)
            .copy(syncStatus = SyncStatus.COMPLETED)
            .also { message ->
                logic.channelFromMessage(message)?.upsertMessage(message)
                logic.threadFromMessage(message)?.upsertMessage(message)
            }
    }

    /**
     * Updates the message object and local database with new message state after message wasn't sent due to error.
     *
     * @param message [Message] that were being sent.
     * @param error [Error] with the reason of failure.
     *
     * @return [Message] Updated message.
     */
    private fun handleSendMessageFailure(
        logic: LogicRegistry,
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
        ).also {
            logic.channelFromMessage(it)?.upsertMessage(it)
            logic.threadFromMessage(it)?.upsertMessage(it)
        }
    }

    /**
     * Updates the channel state container with the information about the last message send date.
     *
     * Note: Sometimes [NewMessageEvent] event arrives faster than [onMessageSendResult] is executed.
     * As a result, the sync status is already [SyncStatus.COMPLETED] and [handleSendMessageSuccess]
     * is never executed. That's why this method is executed as early as possible.
     *
     * @param result [Result] response from the original request.
     * @param message [Message] to be sent.
     */
    private fun handleLastSentMessageDate(
        result: Result<Message>,
        message: Message,
    ) {
        if (result is Result.Success) {
            logic.channelFromMessage(message)?.setLastSentMessageDate(result.value.createdAt)
        }
    }
}
