package network.ermis.offline.repository.domain.channel.userread

import network.ermis.offline.repository.domain.channel.userread.ChannelUserReadEntity
import network.ermis.core.models.ChannelUserRead
import network.ermis.core.models.User

internal fun ChannelUserRead.toEntity(): ChannelUserReadEntity =
    ChannelUserReadEntity(getUserId(), lastReceivedEventDate, unreadMessages, lastRead, lastReadMessageId)

internal suspend fun ChannelUserReadEntity.toModel(getUser: suspend (userId: String) -> User): ChannelUserRead =
    ChannelUserRead(getUser(userId), lastReceivedEventDate, unreadMessages, lastRead, lastReadMessageId)
