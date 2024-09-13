package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class HideChannelRequest(
    val clear_history: Boolean,
)
