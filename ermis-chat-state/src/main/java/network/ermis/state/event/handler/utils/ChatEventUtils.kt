package network.ermis.state.event.handler.utils

import network.ermis.client.events.ChatEvent
import network.ermis.client.events.ConnectedEvent
import network.ermis.client.events.MarkAllReadEvent
import network.ermis.client.events.MessageReadEvent
import network.ermis.client.events.NotificationMarkReadEvent
import network.ermis.client.events.NotificationMarkUnreadEvent
import network.ermis.core.models.ChannelUserRead

internal val ChatEvent.realType get() = when (this) {
    is ConnectedEvent -> "connection.connected"
    else -> type
}

internal fun MessageReadEvent.toChannelUserRead() = ChannelUserRead(
    user = user,
    lastReceivedEventDate = createdAt,
    lastRead = createdAt,
// TODO: remove this once the backend is fixed and is sending us the number of unread messages
    unreadMessages = 0,
// TODO: Backend should send us the last read message id
    lastReadMessageId = null,
)
internal fun NotificationMarkReadEvent.toChannelUserRead() = ChannelUserRead(
    user = user,
    lastReceivedEventDate = createdAt,
    lastRead = createdAt,
// TODO: remove this once the backend is fixed and is sending us the number of unread messages
    unreadMessages = 0,
// TODO: Backend should send us the last read message id
    lastReadMessageId = null,
)

internal fun NotificationMarkUnreadEvent.toChannelUserRead() = ChannelUserRead(
    user = user,
    lastReceivedEventDate = createdAt,
    lastRead = lastReadMessageAt,
    unreadMessages = unreadMessages,
    lastReadMessageId = this.lastReadMessageId,
)

internal fun MarkAllReadEvent.toChannelUserRead() = ChannelUserRead(
    user = user,
    lastReceivedEventDate = createdAt,
    lastRead = createdAt,
    unreadMessages = 0,
// TODO: Backend should send us the last read message id
    lastReadMessageId = null,
)
