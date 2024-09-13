package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamFlagDto

@JsonClass(generateAdapter = true)
internal data class FlagResponse(
    val flag: DownstreamFlagDto,
)
