package network.ermis.client.utils.observable

import network.ermis.client.ChatEventListener
import network.ermis.client.clientstate.DisconnectCause
import network.ermis.client.events.ChatEvent
import network.ermis.client.events.ConnectedEvent
import network.ermis.client.events.ConnectingEvent
import network.ermis.client.events.DisconnectedEvent
import network.ermis.client.events.ErrorEvent
import network.ermis.client.socket.ChatSocket
import network.ermis.client.socket.SocketListener
import network.ermis.core.models.ConnectionData
import network.ermis.core.models.EventType
import io.getstream.result.Error
import io.getstream.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date

internal class ChatEventsObservable(
    private val waitConnection: FlowCollector<Result<ConnectionData>>,
    private val scope: CoroutineScope,
    private val chatSocket: ChatSocket,
) {

    private val mutex = Mutex()

    private val subscriptions = mutableSetOf<EventSubscription>()
    private val eventsMapper = EventsMapper(this)

    private fun onNext(event: ChatEvent) {
        notifySubscriptions(event)
        emitConnectionEvents(event)
    }

    private fun emitConnectionEvents(event: ChatEvent) {
        scope.launch {
            when (event) {
                is ConnectedEvent -> {
                    waitConnection.emit(Result.Success(ConnectionData(event.me, event.connectionId)))
                }
                is ErrorEvent -> {
                    waitConnection.emit(Result.Failure(event.error))
                }
                else -> Unit // Ignore other events
            }
        }
    }

    fun subscribe(
        filter: (ChatEvent) -> Boolean = { true },
        listener: ChatEventListener<ChatEvent>,
    ): Disposable {
        return addSubscription(SubscriptionImpl(filter, listener))
    }

    fun subscribeSuspend(
        filter: (ChatEvent) -> Boolean = { true },
        listener: ChatEventSuspendListener<ChatEvent>,
    ): Disposable {
        return addSubscription(SuspendSubscription(scope, filter, listener))
    }

    fun subscribeSingle(
        filter: (ChatEvent) -> Boolean = { true },
        listener: ChatEventListener<ChatEvent>,
    ): Disposable {
        return addSubscription(
            SubscriptionImpl(filter, listener).apply {
                afterEventDelivered = this::dispose
            },
        )
    }

    private fun notifySubscriptions(event: ChatEvent) {
        scope.launch {
            mutex.withLock {
                val iterator = subscriptions.iterator()
                while (iterator.hasNext()) {
                    val subscription = iterator.next()
                    if (subscription.isDisposed) {
                        iterator.remove()
                    } else {
                        subscription.onNext(event)
                    }
                }
                // remove listener from socket events if there are no subscriptions
                if (subscriptions.isEmpty()) {
                    chatSocket.removeListener(eventsMapper)
                }
            }
        }
    }

    private fun addSubscription(subscription: EventSubscription): Disposable {
        scope.launch {
            mutex.withLock {
                // add listener to socket events only once
                if (subscriptions.isEmpty()) {
                    chatSocket.addListener(eventsMapper)
                }
                subscriptions.add(subscription)
            }
        }
        return subscription
    }

    internal fun interface ChatEventSuspendListener<EventT : ChatEvent> {
        suspend fun onEvent(event: EventT)
    }

    /**
     * Maps methods of [SocketListener] to events of [ChatEventsObservable]
     */
    private class EventsMapper(private val observable: ChatEventsObservable) : SocketListener() {

        override val deliverOnMainThread: Boolean
            get() = false

        override fun onConnecting() {
            observable.onNext(ConnectingEvent(EventType.CONNECTION_CONNECTING, Date(), null))
        }

        override fun onConnected(event: ConnectedEvent) {
            observable.onNext(event)
        }

        override fun onDisconnected(cause: DisconnectCause) {
            observable.onNext(
                DisconnectedEvent(
                    EventType.CONNECTION_DISCONNECTED,
                    createdAt = Date(),
                    disconnectCause = cause,
                    rawCreatedAt = null,
                ),
            )
        }

        override fun onEvent(event: ChatEvent) {
            observable.onNext(event)
        }

        override fun onError(error: Error) {
            observable.onNext(
                ErrorEvent(
                    EventType.CONNECTION_ERROR,
                    createdAt = Date(),
                    error = error,
                    rawCreatedAt = null,
                ),
            )
        }
    }
}
