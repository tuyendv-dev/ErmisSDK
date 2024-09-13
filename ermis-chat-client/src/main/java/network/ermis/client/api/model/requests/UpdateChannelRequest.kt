package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.UpstreamMessageDto

@JsonClass(generateAdapter = true)
internal data class UpdateChannelRequest(
    val data: Map<String, Any>,
    val message: UpstreamMessageDto?,
)
