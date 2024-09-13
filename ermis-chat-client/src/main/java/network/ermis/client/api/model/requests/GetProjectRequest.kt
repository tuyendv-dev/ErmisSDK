package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetProjectRequest(
    @Json(name = "client_id") val client_id: String,
    @Json(name = "chain_id") val chain_id: String,
)
