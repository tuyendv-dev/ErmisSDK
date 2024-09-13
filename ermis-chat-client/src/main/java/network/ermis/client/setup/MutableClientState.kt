package network.ermis.client.setup

import network.ermis.client.network.NetworkStateProvider
import network.ermis.core.models.ConnectionState
import network.ermis.core.models.InitializationState
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Mutable version of [ClientState]. The class makes possible to change state of the SDK. Should only be used
 * internally by the SDK.
 */
internal class MutableClientState(private val networkStateProvider: NetworkStateProvider) : ClientState {

    private val logger by taggedLogger("Chat:ClientState")

    private val _initializationState = MutableStateFlow(InitializationState.NOT_INITIALIZED)
    private val _connectionState: MutableStateFlow<ConnectionState> = MutableStateFlow(ConnectionState.Offline)
    private var _user: MutableStateFlow<User?> = MutableStateFlow(null)

    override val user: StateFlow<User?>
        get() = _user

    override val isOnline: Boolean
        get() = _connectionState.value is ConnectionState.Connected

    override val isOffline: Boolean
        get() = _connectionState.value == ConnectionState.Offline

    override val isConnecting: Boolean
        get() = _connectionState.value == ConnectionState.Connecting

    override val initializationState: StateFlow<InitializationState>
        get() = _initializationState

    override val connectionState: StateFlow<ConnectionState> = _connectionState

    override val isNetworkAvailable: Boolean
        get() = networkStateProvider.isConnected()

    /**
     * Clears the state of [ClientMutableState].
     */
    fun clearState() {
        logger.d { "[clearState] no args" }
        _initializationState.value = InitializationState.NOT_INITIALIZED
        _connectionState.value = ConnectionState.Offline
        _user.value = null
    }

    /**
     * Sets the [ConnectionState]
     *
     * @param connectionState [ConnectionState]
     */
    fun setConnectionState(connectionState: ConnectionState) {
        logger.d { "[setConnectionState] state: $connectionState" }
        _connectionState.value = connectionState
    }

    /**
     * Sets initialized
     *
     * @param state [InitializationState]
     */
    fun setInitializationState(state: InitializationState) {
        _initializationState.value = state
    }

    /**
     * Sets the current connected user.
     *
     * @param user The [User] instance that will be configured.
     */
    fun setUser(user: User) {
        _user.value = user
    }
}
