package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CompletableResponse(
    val duration: String?,
)
