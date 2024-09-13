package network.ermis.core.models

/**
 * The state of initialization process.
 */
public enum class InitializationState {

    /**
     * Initialization is complete. Be aware that it doesn't mean that the SDK is connected. To track
     * the connection state, please use [ClientState.connectionState]
     */
    COMPLETE,

    /**
     * Initialization was requested and should be completed shortly. During this state, the SDK is still
     * not ready to be used.
     */
    INITIALIZING,

    /**
     * The initialization of the SDK was not requested. Use ChatClient.connectUser to start the
     * initialization process.
     */
    NOT_INITIALIZED,
}
