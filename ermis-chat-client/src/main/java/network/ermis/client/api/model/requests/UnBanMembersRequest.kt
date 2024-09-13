package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UnBanMembersRequest(
    @Json(name = "unban_members") val unban_members: List<String>,
)
