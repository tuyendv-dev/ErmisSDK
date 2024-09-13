package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UsersByIdsRequest(
    val users: List<String>,
    val project_id: String?
)