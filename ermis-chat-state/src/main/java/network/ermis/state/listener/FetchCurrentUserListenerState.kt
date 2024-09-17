package network.ermis.state.listener

import io.getstream.log.taggedLogger
import io.getstream.result.Result
import network.ermis.client.setup.ClientState
import network.ermis.core.models.User
import network.ermis.state.event.handler.model.SelfUserFull
import network.ermis.state.event.handler.utils.updateCurrentUser
import network.ermis.state.plugin.state.global.MutableGlobalState

internal class FetchCurrentUserListenerState(
    private val clientState: ClientState,
    private val globalMutableState: MutableGlobalState,
) : network.ermis.client.plugin.listeners.FetchCurrentUserListener {

    private val logger by taggedLogger("Chat:FetchCurUserLST")

    override suspend fun onFetchCurrentUserResult(result: Result<User>) {
        if (result.isSuccess) {
            logger.d { "[onFetchCurrentUserResult] result: $result" }
            globalMutableState.updateCurrentUser(
                currentUser = clientState.user.value,
                receivedUser = SelfUserFull(result.getOrThrow()),
            )
        }
    }
}
