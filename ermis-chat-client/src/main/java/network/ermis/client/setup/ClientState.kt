package network.ermis.client.setup

import network.ermis.core.models.ConnectionState
import network.ermis.core.models.InitializationState
import network.ermis.core.models.User
import kotlinx.coroutines.flow.StateFlow

/**
 * The current state of the SDK. With this class you can get the current user, the connection state, initialization
 * state...
 */
public interface ClientState {

    /**
     * The state of the initialization process of the SDK.
     */
    public val initializationState: StateFlow<InitializationState>

    /**
     * The current user if connected.
     */
    public val user: StateFlow<User?>

    /**
     * StateFlow<ConnectionState> that indicates if we are currently online, connecting of offline.
     */
    public val connectionState: StateFlow<ConnectionState>

    /**
     * If the WebSocket is connected.
     *
     * @return True if the WebSocket is connected, otherwise false.
     */
    public val isOnline: Boolean

    /**
     * If the WebSocket is disconnected.
     *
     * @return True if the WebSocket is disconnected, otherwise false.
     */
    public val isOffline: Boolean

    /**
     * If connection is in connecting state.
     *
     * @return True if the connection is in connecting state.
     */
    public val isConnecting: Boolean

    /**
     * If internet is available or not. This is not related to the connection of the SDK, it returns
     * if internet is available in the device.
     */
    public val isNetworkAvailable: Boolean
}
