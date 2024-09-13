package network.ermis.state.event.chat

import network.ermis.client.events.ChatEvent
import network.ermis.client.events.CidEvent
import network.ermis.client.events.HasChannel
import network.ermis.client.events.MemberAddedEvent
import network.ermis.client.events.MemberRemovedEvent
import network.ermis.client.events.NewMessageEvent
import network.ermis.client.events.NotificationAddedToChannelEvent
import network.ermis.client.events.NotificationInviteAcceptedEvent
import network.ermis.client.events.NotificationMessageNewEvent
import network.ermis.client.events.NotificationRemovedFromChannelEvent
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import kotlinx.coroutines.flow.StateFlow

/**
 * Default implementation of [ChatEventHandler] which is based on the current user membership.
 *
 * @param channels The map of visible channels.
 * @param clientState The client state used to obtain current user.
 */
public open class DefaultChatEventHandler(
    protected val channels: StateFlow<Map<String, Channel>?>,
    protected val clientState: ClientState,
) : BaseChatEventHandler() {

    /**
     * Handles additional events:
     * - [NewMessageEvent] - adds the channel to the set if its not a system message.
     * - [MemberRemovedEvent] - removes the channel from the set if a current user left.
     * - [MemberAddedEvent] - adds the channel to the set if a current user was added.
     *
     * @see [BaseChatEventHandler.handleCidEvent]
     *
     * @param event [ChatEvent] that may contain updates for the set of channels.
     * @param filter [FilterObject] associated with the set of channels. Can be used to define the result of handling.
     * @param cachedChannel Optional cached [Channel] object if exists.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    override fun handleCidEvent(event: CidEvent, filter: FilterObject, cachedChannel: Channel?): EventHandlingResult {
        return when (event) {
            is NewMessageEvent -> handleNewMessageEvent(event, cachedChannel)
            is MemberRemovedEvent -> removeIfCurrentUserLeftChannel(event.cid, event.member)
            is MemberAddedEvent -> addIfCurrentUserJoinedChannel(cachedChannel, event.member)
            else -> super.handleCidEvent(event, filter, cachedChannel)
        }
    }

    /**
     * Handles additional events:
     * - [NotificationMessageNewEvent] - calls watch and adds the channel to the set.
     * - [NotificationRemovedFromChannelEvent] - removes the channel from the set if a current user left.
     * - [NotificationAddedToChannelEvent] - calls watch and adds the channel to the set if a current user was added.
     *
     * @param event [ChatEvent] that may contain updates for the set of channels.
     * @param filter [FilterObject] associated with the set of channels. Can be used to define the result of handling.

     * @return [EventHandlingResult] Result of handling.
     */
    override fun handleChannelEvent(event: HasChannel, filter: FilterObject): EventHandlingResult {
        return when (event) {
            is NotificationMessageNewEvent -> EventHandlingResult.WatchAndAdd(event.cid)
            is NotificationAddedToChannelEvent -> EventHandlingResult.WatchAndAdd(event.cid)
            is NotificationRemovedFromChannelEvent -> removeIfCurrentUserLeftChannel(event.cid, event.member)
            is NotificationInviteAcceptedEvent -> addIfCurrentUserInviteAcceptedChannel(event.channel, event.member)
            else -> super.handleChannelEvent(event, filter)
        }
    }

    // TODO tuyendv show the channel after accepting the invitation
    private fun addIfCurrentUserInviteAcceptedChannel(channel: Channel, member: Member): EventHandlingResult {
        return if (clientState.user.value?.id == member.getUserId()) {
            EventHandlingResult.WatchAndAdd(channel.cid)
        } else {
            EventHandlingResult.Skip
        }
    }

    /**
     * Checks if the message is a system message.
     * If yes it skips the event. Otherwise, it adds the channel cached channel exists and was not added yet.
     *
     * @param event [NewMessageEvent] that may contain updates for the set of channels.
     * @param cachedChannel Optional cached [Channel] object if exists.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    private fun handleNewMessageEvent(event: NewMessageEvent, cachedChannel: Channel?): EventHandlingResult {
        return if (event.message.type == SYSTEM_MESSAGE) {
            EventHandlingResult.Skip
        } else {
            addIfChannelIsAbsent(cachedChannel)
        }
    }

    /**
     * Checks if the current user has left the channel and the channel is visible.
     * If yes then it removes it. Otherwise, it simply skips the event.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param member The member who left the channel.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    private fun removeIfCurrentUserLeftChannel(cid: String, member: Member): EventHandlingResult {
        return if (member.getUserId() != clientState.user.value?.id) {
            EventHandlingResult.Skip
        } else {
            removeIfChannelExists(cid)
        }
    }

    /**
     * Checks if the channel with given id is visible.
     * If yes then it removes it. Otherwise, it simply skips the event.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     *
     * @return [EventHandlingResult] Result of handling.
     */
    protected fun removeIfChannelExists(cid: String): EventHandlingResult {
        val channelsMap = channels.value

        return when {
            channelsMap == null -> EventHandlingResult.Skip
            channelsMap.containsKey(cid) -> EventHandlingResult.Remove(cid)
            else -> EventHandlingResult.Skip
        }
    }

    /**
     * Checks if the current user joined the channel and the channel is not visible yet.
     * If yes then it adds it. Otherwise, it simply skips the event.
     *
     * @param channel Optional cached channel object.
     * @param member The member who joined the channel.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    protected fun addIfCurrentUserJoinedChannel(channel: Channel?, member: Member): EventHandlingResult {
        return if (clientState.user.value?.id == member.getUserId()) {
            addIfChannelIsAbsent(channel)
        } else {
            EventHandlingResult.Skip
        }
    }

    /**
     * Checks if the cached channel exists and is not visible yet.
     * If yes then it adds it. Otherwise, it simply skips the event.
     *
     * @param channel Optional cached channel object.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    protected fun addIfChannelIsAbsent(channel: Channel?): EventHandlingResult {
        val channelsMap = channels.value

        return when {
            channelsMap == null || channel == null -> EventHandlingResult.Skip
            channelsMap.containsKey(channel.cid) -> EventHandlingResult.Skip
            else -> EventHandlingResult.Add(channel)
        }
    }

    private companion object {
        private const val SYSTEM_MESSAGE = "system"
    }
}
