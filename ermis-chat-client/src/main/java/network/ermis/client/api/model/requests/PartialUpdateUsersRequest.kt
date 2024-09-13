package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.PartialUpdateUserDto

@JsonClass(generateAdapter = true)
internal data class PartialUpdateUsersRequest(val users: List<PartialUpdateUserDto>)
