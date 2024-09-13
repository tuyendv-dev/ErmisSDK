
package network.ermis.sample.feature.channel.list

import network.ermis.client.ErmisClient
import network.ermis.client.events.ChannelUpdatedByUserEvent
import network.ermis.client.events.ChannelUpdatedEvent
import network.ermis.client.events.CidEvent
import network.ermis.client.events.HasChannel
import network.ermis.client.events.MemberAddedEvent
import network.ermis.client.events.NotificationAddedToChannelEvent
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.state.event.chat.DefaultChatEventHandler
import network.ermis.state.event.chat.EventHandlingResult
import network.ermis.state.event.chat.factory.ChatEventHandlerFactory
import network.ermis.sample.common.isDraft
import kotlinx.coroutines.flow.StateFlow

class CustomChatEventHandlerFactory : ChatEventHandlerFactory() {
    override fun chatEventHandler(channels: StateFlow<Map<String, Channel>?>) = CustomChatEventHandler(channels)
}

class CustomChatEventHandler(channels: StateFlow<Map<String, Channel>?>) :
    DefaultChatEventHandler(channels, ErmisClient.instance().clientState) {

    override fun handleCidEvent(event: CidEvent, filter: FilterObject, cachedChannel: Channel?): EventHandlingResult {
        return when (event) {
            is MemberAddedEvent -> addIfCurrentUserJoinedAndChannelIsNotDraft(cachedChannel, event.member)
            else -> super.handleCidEvent(event, filter, cachedChannel)
        }
    }

    override fun handleChannelEvent(event: HasChannel, filter: FilterObject): EventHandlingResult {
        return when (event) {
            is NotificationAddedToChannelEvent -> watchIfChannelIsNotDraft(event.channel)
            is ChannelUpdatedEvent -> handleChannelUpdate(event.channel)
            is ChannelUpdatedByUserEvent -> handleChannelUpdate(event.channel)
            else -> super.handleChannelEvent(event, filter)
        }
    }

    private fun addIfCurrentUserJoinedAndChannelIsNotDraft(channel: Channel?, member: Member): EventHandlingResult {
        return if (isChannelNullOrDraft(channel)) {
            EventHandlingResult.Skip
        } else {
            addIfCurrentUserJoinedChannel(channel, member)
        }
    }

    private fun watchIfChannelIsNotDraft(channel: Channel): EventHandlingResult {
        return if (isChannelNullOrDraft(channel)) {
            EventHandlingResult.Skip
        } else {
            EventHandlingResult.WatchAndAdd(channel.cid)
        }
    }

    private fun addIfChannelIsNotDraft(channel: Channel?): EventHandlingResult {
        return if (isChannelNullOrDraft(channel)) {
            EventHandlingResult.Skip
        } else {
            addIfChannelIsAbsent(channel)
        }
    }

    private fun isChannelNullOrDraft(channel: Channel?) = channel == null || channel.isDraft

    private fun handleChannelUpdate(channel: Channel): EventHandlingResult {
        val hasMember = channel.members.any { member ->
            ErmisClient.instance().getCurrentUser()?.id == member.getUserId()
        }

        return if (hasMember) {
            addIfChannelIsNotDraft(channel)
        } else {
            removeIfChannelExists(channel.cid)
        }
    }
}
