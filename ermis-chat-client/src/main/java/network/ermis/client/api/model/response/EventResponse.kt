package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.ChatEventDto

@JsonClass(generateAdapter = true)
internal data class EventResponse(
    val event: ChatEventDto,
    val duration: String,
)
