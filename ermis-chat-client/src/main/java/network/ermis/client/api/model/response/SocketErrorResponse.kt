package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.core.internal.StreamHandsOff

@JsonClass(generateAdapter = true)
internal data class SocketErrorResponse(
    val error: ErrorResponse? = null,
) {

    @StreamHandsOff(
        reason = "Field `StatusCode` name is right, even when it doesn't follow camelCase nor snake_case rules",
    )
    @JsonClass(generateAdapter = true)
    data class ErrorResponse(
        val code: Int = -1,
        val message: String = "",
        val StatusCode: Int = -1,
        val duration: String = "",
        val exception_fields: Map<String, String> = mapOf(),
        val more_info: String = "",
        val details: List<ErrorDetail> = emptyList(),
    ) {

        @JsonClass(generateAdapter = true)
        data class ErrorDetail(
            val code: Int = -1,
            val messages: List<String> = emptyList(),
        )
    }
}
