package network.ermis.client.socket

import network.ermis.client.clientstate.DisconnectCause
import network.ermis.client.events.ChatEvent
import network.ermis.client.events.ConnectedEvent
import io.getstream.result.Error

/**
 * Listener which is invoked for WebSocket events.
 */
public open class SocketListener {

    /**
     * The callbacks are by default delivered on the main thread. Changing this property to false will deliver
     * the callbacks on their originating threads.
     *
     * Set to false for faster callback delivery on the original thread (no unnecessary context switching).
     */
    public open val deliverOnMainThread: Boolean = true

    /**
     * Invoked when the connection begins to establish and socket state changes to Connecting.
     */
    public open fun onConnecting() {
    }

    /**
     * Invoked when we receive the first [ConnectedEvent] in this connection.
     *
     * Note: This is not invoked when the ws connection is opened but when the [ConnectedEvent] is received.
     *
     * @param event [ConnectedEvent] sent by server as first event once the connection is established.
     */
    public open fun onConnected(event: ConnectedEvent) {
    }

    /**
     * Invoked when the web socket connection is disconnected.
     *
     * @param cause [DisconnectCause] reason of disconnection.
     */
    public open fun onDisconnected(cause: DisconnectCause) {
    }

    /**
     * Invoked when there is any error in this web socket connection.
     *
     * @param error [Error] object with the error details.
     */
    public open fun onError(error: Error) {
    }

    /**
     * Invoked when we receive any successful event.
     *
     * @param event parsed [ChatEvent] received in this web socket connection.
     */
    public open fun onEvent(event: ChatEvent) {
    }
}
