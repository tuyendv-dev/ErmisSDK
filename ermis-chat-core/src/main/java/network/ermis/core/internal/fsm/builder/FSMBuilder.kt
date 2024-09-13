package network.ermis.core.internal.fsm.builder

import network.ermis.core.internal.fsm.FiniteStateMachine
import kotlin.reflect.KClass

public class FSMBuilder<STATE : Any, EVENT : Any> {
    private lateinit var _initialState: STATE
    public val stateFunctions: MutableMap<KClass<out STATE>, Map<KClass<out EVENT>, StateFunction<STATE, EVENT>>> =
        mutableMapOf()

    private var _defaultHandler: (STATE, EVENT) -> STATE = { s, _ -> s }

    public fun initialState(state: STATE) {
        _initialState = state
    }

    public fun defaultHandler(defaultHandler: (STATE, EVENT) -> STATE) {
        _defaultHandler = defaultHandler
    }

    public inline fun <reified S : STATE> state(stateHandlerBuilder: StateHandlerBuilder<STATE, EVENT, S>.() -> Unit) {
        stateFunctions[S::class] = StateHandlerBuilder<STATE, EVENT, S>().apply(stateHandlerBuilder).get()
    }

    internal fun build(): FiniteStateMachine<STATE, EVENT> {
        check(this::_initialState.isInitialized) { "Initial state must be set!" }
        return FiniteStateMachine(_initialState, stateFunctions, _defaultHandler)
    }
}
