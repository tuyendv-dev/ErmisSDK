package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamReactionDto

@JsonClass(generateAdapter = true)
internal data class ReactionResponse(
    val reaction: DownstreamReactionDto,
)
