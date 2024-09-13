package network.ermis.client.socket

import network.ermis.client.LifecycleHandler
import network.ermis.client.StreamLifecycleObserver
import network.ermis.client.clientstate.DisconnectCause
import network.ermis.client.debugger.ChatClientDebugger
import network.ermis.core.errors.ChatErrorCode
import network.ermis.client.events.ChatEvent
import network.ermis.client.events.ConnectedEvent
import network.ermis.client.events.HealthEvent
import network.ermis.client.network.NetworkStateProvider
import network.ermis.client.scope.UserScope
import network.ermis.client.socket.ChatSocketStateService.State
import network.ermis.client.token.TokenManager
import network.ermis.core.internal.coroutines.DispatcherProvider
import network.ermis.core.models.User
import io.getstream.log.StreamLog
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("TooManyFunctions", "LongParameterList")
internal open class ChatSocket(
    private val apiKey: String,
    private val wssUrl: String,
    private val tokenManager: TokenManager,
    private val socketFactory: SocketFactory,
    private val userScope: UserScope,
    private val lifecycleObserver: StreamLifecycleObserver,
    private val networkStateProvider: NetworkStateProvider,
    private val clientDebugger: ChatClientDebugger? = null,
) {
    private var streamWebSocket: StreamWebSocket? = null
    private val logger by taggedLogger(TAG)
    private var connectionConf: SocketFactory.ConnectionConf? = null
    private val listeners = mutableSetOf<SocketListener>()
    private val chatSocketStateService = ChatSocketStateService()
    private var socketStateObserverJob: Job? = null
    private val healthMonitor = HealthMonitor(
        userScope = userScope,
        checkCallback = { (chatSocketStateService.currentState as? State.Connected)?.event?.let(::sendEvent) },
        reconnectCallback = { chatSocketStateService.onWebSocketEventLost() },
    )
    private val lifecycleHandler = object : LifecycleHandler {
        override suspend fun resume() { chatSocketStateService.onResume() }
        override suspend fun stopped() { chatSocketStateService.onStop() }
    }
    private val networkStateListener = object : NetworkStateProvider.NetworkStateListener {
        override suspend fun onConnected() { chatSocketStateService.onNetworkAvailable() }
        override suspend fun onDisconnected() { chatSocketStateService.onNetworkNotAvailable() }
    }

    @Suppress("ComplexMethod")
    private fun observeSocketStateService(): Job {
        var socketListenerJob: Job? = null

        suspend fun connectUser(connectionConf: SocketFactory.ConnectionConf) {
            logger.d { "[connectUser] connectionConf: $connectionConf" }
            userScope.launch { startObservers() }
            this.connectionConf = connectionConf

            socketListenerJob?.cancel()
            when (networkStateProvider.isConnected()) {
                true -> {
                    streamWebSocket = socketFactory.createSocket(connectionConf).apply {
                        socketListenerJob = listen().onEach {
                            logger.d { "[observeSocketStateService] Received $it" }
                            when (it) {
                                is StreamWebSocketEvent.Error -> handleError(it.streamError)
                                is StreamWebSocketEvent.Message -> handleEvent(it.chatEvent)
                            }
                        }.launchIn(userScope)
                    }
                }
                false -> chatSocketStateService.onNetworkNotAvailable()
            }
        }

        suspend fun reconnect(connectionConf: SocketFactory.ConnectionConf) {
            logger.d { "[reconnect] connectionConf: $connectionConf" }
            connectUser(connectionConf.asReconnectionConf())
        }

        return userScope.launch {
            chatSocketStateService.observer { state ->
                logger.i { "[onSocketStateChanged] state: $state" }
                when (state) {
                    is State.RestartConnection -> {
                        connectionConf?.let { chatSocketStateService.onReconnect(it, false) } ?: run {
                            logger.e { "[onSocketStateChanged] #reconnect; connectionConf is null" }
                            clientDebugger?.onNonFatalErrorOccurred(
                                tag = TAG,
                                src = "onSocketStateChanged",
                                desc = "Failed to reconnect socket on ${state.reason}",
                                error = Error.GenericError("connectionConf is null"),
                            )
                        }
                    }
                    is State.Connected -> {
                        healthMonitor.ack()
                        callListeners { listener -> listener.onConnected(state.event) }
                    }
                    is State.Connecting -> {
                        callListeners { listener -> listener.onConnecting() }
                        when (state.connectionType) {
                            ChatSocketStateService.ConnectionType.INITIAL_CONNECTION ->
                                connectUser(state.connectionConf)
                            ChatSocketStateService.ConnectionType.AUTOMATIC_RECONNECTION ->
                                reconnect(state.connectionConf.asReconnectionConf())
                            ChatSocketStateService.ConnectionType.FORCE_RECONNECTION ->
                                reconnect(state.connectionConf.asReconnectionConf())
                        }
                    }
                    is State.Disconnected -> {
                        when (state) {
                            is State.Disconnected.DisconnectedByRequest -> {
                                streamWebSocket?.close()
                                healthMonitor.stop()
                                userScope.launch { disposeObservers() }
                            }
                            is State.Disconnected.NetworkDisconnected -> {
                                streamWebSocket?.close()
                                healthMonitor.stop()
                            }
                            is State.Disconnected.Stopped -> {
                                streamWebSocket?.close()
                                healthMonitor.stop()
                                disposeNetworkStateObserver()
                            }
                            is State.Disconnected.DisconnectedPermanently -> {
                                streamWebSocket?.close()
                                healthMonitor.stop()
                                userScope.launch { disposeObservers() }
                            }
                            is State.Disconnected.DisconnectedTemporarily -> {
                                healthMonitor.onDisconnected()
                            }
                            is State.Disconnected.WebSocketEventLost -> {
                                streamWebSocket?.close()
                                connectionConf?.let { chatSocketStateService.onReconnect(it, false) }
                            }
                        }
                        callListeners { listener -> listener.onDisconnected(cause = state.cause) }
                    }
                }
            }
        }
    }

    suspend fun connectUser(user: User, isAnonymous: Boolean) {
        logger.d { "[connectUser] user.id: ${user.id}, isAnonymous: $isAnonymous" }
        socketStateObserverJob?.cancel()
        socketStateObserverJob = observeSocketStateService()
        chatSocketStateService.onConnect(
            when (isAnonymous) {
                true -> SocketFactory.ConnectionConf.AnonymousConnectionConf(wssUrl, apiKey, user)
                false -> SocketFactory.ConnectionConf.UserConnectionConf(wssUrl, apiKey, user)
            },
        )
    }

    suspend fun disconnect() {
        logger.d { "[disconnect] no args" }
        connectionConf = null
        chatSocketStateService.onRequiredDisconnect()
    }

    private suspend fun handleEvent(chatEvent: ChatEvent) {
        StreamLog.v("Chat:Events") { "[handleEvent] Received $chatEvent" }
        when (chatEvent) {
            is ConnectedEvent -> chatSocketStateService.onConnectionEstablished(chatEvent)
            is HealthEvent -> healthMonitor.ack()
            else -> callListeners { listener -> listener.onEvent(chatEvent) }
        }
    }

    private suspend fun startObservers() {
        lifecycleObserver.observe(lifecycleHandler)
        networkStateProvider.subscribe(networkStateListener)
    }

    private suspend fun disposeObservers() {
        lifecycleObserver.dispose(lifecycleHandler)
        disposeNetworkStateObserver()
    }

    private fun disposeNetworkStateObserver() {
        networkStateProvider.unsubscribe(networkStateListener)
    }

    private suspend fun handleError(error: Error) {
        logger.e { "[handleError] error: $error" }
        when (error) {
            is Error.NetworkError -> onChatNetworkError(error)
            else -> callListeners { it.onError(error) }
        }
    }

    private suspend fun onChatNetworkError(error: Error.NetworkError) {
        if (ChatErrorCode.isAuthenticationError(error.serverErrorCode)) {
            tokenManager.expireToken()
        }

        when (error.serverErrorCode) {
            ChatErrorCode.UNDEFINED_TOKEN.code,
            ChatErrorCode.INVALID_TOKEN.code,
            ChatErrorCode.API_KEY_NOT_FOUND.code,
            ChatErrorCode.VALIDATION_ERROR.code,
            -> {
                logger.d {
                    "One unrecoverable error happened. Error: $error. Error code: ${error.serverErrorCode}"
                }
                chatSocketStateService.onUnrecoverableError(error)
            }
            else -> chatSocketStateService.onNetworkError(error)
        }
    }

    fun removeListener(listener: SocketListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    fun addListener(listener: SocketListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    /**
     * Attempt to send [event] to the web socket connection.
     * Returns true only if socket is connected and [okhttp3.WebSocket.send] returns true, otherwise false
     *
     * @see [okhttp3.WebSocket.send]
     */
    internal fun sendEvent(event: ChatEvent): Boolean = streamWebSocket?.send(event) ?: false

    internal fun isConnected(): Boolean = chatSocketStateService.currentState is State.Connected

    /**
     * Awaits until [State.Connected] is set.
     *
     * @param timeoutInMillis Timeout time in milliseconds.
     */
    internal suspend fun awaitConnection(timeoutInMillis: Long = DEFAULT_CONNECTION_TIMEOUT) {
        awaitState<State.Connected>(timeoutInMillis)
    }

    /**
     * Awaits until specified [State] is set.
     *
     * @param timeoutInMillis Timeout time in milliseconds.
     */
    internal suspend inline fun <reified T : State> awaitState(timeoutInMillis: Long) {
        withTimeout(timeoutInMillis) {
            chatSocketStateService.currentStateFlow.first { it is T }
        }
    }

    /**
     * Get connection id of this connection.
     */
    internal fun connectionIdOrError(): String = when (val state = chatSocketStateService.currentState) {
        is State.Connected -> state.event.connectionId
        else -> error("This state doesn't contain connectionId")
    }

    suspend fun reconnectUser(user: User, isAnonymous: Boolean, forceReconnection: Boolean) {
        logger.d {
            "[reconnectUser] user.id: ${user.id}, isAnonymous: $isAnonymous, forceReconnection: $forceReconnection"
        }
        chatSocketStateService.onReconnect(
            when (isAnonymous) {
                true -> SocketFactory.ConnectionConf.AnonymousConnectionConf(wssUrl, apiKey, user)
                false -> SocketFactory.ConnectionConf.UserConnectionConf(wssUrl, apiKey, user)
            },
            forceReconnection,
        )
    }

    private fun callListeners(call: (SocketListener) -> Unit) {
        synchronized(listeners) {
            listeners.forEach { listener ->
                val context = if (listener.deliverOnMainThread) {
                    DispatcherProvider.Main
                } else {
                    EmptyCoroutineContext
                }
                userScope.launch(context) { call(listener) }
            }
        }
    }

    private val State.Disconnected.cause
        get() = when (this) {
            is State.Disconnected.DisconnectedByRequest,
            is State.Disconnected.Stopped,
            -> DisconnectCause.ConnectionReleased
            is State.Disconnected.NetworkDisconnected -> DisconnectCause.NetworkNotAvailable
            is State.Disconnected.DisconnectedPermanently -> DisconnectCause.UnrecoverableError(error)
            is State.Disconnected.DisconnectedTemporarily -> DisconnectCause.Error(error)
            is State.Disconnected.WebSocketEventLost -> DisconnectCause.WebSocketNotAvailable
        }

    companion object {
        private const val TAG = "Chat:Socket"
        private const val DEFAULT_CONNECTION_TIMEOUT = 60_000L
    }
}
