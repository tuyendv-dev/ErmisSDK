package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamChannelUserRead
import network.ermis.core.models.ChannelUserRead
import java.util.Date

internal fun DownstreamChannelUserRead.toDomain(lastReceivedEventDate: Date): ChannelUserRead =
    ChannelUserRead(
        user = user.toDomain(),
        lastReceivedEventDate = lastReceivedEventDate,
        lastRead = last_read ?: lastReceivedEventDate,
        unreadMessages = unread_messages,
        lastReadMessageId = last_read_message_id,
    )
