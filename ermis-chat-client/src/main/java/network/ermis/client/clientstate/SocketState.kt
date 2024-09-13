package network.ermis.client.clientstate

internal sealed class SocketState {
    object Idle : SocketState() { override fun toString() = "Idle" }
    object Pending : SocketState() { override fun toString() = "Pending" }
    data class Connected(val connectionId: String) : SocketState()
    object Disconnected : SocketState() { override fun toString() = "Disconnected" }

    internal fun connectionIdOrError(): String = when (this) {
        is Connected -> connectionId
        else -> error("This state doesn't contain connectionId")
    }
}
