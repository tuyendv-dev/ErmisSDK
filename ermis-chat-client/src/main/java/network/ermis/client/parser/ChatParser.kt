package network.ermis.client.parser

import network.ermis.core.errors.ChatErrorCode
import network.ermis.core.errors.fromChatErrorCode
import network.ermis.client.socket.ErrorDetail
import network.ermis.client.socket.ErrorResponse
import io.getstream.log.StreamLog
import io.getstream.result.Error
import io.getstream.result.Result
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit

internal interface ChatParser {

    private val tag: String get() = "Chat:ChatParser"

    fun toJson(any: Any): String
    fun <T : Any> fromJson(raw: String, clazz: Class<T>): T
    fun configRetrofit(builder: Retrofit.Builder): Retrofit.Builder

    @Suppress("TooGenericExceptionCaught")
    fun <T : Any> fromJsonOrError(raw: String, clazz: Class<T>): Result<T> {
        return try {
            Result.Success(fromJson(raw, clazz))
        } catch (expected: Throwable) {
            Result.Failure(
                Error.ThrowableError("fromJsonOrError error parsing of $clazz into $raw", expected),
            )
        }
    }

    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
    fun toError(okHttpResponse: Response): Error.NetworkError {
        val statusCode: Int = okHttpResponse.code

        return try {
            // Try to parse default Stream error body
            val body = okHttpResponse.peekBody(Long.MAX_VALUE).string()

            if (body.isEmpty()) {
                Error.NetworkError.fromChatErrorCode(
                    chatErrorCode = ChatErrorCode.NO_ERROR_BODY,
                    statusCode = statusCode,
                )
            } else {
                val error = try {
                    fromJson(body, ErrorResponse::class.java)
                } catch (_: Throwable) {
                    ErrorResponse().apply { message = body }
                }
                Error.NetworkError(
                    serverErrorCode = error.code,
                    message = error.message +
                        moreInfoTemplate(error.moreInfo) +
                        buildDetailsTemplate(error.details),
                    statusCode = statusCode,
                )
            }
        } catch (expected: Throwable) {
            StreamLog.e(tag, expected) { "[toError] failed" }
            Error.NetworkError.fromChatErrorCode(
                chatErrorCode = ChatErrorCode.NETWORK_FAILED,
                cause = expected,
                statusCode = statusCode,
            )
        }
    }

    fun toError(errorResponseBody: ResponseBody): Error.NetworkError {
        return try {
            val errorResponse: ErrorResponse = fromJson(errorResponseBody.string(), ErrorResponse::class.java)
            val (code, message, statusCode, _, moreInfo) = errorResponse

            Error.NetworkError(
                serverErrorCode = code,
                message = message + moreInfoTemplate(moreInfo),
                statusCode = statusCode,
            )
        } catch (expected: Throwable) {
            StreamLog.e(tag, expected) { "[toError] failed" }
            Error.NetworkError.fromChatErrorCode(
                chatErrorCode = ChatErrorCode.NETWORK_FAILED,
                cause = expected,
            )
        }
    }

    private fun moreInfoTemplate(moreInfo: String): String {
        return if (moreInfo.isNotBlank()) {
            "\nMore information available at $moreInfo"
        } else {
            ""
        }
    }

    private fun buildDetailsTemplate(details: List<ErrorDetail>): String {
        return if (details.isNotEmpty()) {
            "\nError details: $details"
        } else {
            ""
        }
    }
}
