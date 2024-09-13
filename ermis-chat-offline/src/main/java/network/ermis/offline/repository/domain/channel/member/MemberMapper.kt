package network.ermis.offline.repository.domain.channel.member

import network.ermis.offline.repository.domain.channel.member.MemberEntity
import network.ermis.core.models.Member
import network.ermis.core.models.User

internal fun Member.toEntity(): MemberEntity = MemberEntity(
    userId = getUserId(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isInvited = isInvited ?: false,
    inviteAcceptedAt = inviteAcceptedAt,
    inviteRejectedAt = inviteRejectedAt,
    shadowBanned = shadowBanned ?: false,
    banned = banned,
    channelRole = channelRole,
)

internal suspend fun MemberEntity.toModel(getUser: suspend (userId: String) -> User): Member = Member(
    user = getUser(userId),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isInvited = isInvited,
    inviteAcceptedAt = inviteAcceptedAt,
    inviteRejectedAt = inviteRejectedAt,
    shadowBanned = shadowBanned ?: false,
    banned = banned,
    channelRole = channelRole,
)
