package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.response.BannedUserResponse

@JsonClass(generateAdapter = true)
internal data class QueryBannedUsersResponse(
    val bans: List<BannedUserResponse>,
)
