@file:Suppress("TooManyFunctions")

package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.ChannelDeletedEventDto
import network.ermis.client.api.model.dto.ChannelHiddenEventDto
import network.ermis.client.api.model.dto.ChannelTruncatedEventDto
import network.ermis.client.api.model.dto.ChannelUpdatedByUserEventDto
import network.ermis.client.api.model.dto.ChannelUpdatedEventDto
import network.ermis.client.api.model.dto.ChannelUserBannedEventDto
import network.ermis.client.api.model.dto.ChannelUserUnbannedEventDto
import network.ermis.client.api.model.dto.ChannelVisibleEventDto
import network.ermis.client.api.model.dto.ChatEventDto
import network.ermis.client.api.model.dto.ConnectedEventDto
import network.ermis.client.api.model.dto.ConnectingEventDto
import network.ermis.client.api.model.dto.DisconnectedEventDto
import network.ermis.client.api.model.dto.ErrorEventDto
import network.ermis.client.api.model.dto.GlobalUserBannedEventDto
import network.ermis.client.api.model.dto.GlobalUserUnbannedEventDto
import network.ermis.client.api.model.dto.HealthEventDto
import network.ermis.client.api.model.dto.MarkAllReadEventDto
import network.ermis.client.api.model.dto.MemberAddedEventDto
import network.ermis.client.api.model.dto.MemberRemovedEventDto
import network.ermis.client.api.model.dto.MemberUpdatedEventDto
import network.ermis.client.api.model.dto.MessageDeletedEventDto
import network.ermis.client.api.model.dto.MessageReadEventDto
import network.ermis.client.api.model.dto.MessageUpdatedEventDto
import network.ermis.client.api.model.dto.NewMessageEventDto
import network.ermis.client.api.model.dto.NotificationAddedToChannelEventDto
import network.ermis.client.api.model.dto.NotificationChannelDeletedEventDto
import network.ermis.client.api.model.dto.NotificationChannelMutesUpdatedEventDto
import network.ermis.client.api.model.dto.NotificationChannelTruncatedEventDto
import network.ermis.client.api.model.dto.NotificationInviteAcceptedEventDto
import network.ermis.client.api.model.dto.NotificationInviteRejectedEventDto
import network.ermis.client.api.model.dto.NotificationInvitedEventDto
import network.ermis.client.api.model.dto.NotificationMarkReadEventDto
import network.ermis.client.api.model.dto.NotificationMarkUnreadEventDto
import network.ermis.client.api.model.dto.NotificationMessageNewEventDto
import network.ermis.client.api.model.dto.NotificationMutesUpdatedEventDto
import network.ermis.client.api.model.dto.NotificationRemovedFromChannelEventDto
import network.ermis.client.api.model.dto.ReactionDeletedEventDto
import network.ermis.client.api.model.dto.ReactionNewEventDto
import network.ermis.client.api.model.dto.ReactionUpdateEventDto
import network.ermis.client.api.model.dto.SignalWebrtcDto
import network.ermis.client.api.model.dto.SignalWebrtcEventDto
import network.ermis.client.api.model.dto.TypingStartEventDto
import network.ermis.client.api.model.dto.TypingStopEventDto
import network.ermis.client.api.model.dto.UnknownEventDto
import network.ermis.client.api.model.dto.UpstreamConnectedEventDto
import network.ermis.client.api.model.dto.UserDeletedEventDto
import network.ermis.client.api.model.dto.UserPresenceChangedEventDto
import network.ermis.client.api.model.dto.UserStartWatchingEventDto
import network.ermis.client.api.model.dto.UserStopWatchingEventDto
import network.ermis.client.api.model.dto.UserUpdatedEventDto
import network.ermis.client.events.ChannelDeletedEvent
import network.ermis.client.events.ChannelHiddenEvent
import network.ermis.client.events.ChannelTruncatedEvent
import network.ermis.client.events.ChannelUpdatedByUserEvent
import network.ermis.client.events.ChannelUpdatedEvent
import network.ermis.client.events.ChannelUserBannedEvent
import network.ermis.client.events.ChannelUserUnbannedEvent
import network.ermis.client.events.ChannelVisibleEvent
import network.ermis.client.events.ChatEvent
import network.ermis.client.events.ConnectedEvent
import network.ermis.client.events.ConnectingEvent
import network.ermis.client.events.DisconnectedEvent
import network.ermis.client.events.ErrorEvent
import network.ermis.client.events.GlobalUserBannedEvent
import network.ermis.client.events.GlobalUserUnbannedEvent
import network.ermis.client.events.HealthEvent
import network.ermis.client.events.MarkAllReadEvent
import network.ermis.client.events.MemberAddedEvent
import network.ermis.client.events.MemberRemovedEvent
import network.ermis.client.events.MemberUpdatedEvent
import network.ermis.client.events.MessageDeletedEvent
import network.ermis.client.events.MessageReadEvent
import network.ermis.client.events.MessageUpdatedEvent
import network.ermis.client.events.NewMessageEvent
import network.ermis.client.events.NotificationAddedToChannelEvent
import network.ermis.client.events.NotificationChannelDeletedEvent
import network.ermis.client.events.NotificationChannelMutesUpdatedEvent
import network.ermis.client.events.NotificationChannelTruncatedEvent
import network.ermis.client.events.NotificationInviteAcceptedEvent
import network.ermis.client.events.NotificationInviteRejectedEvent
import network.ermis.client.events.NotificationInvitedEvent
import network.ermis.client.events.NotificationMarkReadEvent
import network.ermis.client.events.NotificationMarkUnreadEvent
import network.ermis.client.events.NotificationMessageNewEvent
import network.ermis.client.events.NotificationMutesUpdatedEvent
import network.ermis.client.events.NotificationRemovedFromChannelEvent
import network.ermis.client.events.ReactionDeletedEvent
import network.ermis.client.events.ReactionNewEvent
import network.ermis.client.events.ReactionUpdateEvent
import network.ermis.client.events.SignalWebrtcEvent
import network.ermis.client.events.TypingStartEvent
import network.ermis.client.events.TypingStopEvent
import network.ermis.client.events.UnknownEvent
import network.ermis.client.events.UserDeletedEvent
import network.ermis.client.events.UserPresenceChangedEvent
import network.ermis.client.events.UserStartWatchingEvent
import network.ermis.client.events.UserStopWatchingEvent
import network.ermis.client.events.UserUpdatedEvent
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.core.models.CallSignal

