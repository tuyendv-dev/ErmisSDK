package network.ermis.state.event.chat

import network.ermis.client.events.ChannelDeletedEvent
import network.ermis.client.events.ChannelHiddenEvent
import network.ermis.client.events.ChannelVisibleEvent
import network.ermis.client.events.ChatEvent
import network.ermis.client.events.CidEvent
import network.ermis.client.events.HasChannel
import network.ermis.client.events.NotificationChannelDeletedEvent
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject

/**
 * Handler responsible for deciding whether the set of channels should be updated after receiving the particular event.
 *
 * @see [EventHandlingResult]
 */
public fun interface ChatEventHandler {
    /**
     * Computes the event handling result.
     *
     * @param event [ChatEvent] that may contain updates for the set of channels.
     * @param filter [FilterObject] associated with the set of channels. Can be used to define the result of handling.
     * @param cachedChannel optional cached [Channel] object.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    public fun handleChatEvent(event: ChatEvent, filter: FilterObject, cachedChannel: Channel?): EventHandlingResult
}

/**
 * Represent possible outcomes of handling a chat event.
 */
public sealed class EventHandlingResult {
    /**
     * Add a channel to a query channels collection.
     *
     * @param channel Channel to be added.
     */
    public data class Add(public val channel: Channel) : EventHandlingResult()

    /**
     * Call watch and add the channel to a query channels collection.
     *
     * @param cid cid of the channel to watch and add.
     */
    public data class WatchAndAdd(public val cid: String) : EventHandlingResult()

    /**
     * Remove a channel from a query channels collection.
     *
     * @param cid cid of channel to remove.
     *
     */
    public data class Remove(public val cid: String) : EventHandlingResult()

    /**
     * Skip the event.
     */
    public object Skip : EventHandlingResult() {
        override fun toString(): String = "Skip"
    }
}

/**
 * More specific [ChatEventHandler] implementation that gives you a separation
 * between [CidEvent] and [HasChannel] events.
 *
 * The channel will be removed from the set after receiving
 * [ChannelDeletedEvent], [NotificationChannelDeletedEvent], [ChannelHiddenEvent] events.
 *
 * The channel will be watched and added to the set after receiving [ChannelVisibleEvent] event.
 *
 * Other events will be skipped.
 */
public abstract class BaseChatEventHandler : ChatEventHandler {

    /**
     * Handles [HasChannel] event which contains specific [Channel] object.
     *
     * @param event [ChatEvent] that may contain updates for the set of channels.
     * @param filter [FilterObject] associated with the set of channels. Can be used to define the result of handling.

     * @return [EventHandlingResult] Result of handling.
     */
    public open fun handleChannelEvent(event: HasChannel, filter: FilterObject): EventHandlingResult {
        return when (event) {
            is ChannelDeletedEvent -> EventHandlingResult.Remove(event.cid)
            is NotificationChannelDeletedEvent -> EventHandlingResult.Remove(event.cid)
            else -> EventHandlingResult.Skip
        }
    }

    /**
     * Handles [CidEvent] event which is associated with a specific [Channel] which can be tracked using [CidEvent.cid].
     *
     * @param event [ChatEvent] that may contain updates for the set of channels.
     * @param filter [FilterObject] associated with the set of channels. Can be used to define the result of handling.
     * @param cachedChannel optional cached [Channel] object if exists.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    public open fun handleCidEvent(
        event: CidEvent,
        filter: FilterObject,
        cachedChannel: Channel?,
    ): EventHandlingResult {
        return when (event) {
            is ChannelHiddenEvent -> EventHandlingResult.Remove(event.cid)
            is ChannelVisibleEvent -> EventHandlingResult.WatchAndAdd(event.cid)
            else -> EventHandlingResult.Skip
        }
    }

    /**
     * Computes the event handling result.
     *
     * @param event [ChatEvent] that may contain updates for the set of channels.
     * @param filter [FilterObject] associated with the set of channels. Can be used to define the result of handling.
     * @param cachedChannel optional cached [Channel] object.
     *
     * @return [EventHandlingResult] Result of handling.
     */
    override fun handleChatEvent(event: ChatEvent, filter: FilterObject, cachedChannel: Channel?): EventHandlingResult {
        return when (event) {
            is HasChannel -> handleChannelEvent(event, filter)
            is CidEvent -> handleCidEvent(event, filter, cachedChannel)
            else -> EventHandlingResult.Skip
        }
    }
}
