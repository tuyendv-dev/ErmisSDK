package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.response.ChannelResponse

@JsonClass(generateAdapter = true)
internal data class QueryChannelsResponse(
    val channels: List<ChannelResponse>,
)
