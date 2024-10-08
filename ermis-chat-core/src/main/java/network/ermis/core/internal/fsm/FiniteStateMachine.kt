package network.ermis.core.internal.fsm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import network.ermis.core.internal.fsm.builder.FSMBuilder
import network.ermis.core.internal.fsm.builder.StateFunction
import kotlin.reflect.KClass

/**
 * This class represents a Finite State Machine. It can be only in one possible state at a time
 * out of the set of possible states [S]. It can handle events from the set [E].
 *
 * @param initialState The initial state.
 * @property stateFunctions A map of states and possible event handlers for them.
 * @property defaultEventHandler Called when [stateFunctions] has no handler for
 *                               a given state/event combination.
 */
public class FiniteStateMachine<S : Any, E : Any>(
    initialState: S,
    private val stateFunctions: Map<KClass<out S>, Map<KClass<out E>, StateFunction<S, E>>>,
    private val defaultEventHandler: (S, E) -> S,
) {
    private val mutex = Mutex()
    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)

    /**
     * The current state as [StateFlow].
     */
    public val stateFlow: StateFlow<S> = _state

    /**
     * The current state.
     */
    public val state: S
        get() = _state.value

    /**
     * Sends an event to the state machine. The entry point to change state.
     */
    public suspend fun sendEvent(event: E) {
        mutex.withLock {
            val currentState = _state.value
            val handler = stateFunctions[currentState::class]?.get(event::class) ?: defaultEventHandler
            _state.value = handler(currentState, event)
        }
    }

    /**
     * Keeps the FSM in its current state.
     * Usually used when handling events that don't need to make a transition.
     *
     * ```kotlin
     * onEvent<SomeEvent> { state, event -> stay() }
     * ```
     *
     * @return the current state value
     */
    public fun stay(): S = state

    public companion object {
        public operator fun <S : Any, E : Any> invoke(builder: FSMBuilder<S, E>.() -> Unit): FiniteStateMachine<S, E> {
            return FSMBuilder<S, E>().apply(builder).build()
        }
    }
}
