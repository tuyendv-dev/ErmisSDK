package network.ermis.core.errors

import io.getstream.result.Error
import io.getstream.result.Error.NetworkError.Companion.UNKNOWN_STATUS_CODE
import java.net.UnknownHostException

/**
 * Represents the error in the SDK.
 */
private const val HTTP_TOO_MANY_REQUESTS = 429
private const val HTTP_TIMEOUT = 408
private const val HTTP_API_ERROR = 500

/**
 * Creates [Error.NetworkError] from [ChatErrorCode] with custom status code and optional cause.
 *
 * @param chatErrorCode The [ChatErrorCode] from which the error should be created.
 * @param statusCode HTTP status code or [UNKNOWN_STATUS_CODE] if not available.
 * @param cause The optional [Throwable] associated with the error.
 *
 * @return [Error.NetworkError] instance.
 */
public fun Error.NetworkError.Companion.fromChatErrorCode(
    chatErrorCode: ChatErrorCode,
    statusCode: Int = UNKNOWN_STATUS_CODE,
    cause: Throwable? = null,
): Error.NetworkError {
    return Error.NetworkError(
        message = chatErrorCode.description,
        serverErrorCode = chatErrorCode.code,
        statusCode = statusCode,
        cause = cause,
    )
}

/**
 * Returns true if an error is a permanent failure instead of a temporary one (broken network, 500, rate limit etc.)
 *
 * A permanent error is an error returned by Stream's API (IE a validation error on the input)
 * Any permanent error will always have a stream error code
 *
 * Temporary errors are retried. Network not being available is a common example of a temporary error.
 *
 * See the error codes here
 * https://getstream.io/chat/docs/api_errors_response/?language=js
 */
public fun Error.isPermanent(): Boolean {
    return if (this is Error.NetworkError) {
        // stream errors are mostly permanent. the exception to this are the rate limit and timeout error
        val temporaryErrors = listOf(HTTP_TOO_MANY_REQUESTS, HTTP_TIMEOUT, HTTP_API_ERROR)

        when {
            statusCode in temporaryErrors -> false
            cause is UnknownHostException -> false
            else -> true
        }
    } else {
        false
    }
}

/**
 * Copies the original [Error] objects with custom message.
 *
 * @param message The message to replace.
 *
 * @return New [Error] instance.
 */
public fun Error.copyWithMessage(message: String): Error {
    return when (this) {
        is Error.GenericError -> this.copy(message = message)
        is Error.NetworkError -> this.copy(message = message)
        is Error.ThrowableError -> this.copy(message = message)
    }
}

/**
 * Extracts the cause from [Error] object or null if it's not available.
 *
 * @return The [Throwable] that is the error's cause or null if not available.
 */
public fun Error.extractCause(): Throwable? {
    return when (this) {
        is Error.GenericError -> null
        is Error.NetworkError -> cause
        is Error.ThrowableError -> cause
    }
}
