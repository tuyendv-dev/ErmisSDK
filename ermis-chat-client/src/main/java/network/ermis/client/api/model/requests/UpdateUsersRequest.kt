package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.UpstreamUserDto

@JsonClass(generateAdapter = true)
internal data class UpdateUsersRequest(
    val users: Map<String, UpstreamUserDto>,
)
