package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchMessagesRequest(
    val filter_conditions: Map<*, *>,
    val message_filter_conditions: Map<*, *>,
    val offset: Int?,
    val limit: Int?,
    val next: String?,
    val sort: List<Map<String, Any>>?,
)
