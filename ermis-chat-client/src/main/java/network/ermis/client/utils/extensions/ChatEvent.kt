package network.ermis.client.utils.extensions

import network.ermis.client.events.ChannelTruncatedEvent
import network.ermis.client.events.ChannelUpdatedByUserEvent
import network.ermis.client.events.ChannelUpdatedEvent
import network.ermis.client.events.ChatEvent
import network.ermis.client.events.MessageDeletedEvent
import network.ermis.client.events.MessageUpdatedEvent
import network.ermis.client.events.NewMessageEvent
import network.ermis.client.events.NotificationMessageNewEvent
import network.ermis.client.events.ReactionDeletedEvent
import network.ermis.client.events.ReactionNewEvent
import network.ermis.client.events.ReactionUpdateEvent

public fun ChatEvent.enrichIfNeeded(): ChatEvent = when (this) {
    is NewMessageEvent -> copy(message = message.enrichWithCid(cid))
    is MessageDeletedEvent -> copy(message = message.enrichWithCid(cid))
    is MessageUpdatedEvent -> copy(message = message.enrichWithCid(cid))
    is ReactionNewEvent -> copy(message = message.enrichWithCid(cid))
    is ReactionUpdateEvent -> copy(message = message.enrichWithCid(cid))
    is ReactionDeletedEvent -> copy(message = message.enrichWithCid(cid))
    is ChannelUpdatedEvent -> copy(message = message?.enrichWithCid(cid))
    is ChannelTruncatedEvent -> copy(message = message?.enrichWithCid(cid))
    is ChannelUpdatedByUserEvent -> copy(message = message?.enrichWithCid(cid))
    is NotificationMessageNewEvent -> copy(message = message.enrichWithCid(cid))
    else -> this
}
