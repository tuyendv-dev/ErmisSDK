package network.ermis.offline.repository.domain.channel.internal

import network.ermis.client.utils.extensions.internal.lastMessage
import network.ermis.client.utils.extensions.syncUnreadCountWithReads
import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelUserRead
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.offline.repository.domain.channel.member.MemberEntity
import network.ermis.offline.repository.domain.channel.member.toEntity
import network.ermis.offline.repository.domain.channel.member.toModel
import network.ermis.offline.repository.domain.channel.userread.ChannelUserReadEntity
import network.ermis.offline.repository.domain.channel.userread.toEntity
import network.ermis.offline.repository.domain.channel.userread.toModel

internal fun Channel.toEntity(): ChannelEntity {
    return ChannelEntity(
        type = type,
        channelId = id,
        name = name,
        description = description,
        image = image,
        cooldown = cooldown,
        frozen = frozen,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        extraData = extraData,
        syncStatus = syncStatus,
        hidden = hidden,
        hideMessagesBefore = hiddenMessagesBefore,
        members = members.map(Member::toEntity).associateBy(MemberEntity::userId).toMutableMap(),
        memberCount = memberCount,
        reads = read.map(ChannelUserRead::toEntity).associateBy(ChannelUserReadEntity::userId).toMutableMap(),
        lastMessageId = lastMessage?.id,
        lastMessageAt = lastMessageAt,
        createdByUserId = createdBy.id,
        watcherIds = watchers.map(User::id),
        watcherCount = watcherCount,
        team = team,
        ownCapabilities = ownCapabilities,
        memberCapabilities = memberCapabilities,
        membership = membership?.toEntity(),
    )
}

internal suspend fun ChannelEntity.toModel(
    getUser: suspend (userId: String) -> User,
    getMessage: suspend (messageId: String) -> Message?,
): Channel = Channel(
    cooldown = cooldown,
    type = type,
    id = channelId,
    name = name,
    description = description,
    image = image,
    frozen = frozen,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    extraData = extraData.toMutableMap(),
    lastMessageAt = lastMessageAt,
    syncStatus = syncStatus,
    hidden = hidden,
    hiddenMessagesBefore = hideMessagesBefore,
    members = members.values.map { it.toModel(getUser) },
    memberCount = memberCount,
    messages = listOfNotNull(lastMessageId?.let { getMessage(it) }),
    read = reads.values.map { it.toModel(getUser) },
    createdBy = getUser(createdByUserId),
    watchers = watcherIds.map { getUser(it) },
    watcherCount = watcherCount,
    team = team,
    ownCapabilities = ownCapabilities,
    memberCapabilities = memberCapabilities,
    membership = membership?.toModel(getUser),
).syncUnreadCountWithReads()
