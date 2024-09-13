package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SendActionRequest(
    val channel_id: String,
    val message_id: String,
    val type: String,
    val form_data: Map<Any, Any>,
)
