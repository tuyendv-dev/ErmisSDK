package network.ermis.state.listener

import io.getstream.result.Result
import network.ermis.client.plugin.listeners.EditMessageListener
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.extensions.updateFailedMessage
import network.ermis.client.utils.extensions.updateMessageOnlineState
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import network.ermis.state.plugin.logic.LogicRegistry

/**
 * Implementation of [EditMessageListener] that deals with state read and write.
 *
 * @param logic [LogicRegistry]
 * @param clientState [ClientState]
 */
internal class EditMessageListenerState(
    private val logic: LogicRegistry,
    private val clientState: ClientState,
) : network.ermis.client.plugin.listeners.EditMessageListener {

    /**
     * Method called when a message edit request happens. This method should be used to update messages locally.
     *
     * @param message [Message].
     */
    override suspend fun onMessageEditRequest(message: Message) {
        val isOnline = clientState.isNetworkAvailable
        val messagesToEdit = message.updateMessageOnlineState(isOnline)

        logic.channelFromMessage(messagesToEdit)?.stateLogic()?.upsertMessage(messagesToEdit)
        logic.threadFromMessage(messagesToEdit)?.stateLogic()?.upsertMessage(messagesToEdit)
    }

    /**
     * Method called when an edition in a message returns from the API. Updates the local messages accordingly.
     *
     * @param originalMessage [Message].
     * @param result the result of the API call.
     */
    override suspend fun onMessageEditResult(originalMessage: Message, result: Result<Message>) {
        val parsedMessage = when (result) {
            is Result.Success -> result.value.copy(syncStatus = SyncStatus.COMPLETED)
            is Result.Failure -> originalMessage.updateFailedMessage(result.value)
        }

        logic.channelFromMessage(parsedMessage)?.stateLogic()?.upsertMessage(parsedMessage)
        logic.threadFromMessage(parsedMessage)?.stateLogic()?.upsertMessage(parsedMessage)
    }
}
