package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class QueryChannelsRequest(
    val filter_conditions: Map<*, *>,
    val offset: Int,
    val limit: Int,
    val sort: List<Map<String, Any>>,
    val message_limit: Int,
    val member_limit: Int,
    val state: Boolean,
    val watch: Boolean,
    val presence: Boolean,
)
