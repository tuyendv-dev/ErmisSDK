package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamUserDto

@JsonClass(generateAdapter = true)
internal data class UsersResponse(
    val data: List<DownstreamUserDto>,
    val page: Int,
    val page_count: Int,
    val count: Int,
)
