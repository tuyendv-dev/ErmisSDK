package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BanMembersRequest(
    @Json(name = "ban_members") val ban_members: List<String>,
)
