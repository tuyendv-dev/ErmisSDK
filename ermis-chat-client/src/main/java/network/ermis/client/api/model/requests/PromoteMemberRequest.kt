package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PromoteMemberRequest(
    @Json(name = "promote_members") val promote_members: List<String>,
)
