package network.ermis.client

import androidx.lifecycle.LifecycleOwner
import network.ermis.client.events.ChatEvent
import network.ermis.client.utils.observable.Disposable
import kotlin.reflect.KClass

/**
 * Subscribes to client events of type [T].
 */
public inline fun <reified T : ChatEvent> ErmisClient.subscribeFor(
    listener: ChatEventListener<T>,
): Disposable {
    return this.subscribeFor(
        T::class.java,
        listener = { event -> listener.onEvent(event as T) },
    )
}

/**
 * Subscribes to client events of type [T], in the lifecycle of [lifecycleOwner].
 *
 * Only receives events when the lifecycle is in a STARTED state, otherwise events are dropped.
 */
public inline fun <reified T : ChatEvent> ErmisClient.subscribeFor(
    lifecycleOwner: LifecycleOwner,
    listener: ChatEventListener<T>,
): Disposable {
    return this.subscribeFor(
        lifecycleOwner,
        T::class.java,
        listener = { event -> listener.onEvent(event as T) },
    )
}

/**
 * Subscribes to the specific [eventTypes] of the client.
 */
public fun ErmisClient.subscribeFor(
    vararg eventTypes: KClass<out ChatEvent>,
    listener: ChatEventListener<ChatEvent>,
): Disposable {
    return subscribeFor(eventTypes = eventTypes.map { it.java }.toTypedArray(), listener = listener)
}

/**
 * Subscribes to the specific [eventTypes] of the client, in the lifecycle of [lifecycleOwner].
 *
 * Only receives events when the lifecycle is in a STARTED state, otherwise events are dropped.
 */
public fun ErmisClient.subscribeFor(
    lifecycleOwner: LifecycleOwner,
    vararg eventTypes: KClass<out ChatEvent>,
    listener: ChatEventListener<ChatEvent>,
): Disposable {
    return subscribeFor(lifecycleOwner, eventTypes = eventTypes.map { it.java }.toTypedArray(), listener = listener)
}

/**
 * Subscribes for the next client event of type [T].
 */
public inline fun <reified T : ChatEvent> ErmisClient.subscribeForSingle(
    listener: ChatEventListener<T>,
): Disposable {
    return this.subscribeForSingle(T::class.java, listener)
}
