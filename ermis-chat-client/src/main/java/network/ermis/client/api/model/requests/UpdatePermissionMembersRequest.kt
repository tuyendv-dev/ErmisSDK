package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UpdatePermissionMembersRequest(
    @Json(name = "remove_capabilities") val remove_capabilities: List<String>,
    @Json(name = "add_capabilities") val add_capabilities: List<String>,
)
