package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DownstreamChannelDto
import network.ermis.client.api.model.dto.DownstreamChannelUserRead
import network.ermis.client.api.model.dto.DownstreamMemberDto
import network.ermis.client.api.model.dto.DownstreamMessageDto
import network.ermis.client.api.model.dto.DownstreamUserDto
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class ChannelResponse(
    val channel: DownstreamChannelDto,
    val messages: List<DownstreamMessageDto> = emptyList(),
    val members: List<DownstreamMemberDto>? = emptyList(),
    val membership: DownstreamMemberDto?,
    val watchers: List<DownstreamUserDto> = emptyList(),
    val read: List<DownstreamChannelUserRead> = emptyList(),
    val watcher_count: Int = 0,
    val hidden: Boolean?,
    val hide_messages_before: Date?,
) {
    val getMembers: List<DownstreamMemberDto>
        get() = members ?: emptyList()
}
