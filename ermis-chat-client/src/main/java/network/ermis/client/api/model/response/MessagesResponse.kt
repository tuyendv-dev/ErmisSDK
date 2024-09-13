package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamMessageDto

@JsonClass(generateAdapter = true)
internal data class MessagesResponse(
    val messages: List<DownstreamMessageDto>,
)
