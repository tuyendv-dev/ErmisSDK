package network.ermis.core.internal.fsm.builder

import kotlin.reflect.KClass

internal typealias StateFunction<S, E> = (S, E) -> S

public class StateHandlerBuilder<STATE : Any, EVENT : Any, S : STATE> {

    @PublishedApi
    internal val eventHandlers: MutableMap<KClass<out EVENT>, StateFunction<STATE, EVENT>> = mutableMapOf()

    @PublishedApi
    internal val onEnterListeners: MutableList<(STATE, EVENT) -> Unit> = mutableListOf()

    public inline fun <reified E : EVENT> onEvent(noinline func: S.(E) -> STATE) {
        @Suppress("UNCHECKED_CAST")
        eventHandlers[E::class] = func as (STATE, EVENT) -> STATE
    }

    public inline fun onEnter(crossinline listener: S.(EVENT) -> Unit) {
        onEnterListeners.add { state, cause ->
            @Suppress("UNCHECKED_CAST")
            listener(state as S, cause)
        }
    }

    @PublishedApi
    internal fun get(): Map<KClass<out EVENT>, StateFunction<STATE, EVENT>> = eventHandlers

    @PublishedApi
    internal fun getEnterListeners(): MutableList<(STATE, EVENT) -> Unit> = onEnterListeners
}
