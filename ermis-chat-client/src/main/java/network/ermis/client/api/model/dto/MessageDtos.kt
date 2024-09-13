package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass
import network.ermis.core.internal.StreamHandsOff
import java.util.Date

/**
 * See [io.getstream.chat.android.client.parser2.adapters.UpstreamMessageDtoAdapter] for
 * special [extraData] handling.
 */
@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class UpstreamMessageDto(
    val attachments: List<AttachmentDto>,
    val cid: String,
    val command: String?,
    val html: String,
    val id: String,
    val mentioned_users: List<String>?,
    val parent_id: String?,
    val pin_expires: Date?,
    val pinned: Boolean,
    val pinned_at: Date?,
    val pinned_by: UpstreamUserDto?,
    val quoted_message_id: String?,
    val shadowed: Boolean,
    val show_in_channel: Boolean?,
    val silent: Boolean,
    val text: String,
    val thread_participants: List<UpstreamUserDto>,

    val extraData: Map<String, Any>,
) : ExtraDataDto

/**
 * See [io.getstream.chat.android.client.parser2.adapters.DownstreamMessageDtoAdapter] for
 * special [extraData] handling.
 */
@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class DownstreamMessageDto(
    val attachments: List<AttachmentDto>?, // TODO remove this field
    val channel: ChannelInfoDto?,
    val cid: String,
    val command: String?,
    val created_at: Date,
    val deleted_at: Date?,
    val html: String?,
    val i18n: Map<String, String> = emptyMap(),
    val id: String,
    val latest_reactions: List<DownstreamReactionDto>?, // TODO fix it when server api works
    val mentioned_users: List<DownstreamUserDto>?,//  TODO fix it when server api works
    val own_reactions: List<DownstreamReactionDto>?,//  TODO fix it when server api works
    val parent_id: String?,
    val pin_expires: Date?,
    val pinned: Boolean? = false,
    val pinned_at: Date?,
    val pinned_by: DownstreamUserDto?,
    val quoted_message: DownstreamMessageDto?,
    val quoted_message_id: String?,
    val reaction_counts: Map<String, Int>?,
    val reaction_scores: Map<String, Int>?,
    val reply_count: Int?, //  TODO fix it when server api works
    val deleted_reply_count: Int?, //  TODO fix it when server api works
    val shadowed: Boolean? = false,
    val show_in_channel: Boolean? = false,
    val silent: Boolean?,
    val text: String,
    val thread_participants: List<DownstreamUserDto> = emptyList(),
    val type: String,
    val updated_at: Date?,
    val user: DownstreamUserDto,
    val moderation_details: DownstreamModerationDetailsDto? = null,

    val extraData: Map<String, Any>,
) : ExtraDataDto {

    val getOwnReactions: List<DownstreamReactionDto>
        get() = own_reactions ?: emptyList()
    val getLatestReactions: List<DownstreamReactionDto>
        get() = latest_reactions ?: emptyList()
    val getMentionedUsers: List<DownstreamUserDto>
        get() = mentioned_users ?: emptyList()
    val getAttachments: List<AttachmentDto>
        get() = attachments ?: emptyList()
}
