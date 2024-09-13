package network.ermis.state.listener

import network.ermis.state.plugin.logic.LogicRegistry
import io.getstream.result.Result
import network.ermis.client.plugin.listeners.SendGiphyListener
import network.ermis.core.models.Message

/**
 * [SendGiphyListener] implementation for [StatePlugin].
 * Handles removing ephemeral message from the state.
 *
 * @param logic [LogicRegistry]
 */
internal class SendGiphyListenerState(private val logic: LogicRegistry) :
    SendGiphyListener {

    /**
     * Removes ephemeral message from the state if the request was successful.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param result The API call result.
     */
    override fun onGiphySendResult(cid: String, result: Result<Message>) {
        if (result is Result.Success) {
            val message = result.value
            logic.channelFromMessage(message)?.stateLogic()?.deleteMessage(message)
            logic.threadFromMessage(message)?.stateLogic()?.deleteMessage(message)
        }
    }
}