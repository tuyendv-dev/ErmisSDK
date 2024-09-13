package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamMemberDto
import network.ermis.client.api.model.dto.UpstreamMemberDto
import network.ermis.core.models.Member

internal fun DownstreamMemberDto.toDomain(): Member =
    Member(
        user = user.toDomain(),
        createdAt = created_at,
        updatedAt = updated_at,
        isInvited = invited,
        inviteAcceptedAt = invite_accepted_at,
        inviteRejectedAt = invite_rejected_at,
        shadowBanned = shadow_banned,
        banned = banned,
        channelRole = channel_role,
    )

internal fun Member.toDto(): UpstreamMemberDto =
    UpstreamMemberDto(
        user = user.toDto(),
        created_at = createdAt,
        updated_at = updatedAt,
        invited = isInvited,
        invite_accepted_at = inviteAcceptedAt,
        invite_rejected_at = inviteRejectedAt,
        shadow_banned = shadowBanned ?: false,
        banned = banned,
        channel_role = channelRole,
    )
