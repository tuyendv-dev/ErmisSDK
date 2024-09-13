package network.ermis.client.utils.observable

import network.ermis.client.ChatEventListener
import network.ermis.client.events.ChatEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

public interface Disposable {
    public val isDisposed: Boolean
    public fun dispose()
}

internal interface EventSubscription : Disposable {
    fun onNext(event: ChatEvent)
}

internal open class SubscriptionImpl(
    private val filter: (ChatEvent) -> Boolean,
    listener: ChatEventListener<ChatEvent>,
) : EventSubscription {

    @Volatile
    private var listener: ChatEventListener<ChatEvent>? = listener

    @Volatile
    override var isDisposed: Boolean = false

    var afterEventDelivered: () -> Unit = {}

    override fun dispose() {
        isDisposed = true
        listener = null
    }

    final override fun onNext(event: ChatEvent) {
        check(!isDisposed) { "Subscription already disposed, onNext should not be called on it" }

        if (filter(event)) {
            try {
                listener!!.onEvent(event)
            } finally {
                afterEventDelivered()
            }
        }
    }
}

internal class SuspendSubscription(
    private val scope: CoroutineScope,
    private val filter: (ChatEvent) -> Boolean,
    listener: ChatEventsObservable.ChatEventSuspendListener<ChatEvent>,
) : EventSubscription {
    @Volatile
    private var listener: ChatEventsObservable.ChatEventSuspendListener<ChatEvent>? = listener

    @Volatile
    override var isDisposed: Boolean = false

    override fun dispose() {
        isDisposed = true
        listener = null
    }

    override fun onNext(event: ChatEvent) {
        check(!isDisposed) { "Subscription already disposed, onNext should not be called on it" }
        scope.launch {
            if (filter(event)) {
                listener?.onEvent(event)
            }
        }
    }
}
