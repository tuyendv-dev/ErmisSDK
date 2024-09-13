package network.ermis.client.clientstate

/**
 * Sealed class represents possible cause of disconnection.
 */
public sealed class DisconnectCause {

    /**
     * Happens when networks is not available anymore.
     */
    public object NetworkNotAvailable : DisconnectCause() { override fun toString(): String = "NetworkNotAvailable" }

    /**
     * Happens when Web Socket connection is not available.
     */
    public object WebSocketNotAvailable : DisconnectCause() {
        override fun toString(): String = "WebSocketNotAvailable"
    }

    /**
     * Happens when some non critical error occurs.
     * @param error Instance of [io.getstream.result.Error.NetworkError] as a reason of it.
     */
    public data class Error(public val error: io.getstream.result.Error.NetworkError?) : DisconnectCause()

    /**
     * Happens when a critical error occurs. Connection can't be restored after such disconnection.
     * @param error Instance of [io.getstream.result.Error.NetworkError] as a reason of it.
     */
    public data class UnrecoverableError(public val error: io.getstream.result.Error.NetworkError?) : DisconnectCause()

    /**
     * Happens when disconnection has been done intentionally. E.g. we release connection when app went to background
     * or when the user logout.
     */
    public object ConnectionReleased : DisconnectCause() { override fun toString(): String = "ConnectionReleased" }
}