internal fun ConnectedEvent.toDto(): UpstreamConnectedEventDto {
    return UpstreamConnectedEventDto(
        type = this.type,
        created_at = createdAt,
        me = me.toDto(),
        connection_id = connectionId,
    )
}

@Suppress("ComplexMethod")
internal fun ChatEventDto.toDomain(): ChatEvent {
    return when (this) {
        is NewMessageEventDto -> toDomain()
        is ChannelDeletedEventDto -> toDomain()
        is ChannelHiddenEventDto -> toDomain()
        is ChannelTruncatedEventDto -> toDomain()
        is ChannelUpdatedByUserEventDto -> toDomain()
        is ChannelUpdatedEventDto -> toDomain()
        is ChannelUserBannedEventDto -> toDomain()
        is ChannelUserUnbannedEventDto -> toDomain()
        is ChannelVisibleEventDto -> toDomain()
        is ConnectedEventDto -> toDomain()
        is ConnectingEventDto -> toDomain()
        is DisconnectedEventDto -> toDomain()
        is ErrorEventDto -> toDomain()
        is GlobalUserBannedEventDto -> toDomain()
        is GlobalUserUnbannedEventDto -> toDomain()
        is HealthEventDto -> toDomain()
        is MarkAllReadEventDto -> toDomain()
        is MemberAddedEventDto -> toDomain()
        is MemberRemovedEventDto -> toDomain()
        is MemberUpdatedEventDto -> toDomain()
        is MessageDeletedEventDto -> toDomain()
        is MessageReadEventDto -> toDomain()
        is MessageUpdatedEventDto -> toDomain()
        is NotificationAddedToChannelEventDto -> toDomain()
        is NotificationChannelDeletedEventDto -> toDomain()
        is NotificationChannelMutesUpdatedEventDto -> toDomain()
        is NotificationChannelTruncatedEventDto -> toDomain()
        is NotificationInviteAcceptedEventDto -> toDomain()
        is NotificationInviteRejectedEventDto -> toDomain()
        is NotificationInvitedEventDto -> toDomain()
        is NotificationMarkReadEventDto -> toDomain()
        is NotificationMarkUnreadEventDto -> toDomain()
        is NotificationMessageNewEventDto -> toDomain()
        is NotificationMutesUpdatedEventDto -> toDomain()
        is NotificationRemovedFromChannelEventDto -> toDomain()
        is ReactionDeletedEventDto -> toDomain()
        is ReactionNewEventDto -> toDomain()
        is ReactionUpdateEventDto -> toDomain()
        is TypingStartEventDto -> toDomain()
        is TypingStopEventDto -> toDomain()
        is UnknownEventDto -> toDomain()
        is UserDeletedEventDto -> toDomain()
        is UserPresenceChangedEventDto -> toDomain()
        is UserStartWatchingEventDto -> toDomain()
        is UserStopWatchingEventDto -> toDomain()
        is UserUpdatedEventDto -> toDomain()
        is SignalWebrtcEventDto -> toDomain()
    }
}

