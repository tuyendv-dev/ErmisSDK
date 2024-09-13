package network.ermis.client.clientstate

import network.ermis.core.internal.fsm.FiniteStateMachine
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import kotlinx.coroutines.flow.StateFlow

internal class UserStateService {
    private val logger by taggedLogger("Chat:UserStateService")

    suspend fun onUserUpdated(user: User) {
        logger.d { "[onUserUpdated] user.id: '${user.id}'" }
        fsm.sendEvent(UserStateEvent.UserUpdated(user))
    }

    suspend fun onSetUser(user: User, isAnonymous: Boolean) {
        logger.i { "[onSetUser] user.id: '${user.id}', isAnonymous: $isAnonymous" }
        if (isAnonymous) {
            fsm.sendEvent(UserStateEvent.ConnectAnonymous(user))
        } else {
            fsm.sendEvent(UserStateEvent.ConnectUser(user))
        }
    }

    suspend fun onLogout() {
        logger.i { "[onLogout] no args" }
        fsm.sendEvent(UserStateEvent.UnsetUser)
    }

    suspend fun onSocketUnrecoverableError() {
        logger.e { "[onSocketUnrecoverableError] no args" }
        fsm.sendEvent(UserStateEvent.UnsetUser)
    }

    internal val state: UserState
        get() = fsm.state

    internal val stateFlow: StateFlow<UserState>
        get() = fsm.stateFlow

    private val fsm = FiniteStateMachine<UserState, UserStateEvent> {
        defaultHandler { state, event ->
            logger.e { "Can't handle $event while being in state ${state::class.simpleName}" }
            state
        }
        initialState(UserState.NotSet)
        state<UserState.NotSet> {
            onEvent<UserStateEvent.ConnectUser> { event -> UserState.UserSet(event.user) }
            onEvent<UserStateEvent.ConnectAnonymous> { event -> UserState.AnonymousUserSet(event.user) }
            onEvent<UserStateEvent.UnsetUser> { state }
        }
        state<UserState.UserSet> {
            onEvent<UserStateEvent.UserUpdated> { event -> UserState.UserSet(event.user) }
            onEvent<UserStateEvent.UnsetUser> { UserState.NotSet }
        }
        state<UserState.AnonymousUserSet> {
            onEvent<UserStateEvent.UserUpdated> { event -> UserState.AnonymousUserSet(event.user) }
            onEvent<UserStateEvent.UnsetUser> { UserState.NotSet }
        }
    }

    private sealed class UserStateEvent {
        data class ConnectUser(val user: User) : UserStateEvent()
        data class UserUpdated(val user: User) : UserStateEvent()
        data class ConnectAnonymous(val user: User) : UserStateEvent()
        object UnsetUser : UserStateEvent() { override fun toString(): String = "UnsetUser" }
    }
}
