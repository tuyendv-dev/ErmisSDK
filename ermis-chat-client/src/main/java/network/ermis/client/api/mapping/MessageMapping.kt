package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.AttachmentDto
import network.ermis.client.api.model.dto.DownstreamMessageDto
import network.ermis.client.api.model.dto.DownstreamReactionDto
import network.ermis.client.api.model.dto.DownstreamUserDto
import network.ermis.client.api.model.dto.UpstreamMessageDto
import network.ermis.core.models.Attachment
import network.ermis.core.models.Message
import network.ermis.core.models.User

internal fun Message.toDto(): UpstreamMessageDto =
    UpstreamMessageDto(
        attachments = attachments.map(Attachment::toDto),
        cid = cid,
        command = command,
        html = html,
        id = id,
        mentioned_users = mentionedUsersIds,
        parent_id = parentId,
        pin_expires = pinExpires,
        pinned = pinned,
        pinned_at = pinnedAt,
        pinned_by = pinnedBy?.toDto(),
        quoted_message_id = replyMessageId,
        shadowed = shadowed,
        show_in_channel = showInChannel,
        silent = silent,
        text = text,
        thread_participants = threadParticipants.map(User::toDto),
        extraData = extraData,
    )

internal fun DownstreamMessageDto.toDomain(): Message =
    Message(
        attachments = getAttachments.mapTo(mutableListOf(), AttachmentDto::toDomain),
        channelInfo = channel?.toDomain(),
        cid = cid,
        command = command,
        createdAt = created_at,
        deletedAt = deleted_at,
        html = html ?: text,
        i18n = i18n,
        id = id,
        latestReactions = getLatestReactions.mapTo(mutableListOf(), DownstreamReactionDto::toDomain),
        mentionedUsers = getMentionedUsers.mapTo(mutableListOf(), DownstreamUserDto::toDomain),
        ownReactions = getOwnReactions.mapTo(mutableListOf(), DownstreamReactionDto::toDomain),
        parentId = parent_id,
        pinExpires = pin_expires,
        pinned = pinned ?: false,
        pinnedAt = pinned_at,
        pinnedBy = pinned_by?.toDomain(),
        reactionCounts = reaction_counts.orEmpty().toMutableMap(),
        reactionScores = reaction_scores.orEmpty().toMutableMap(),
        replyCount = reply_count ?: 0,
        deletedReplyCount = deleted_reply_count ?: 0,
        replyMessageId = quoted_message_id,
        replyTo = quoted_message?.toDomain(),
        shadowed = shadowed ?: false,
        showInChannel = show_in_channel ?: true,
        silent = silent ?: false,
        text = text,
        threadParticipants = thread_participants.map(DownstreamUserDto::toDomain),
        type = type,
        updatedAt = updated_at,
        user = user.toDomain(),
        moderationDetails = moderation_details?.toDomain(),
        extraData = extraData.toMutableMap(),
    )
