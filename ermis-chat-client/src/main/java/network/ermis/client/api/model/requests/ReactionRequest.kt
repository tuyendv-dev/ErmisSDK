package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.UpstreamReactionDto

@JsonClass(generateAdapter = true)
internal data class ReactionRequest(
    val reaction: UpstreamReactionDto,
    val enforce_unique: Boolean,
)
