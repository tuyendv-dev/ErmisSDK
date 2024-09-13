package network.ermis.core.errors

import io.getstream.result.Error

private const val HTTP_BAD_REQUEST = 400

public fun Error.NetworkError.isStatusBadRequest(): Boolean {
    return statusCode == HTTP_BAD_REQUEST
}

public fun Error.NetworkError.isValidationError(): Boolean {
    return serverErrorCode == ChatErrorCode.VALIDATION_ERROR.code
}
