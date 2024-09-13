package network.ermis.client.socket

import network.ermis.core.errors.ChatErrorCode
import network.ermis.core.errors.extractCause
import network.ermis.core.errors.fromChatErrorCode
import network.ermis.client.events.ChatEvent
import network.ermis.client.parser.ChatParser
import network.ermis.client.utils.TokenUtils.logger
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.recover
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

private const val EVENTS_BUFFER_SIZE = 100
private const val CLOSE_SOCKET_CODE = 1000
private const val CLOSE_SOCKET_REASON = "Connection close by client"

internal class StreamWebSocket(
    private val parser: ChatParser,
    socketCreator: (WebSocketListener) -> WebSocket,
) {
    private val eventFlow = MutableSharedFlow<StreamWebSocketEvent>(extraBufferCapacity = EVENTS_BUFFER_SIZE)

    private val webSocket = socketCreator(object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            logger.i { "Belo [StreamWebSocket] Received message: $text" }
            eventFlow.tryEmit(parseMessage(text))
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            logger.i { "Belo [StreamWebSocket] onFailure message: $response $t"  }

            eventFlow.tryEmit(
                StreamWebSocketEvent.Error(
                    Error.NetworkError.fromChatErrorCode(
                        chatErrorCode = ChatErrorCode.SOCKET_FAILURE,
                        cause = t,
                    ),
                ),
            )
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (code != CLOSE_SOCKET_CODE) {
                // Treat as failure and reconnect, socket shouldn't be closed by server
                eventFlow.tryEmit(
                    StreamWebSocketEvent.Error(
                        Error.NetworkError.fromChatErrorCode(
                            chatErrorCode = ChatErrorCode.SOCKET_CLOSED,
                        ),
                    ),
                )
            }
        }
    })

    fun send(chatEvent: ChatEvent): Boolean = webSocket.send(parser.toJson(chatEvent))
    fun close(): Boolean = webSocket.close(CLOSE_SOCKET_CODE, CLOSE_SOCKET_REASON)
    fun listen(): Flow<StreamWebSocketEvent> = eventFlow.asSharedFlow()

    private fun parseMessage(text: String): StreamWebSocketEvent =
        parser.fromJsonOrError(text, ChatEvent::class.java)
            .map { StreamWebSocketEvent.Message(it) }
            .recover { parseChatError ->
                val errorResponse =
                    when (val chatErrorResult = parser.fromJsonOrError(text, SocketErrorMessage::class.java)) {
                        is Result.Success -> {
                            chatErrorResult.value.error
                        }
                        is Result.Failure -> null
                    }
                StreamWebSocketEvent.Error(
                    errorResponse?.let {
                        Error.NetworkError(
                            message = it.message,
                            statusCode = it.statusCode,
                            serverErrorCode = it.code,
                        )
                    } ?: Error.NetworkError.fromChatErrorCode(
                        chatErrorCode = ChatErrorCode.CANT_PARSE_EVENT,
                        cause = parseChatError.extractCause(),
                    ),
                )
            }.value
}

internal sealed class StreamWebSocketEvent {
    data class Error(val streamError: io.getstream.result.Error) : StreamWebSocketEvent()
    data class Message(val chatEvent: ChatEvent) : StreamWebSocketEvent()
}
