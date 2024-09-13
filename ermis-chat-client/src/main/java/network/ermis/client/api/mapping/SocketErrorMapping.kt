package network.ermis.client.api.mapping

import network.ermis.client.api.model.response.SocketErrorResponse
import network.ermis.client.socket.ErrorDetail
import network.ermis.client.socket.ErrorResponse
import network.ermis.client.socket.SocketErrorMessage

internal fun SocketErrorResponse.toDomain(): SocketErrorMessage {
    return SocketErrorMessage(
        error = error?.toDomain(),
    )
}

internal fun SocketErrorResponse.ErrorResponse.toDomain(): ErrorResponse {
    val dto = this
    return ErrorResponse(
        code = dto.code,
        message = dto.message,
        statusCode = dto.StatusCode,
        exceptionFields = dto.exception_fields,
        moreInfo = dto.more_info,
        details = dto.details.map { it.toDomain() },
    ).apply {
        duration = dto.duration
    }
}

internal fun SocketErrorResponse.ErrorResponse.ErrorDetail.toDomain(): ErrorDetail {
    val dto = this
    return ErrorDetail(
        code = dto.code,
        messages = dto.messages,
    )
}
