package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SendEventRequest(
    val event: Map<Any, Any>,
)
