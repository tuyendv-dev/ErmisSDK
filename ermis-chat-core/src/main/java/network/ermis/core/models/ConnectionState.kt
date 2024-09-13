package network.ermis.core.models

import androidx.compose.runtime.Immutable

/**
 * Represents possible states of the WebSocket connection.
 */
@Immutable
public sealed class ConnectionState {
    /**
     * The client is connected to the WebSocket.
     */
    @Immutable
    public data object Connected : ConnectionState() { override fun toString(): String = "Connected" }

    /**
     * The client is trying to connect to the WebSocket.
     */
    @Immutable
    public data object Connecting : ConnectionState() { override fun toString(): String = "Connecting" }

    /**
     * The client is permanently disconnected from the WebSocket.
     */
    @Immutable
    public data object Offline : ConnectionState() { override fun toString(): String = "Offline" }
}
