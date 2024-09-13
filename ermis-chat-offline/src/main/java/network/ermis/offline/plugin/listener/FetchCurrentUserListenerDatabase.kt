package network.ermis.offline.plugin.listener

import network.ermis.client.persistance.UserRepository
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import io.getstream.result.onSuccessSuspend

internal class FetchCurrentUserListenerDatabase(
    private val userRepository: UserRepository,
) : network.ermis.client.plugin.listeners.FetchCurrentUserListener {

    private val logger by taggedLogger("Chat:FetchCurUserLDB")

    override suspend fun onFetchCurrentUserResult(result: Result<User>) {
        result.onSuccessSuspend {
            logger.d { "[onFetchCurrentUserResult] result: $result" }
            userRepository.insertCurrentUser(it)
        }
    }
}
