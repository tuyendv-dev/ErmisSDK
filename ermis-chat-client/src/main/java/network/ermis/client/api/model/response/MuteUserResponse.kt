package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamMuteDto
import network.ermis.client.api.model.dto.DownstreamUserDto

@JsonClass(generateAdapter = true)
internal data class MuteUserResponse(
    val mute: DownstreamMuteDto,
    val own_user: DownstreamUserDto,
)
