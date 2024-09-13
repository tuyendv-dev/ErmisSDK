package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class JoinProjectRequest(
    @Json(name = "project_id") val project_id: String
)