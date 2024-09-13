package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass
import network.ermis.core.internal.StreamHandsOff
import java.util.Date

@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class UpstreamChannelDto(
    val cid: String,
    val id: String,
    val type: String,
    val name: String,
    val description: String?,
    val image: String,
    val watcher_count: Int,
    val frozen: Boolean,
    val last_message_at: Date?,
    val created_at: Date?,
    val deleted_at: Date?,
    val updated_at: Date?,
    val member_count: Int,
    val messages: List<UpstreamMessageDto>,
    val members: List<UpstreamMemberDto>?,
    val watchers: List<UpstreamUserDto>,
    val read: List<UpstreamChannelUserRead>,
    val config: ConfigDto,
    val created_by: UpstreamUserDto,
    val team: String,
    val cooldown: Int,
    val pinned_messages: List<UpstreamMessageDto>,

    val extraData: Map<String, Any>,
) : ExtraDataDto

@JsonClass(generateAdapter = true)
internal data class DownstreamChannelDto(
    val cid: String,
    val id: String,
    val type: String,
    val name: String?,
    val description: String?,
    val image: String?,
    val watcher_count: Int = 0,
    val frozen: Boolean,
    val last_message_at: Date?,
    val created_at: Date?,
    val deleted_at: Date?,
    val updated_at: Date?,
    val member_count: Int = 0,
    val messages: List<DownstreamMessageDto> = emptyList(), // TODO fix it when server api works
    val members: List<DownstreamMemberDto>? = emptyList(),
    val watchers: List<DownstreamUserDto> = emptyList(),
    val read: List<DownstreamChannelUserRead> = emptyList(),
    val config: ConfigDto?,
    val created_by: DownstreamUserDto?,
    val team: String = "",
    val cooldown: Int = 0,
    val pinned_messages: List<DownstreamMessageDto> = emptyList(),
    val own_capabilities: List<String>? = emptyList(),
    val member_capabilities: List<String>? = emptyList(),
    val membership: DownstreamMemberDto?,

    val extraData: Map<String, Any>,
) : ExtraDataDto {
    val getMembers: List<DownstreamMemberDto>
        get() = members ?: emptyList()



}
