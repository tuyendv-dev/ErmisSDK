package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamChannelDto
import network.ermis.client.api.model.dto.DownstreamUserDto
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class BannedUserResponse(
    val user: DownstreamUserDto,
    val banned_by: DownstreamUserDto?,
    val channel: DownstreamChannelDto?,
    val created_at: Date?,
    val expires: Date?,
    val shadow: Boolean = false,
    val reason: String?,
)
