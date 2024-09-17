package network.ermis.state.listener

import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.errors.cause.MessageModerationDeletedException
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.message.isModerationError
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import network.ermis.state.plugin.logic.LogicRegistry
import network.ermis.state.plugin.logic.channel.ChannelLogic
import network.ermis.state.plugin.state.global.GlobalState
import java.util.Date

/**
 * Listener for requests of message deletion and for message deletion results responsible to
 * change SDK state
 */
internal class DeleteMessageListenerState(
    private val logic: LogicRegistry,
    private val clientState: ClientState,
    private val globalState: GlobalState,
) : network.ermis.client.plugin.listeners.DeleteMessageListener {

    /**
     * Checks if message can be safely deleted.
     *
     * @param messageId The message id to be deleted.
     */
    override suspend fun onMessageDeletePrecondition(messageId: String): Result<Unit> {
        val channelLogic: ChannelLogic? = logic.channelFromMessageId(messageId)

        return channelLogic?.getMessage(messageId)?.let { message ->
            val isModerationFailed = message.isModerationError(clientState.user.value?.id)

            if (isModerationFailed) {
                deleteMessage(message)
                Result.Failure(
                    Error.ThrowableError(
                        message = "Message with failed moderation has been deleted locally: $messageId",
                        cause = MessageModerationDeletedException(
                            "Message with failed moderation has been deleted locally: $messageId",
                        ),
                    ),
                )
            } else {
                Result.Success(Unit)
            }
        } ?: Result.Failure(Error.GenericError(message = "No message found with id: $messageId"))
    }

    /**
     * Method called when a request to delete a message in the API happens
     *
     * @param messageId
     */
    override suspend fun onMessageDeleteRequest(messageId: String) {
        val channelLogic: ChannelLogic? = logic.channelFromMessageId(messageId)

        channelLogic?.getMessage(messageId)?.let { message ->
            val isModerationFailed = message.isModerationError(clientState.user.value?.id)
            if (isModerationFailed || message.syncStatus == SyncStatus.FAILED_PERMANENTLY) {
                deleteMessage(message)
            } else {
                val networkAvailable = clientState.isNetworkAvailable
                val messageToBeDeleted = message.copy(
                    deletedAt = Date(),
                    syncStatus = if (!networkAvailable) SyncStatus.SYNC_NEEDED else SyncStatus.IN_PROGRESS,
                )

                updateMessage(messageToBeDeleted)
            }
        }
    }

    /**
     * Method called when a request for message deletion return. Use it to update database, update messages or
     * to present an error to the user.
     *
     * @param result the result of the API call.
     */
    override suspend fun onMessageDeleteResult(originalMessageId: String, result: Result<Message>) {
        when (result) {
            is Result.Success -> {
                updateMessage(result.value.copy(syncStatus = SyncStatus.COMPLETED))
            }
            is Result.Failure -> {
                logic.channelFromMessageId(originalMessageId)
                    ?.getMessage(originalMessageId)
                    ?.let { originalMessage ->
                        val failureMessage = originalMessage.copy(
                            syncStatus = SyncStatus.SYNC_NEEDED,
                            updatedLocallyAt = Date(),
                        )

                        updateMessage(failureMessage)
                    }
            }
        }
    }

    private fun updateMessage(message: Message) {
        logic.channelFromMessage(message)?.upsertMessage(message)
        logic.threadFromMessage(message)?.upsertMessage(message)
    }

    private fun deleteMessage(message: Message) {
        logic.channelFromMessage(message)?.deleteMessage(message)
        logic.threadFromMessage(message)?.deleteMessage(message)
    }
}
