package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.UpstreamMemberDto

@JsonClass(generateAdapter = true)
internal data class QueryMembersRequest(
    val type: String,
    val id: String,
    val filter_conditions: Map<*, *>,
    val offset: Int,
    val limit: Int,
    val sort: List<Map<String, Any>>,
    val members: List<UpstreamMemberDto>,
)
