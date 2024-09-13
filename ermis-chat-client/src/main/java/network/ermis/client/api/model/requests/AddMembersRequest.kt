package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.UpstreamMessageDto

@JsonClass(generateAdapter = true)
internal data class AddMembersRequest(
    val add_members: List<String>,
    val message: UpstreamMessageDto?,
    val hide_history: Boolean?,
    val skip_push: Boolean?,
)
