package network.ermis.client.user

import network.ermis.client.api.ErmisClientConfig
import network.ermis.core.errors.ChatErrorCode
import network.ermis.client.events.ConnectedEvent
import network.ermis.client.network.NetworkStateProvider
import network.ermis.client.socket.SocketFactory
import network.ermis.client.socket.SocketFactory.ConnectionConf
import network.ermis.client.socket.SocketFactory.ConnectionConf.AnonymousConnectionConf
import network.ermis.client.socket.SocketFactory.ConnectionConf.UserConnectionConf
import network.ermis.client.socket.StreamWebSocket
import network.ermis.client.socket.StreamWebSocketEvent
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeoutOrNull
import java.io.UnsupportedEncodingException

/**
 * Fetches the current user from the backend.
 */
internal class CurrentUserFetcher(
    private val networkStateProvider: NetworkStateProvider,
    private val socketFactory: SocketFactory,
    private val config: ErmisClientConfig,
) {

    private val logger by taggedLogger("Chat:CurrentUserFetcher")

    suspend fun fetch(currentUser: User): Result<User> {
        logger.d { "[fetch] no args" }
        if (!networkStateProvider.isConnected()) {
            logger.w { "[fetch] rejected (no internet connection)" }
            return Result.Failure(ChatErrorCode.NETWORK_FAILED.toNetworkError())
        }
        var ws: StreamWebSocket? = null
        return try {
            ws = socketFactory.createSocket(currentUser.toConnectionConf(config))
            ws.listen().firstUserWithTimeout(TIMEOUT_MS).also {
                logger.v { "[fetch] completed: $it" }
            }
        } catch (e: UnsupportedEncodingException) {
            logger.e { "[fetch] failed: $e" }
            Result.Failure(Error.ThrowableError(e.message.orEmpty(), e))
        } finally {
            try {
                ws?.close()
            } catch (_: IllegalArgumentException) {
                // no-op
            }
        }
    }

    private fun User.toConnectionConf(config: ErmisClientConfig): ConnectionConf = when (config.isAnonymous) {
        true -> AnonymousConnectionConf(config.wssUrl, config.apiKey, this)
        false -> UserConnectionConf(config.wssUrl, config.apiKey, this)
    }.asReconnectionConf()

    private suspend fun Flow<StreamWebSocketEvent>.firstUserWithTimeout(
        timeMillis: Long,
    ): Result<User> = withTimeoutOrNull(timeMillis) {
        mapNotNull {
            when (it) {
                is StreamWebSocketEvent.Error -> Result.Failure(it.streamError)
                is StreamWebSocketEvent.Message -> (it.chatEvent as? ConnectedEvent)?.let { Result.Success(it.me) }
            }
        }
            .first()
    } ?: Result.Failure(Error.GenericError("Timeout while fetching current user"))

    private fun ChatErrorCode.toNetworkError() = Error.NetworkError(
        message = description,
        serverErrorCode = code,
    )

    private companion object {
        private const val TIMEOUT_MS = 15_000L
    }
}
