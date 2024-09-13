package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.UpstreamMessageDto

@JsonClass(generateAdapter = true)
internal data class InviteMembersRequest(
    val invites: List<String>,
    val message: UpstreamMessageDto?,
    val skip_push: Boolean?,
)