private fun ChannelDeletedEventDto.toDomain(): ChannelDeletedEvent {
    return ChannelDeletedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        channel = channel.toDomain(),
        user = user?.toDomain(),
    )
}

private fun ChannelHiddenEventDto.toDomain(): ChannelHiddenEvent {
    return ChannelHiddenEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        user = user.toDomain(),
        clearHistory = clear_history,
    )
}

private fun ChannelTruncatedEventDto.toDomain(): ChannelTruncatedEvent {
    return ChannelTruncatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        user = user?.toDomain(),
        message = message?.toDomain(),
        channel = channel.toDomain(),
    )
}

private fun ChannelUpdatedEventDto.toDomain(): ChannelUpdatedEvent {
    return ChannelUpdatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        message = message?.toDomain(),
        channel = channel.toDomain(),
    )
}

private fun ChannelUpdatedByUserEventDto.toDomain(): ChannelUpdatedByUserEvent {
    return ChannelUpdatedByUserEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        user = user.toDomain(),
        message = message?.toDomain(),
        channel = channel.toDomain(),
    )
}

private fun ChannelVisibleEventDto.toDomain(): ChannelVisibleEvent {
    return ChannelVisibleEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        user = user.toDomain(),
    )
}

private fun HealthEventDto.toDomain(): HealthEvent {
    return HealthEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        connectionId = connection_id,
    )
}

private fun MemberAddedEventDto.toDomain(): MemberAddedEvent {
    return MemberAddedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        member = member.toDomain(),
    )
}

private fun MemberRemovedEventDto.toDomain(): MemberRemovedEvent {
    return MemberRemovedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        member = member.toDomain(),
    )
}

private fun MemberUpdatedEventDto.toDomain(): MemberUpdatedEvent {
    return MemberUpdatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        member = member.toDomain(),
    )
}

private fun MessageDeletedEventDto.toDomain(): MessageDeletedEvent {
    // TODO review createdAt and deletedAt fields here
    return MessageDeletedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user?.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        message = message.toDomain(),
        hardDelete = hard_delete ?: false,
    )
}

private fun MessageReadEventDto.toDomain(): MessageReadEvent {
    return MessageReadEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
    )
}

private fun MessageUpdatedEventDto.toDomain(): MessageUpdatedEvent {
    return MessageUpdatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        message = message.toDomain(),
    )
}

private fun NewMessageEventDto.toDomain(): NewMessageEvent {
    return NewMessageEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        message = message.toDomain(),
        watcherCount = watcher_count,
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
    )
}

private fun NotificationAddedToChannelEventDto.toDomain(): NotificationAddedToChannelEvent {
    return NotificationAddedToChannelEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        channel = channel.toDomain(),
        member = member.toDomain(),
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
    )
}

private fun NotificationChannelDeletedEventDto.toDomain(): NotificationChannelDeletedEvent {
    return NotificationChannelDeletedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        channel = channel.toDomain(),
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
    )
}

private fun NotificationChannelMutesUpdatedEventDto.toDomain(): NotificationChannelMutesUpdatedEvent {
    return NotificationChannelMutesUpdatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        me = me.toDomain(),
    )
}

private fun NotificationChannelTruncatedEventDto.toDomain(): NotificationChannelTruncatedEvent {
    return NotificationChannelTruncatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        channel = channel.toDomain(),
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
    )
}

private fun NotificationInviteAcceptedEventDto.toDomain(): NotificationInviteAcceptedEvent {
    val (channelType, channelId) = cid.cidToTypeAndId()
    return NotificationInviteAcceptedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type ?: channelType,
        channelId = channel_id ?: channelId,
        user = user?.toDomain() ?: member.user.toDomain(),
        member = member.toDomain(),
        channel = channel.toDomain().copy(membership = member.toDomain()),
    )
}

private fun NotificationInviteRejectedEventDto.toDomain(): NotificationInviteRejectedEvent {
    val (channelType, channelId) = cid.cidToTypeAndId()
    return NotificationInviteRejectedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type ?: channelType,
        channelId = channel_id ?: channelId,
        user = user?.toDomain() ?: member.user.toDomain(),
        member = member.toDomain(),
        channel = channel.toDomain(),
    )
}

private fun NotificationInvitedEventDto.toDomain(): NotificationInvitedEvent {
    return NotificationInvitedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        user = user.toDomain(),
        member = member.toDomain(),
    )
}

private fun NotificationMarkReadEventDto.toDomain(): NotificationMarkReadEvent {
    return NotificationMarkReadEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
    )
}

