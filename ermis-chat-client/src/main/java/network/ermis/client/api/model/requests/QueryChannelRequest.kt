package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class QueryChannelRequest(
    val state: Boolean,
    val watch: Boolean,
    val presence: Boolean,
    val messages: Map<String, Any>,
    val watchers: Map<String, Any>,
    val members: Map<String, Any>,
    val data: Map<String, Any>,
    val project_id: String? = null
)
