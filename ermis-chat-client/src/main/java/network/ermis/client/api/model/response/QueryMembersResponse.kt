package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamMemberDto

@JsonClass(generateAdapter = true)
internal data class QueryMembersResponse(
    val members: List<DownstreamMemberDto>,
)
