package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamUserDto

@JsonClass(generateAdapter = true)
internal data class TokenResponse(
    val user: DownstreamUserDto,
    val access_token: String,
)