private fun NotificationMarkUnreadEventDto.toDomain(): NotificationMarkUnreadEvent {
    return NotificationMarkUnreadEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
        firstUnreadMessageId = first_unread_message_id,
        lastReadMessageId = last_read_message_id,
        lastReadMessageAt = last_read_at.date,
        unreadMessages = unread_messages,
    )
}

private fun MarkAllReadEventDto.toDomain(): MarkAllReadEvent {
    return MarkAllReadEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
    )
}

private fun NotificationMessageNewEventDto.toDomain(): NotificationMessageNewEvent {
    return NotificationMessageNewEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        channel = channel.toDomain(),
        message = message.toDomain(),
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
    )
}

private fun NotificationMutesUpdatedEventDto.toDomain(): NotificationMutesUpdatedEvent {
    return NotificationMutesUpdatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        me = me.toDomain(),
    )
}

private fun NotificationRemovedFromChannelEventDto.toDomain(): NotificationRemovedFromChannelEvent {
    return NotificationRemovedFromChannelEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user?.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        channel = channel.toDomain(),
        member = member.toDomain(),
    )
}

private fun ReactionDeletedEventDto.toDomain(): ReactionDeletedEvent {
    return ReactionDeletedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        message = message.toDomain(),
        reaction = reaction.toDomain(),
    )
}

private fun ReactionNewEventDto.toDomain(): ReactionNewEvent {
    return ReactionNewEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        message = message.toDomain(),
        reaction = reaction.toDomain(),
    )
}

private fun ReactionUpdateEventDto.toDomain(): ReactionUpdateEvent {
    return ReactionUpdateEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        message = message.toDomain(),
        reaction = reaction.toDomain(),
    )
}

private fun TypingStartEventDto.toDomain(): TypingStartEvent {
    return TypingStartEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        parentId = parent_id,
    )
}

private fun TypingStopEventDto.toDomain(): TypingStopEvent {
    return TypingStopEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        parentId = parent_id,
    )
}

private fun ChannelUserBannedEventDto.toDomain(): ChannelUserBannedEvent {
    return ChannelUserBannedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
        user = user.toDomain(),
        expiration = expiration,
        shadow = shadow ?: false,
    )
}

private fun GlobalUserBannedEventDto.toDomain(): GlobalUserBannedEvent {
    return GlobalUserBannedEvent(
        type = type,
        user = user.toDomain(),
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
    )
}

private fun UserDeletedEventDto.toDomain(): UserDeletedEvent {
    return UserDeletedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
    )
}

private fun UserPresenceChangedEventDto.toDomain(): UserPresenceChangedEvent {
    return UserPresenceChangedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
    )
}

private fun UserStartWatchingEventDto.toDomain(): UserStartWatchingEvent {
    return UserStartWatchingEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        watcherCount = watcher_count,
        channelType = channel_type,
        channelId = channel_id,
        user = user.toDomain(),
    )
}

private fun UserStopWatchingEventDto.toDomain(): UserStopWatchingEvent {
    return UserStopWatchingEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        watcherCount = watcher_count,
        channelType = channel_type,
        channelId = channel_id,
        user = user.toDomain(),
    )
}

private fun ChannelUserUnbannedEventDto.toDomain(): ChannelUserUnbannedEvent {
    return ChannelUserUnbannedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
        cid = cid,
        channelType = channel_type,
        channelId = channel_id,
    )
}

private fun GlobalUserUnbannedEventDto.toDomain(): GlobalUserUnbannedEvent {
    return GlobalUserUnbannedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
    )
}

private fun UserUpdatedEventDto.toDomain(): UserUpdatedEvent {
    return UserUpdatedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user.toDomain(),
    )
}

private fun ConnectedEventDto.toDomain(): ConnectedEvent {
    return ConnectedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        me = me.toDomain(),
        connectionId = connection_id,
    )
}

private fun ConnectingEventDto.toDomain(): ConnectingEvent {
    return ConnectingEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
    )
}

private fun DisconnectedEventDto.toDomain(): DisconnectedEvent {
    return DisconnectedEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
    )
}

private fun ErrorEventDto.toDomain(): ErrorEvent {
    return ErrorEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        error = error,
    )
}

private fun SignalWebrtcDto.toDomain(): CallSignal {
    return CallSignal(
        type = type,
        sdp = sdp
    )
}
private fun SignalWebrtcEventDto.toDomain(): SignalWebrtcEvent {
    return SignalWebrtcEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        cid = cid,
        action = action,
        userId = user_id,
        signal = signal.toDomain(),
    )
}

private fun UnknownEventDto.toDomain(): UnknownEvent {
    return UnknownEvent(
        type = type,
        createdAt = created_at.date,
        rawCreatedAt = created_at.rawDate,
        user = user?.toDomain(),
        rawData = rawData,
    )
}
