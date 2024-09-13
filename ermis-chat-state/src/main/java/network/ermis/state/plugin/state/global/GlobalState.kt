package network.ermis.state.plugin.state.global

import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelMute
import network.ermis.core.models.Mute
import network.ermis.core.models.TypingEvent
import network.ermis.state.plugin.internal.StatePlugin
import kotlinx.coroutines.flow.StateFlow

/**
 * Global state of [StatePlugin].
 */
public interface GlobalState {

    /**
     * The total unread message count for the current user.
     * Depending on your app you'll want to show this or the channelUnreadCount.
     */
    public val totalUnreadCount: StateFlow<Int>

    /**
     * the number of unread channels for the current user.
     */
    public val channelUnreadCount: StateFlow<Int>

    /**
     * list of users that you've muted.
     */
    public val muted: StateFlow<List<Mute>>

    /**
     * List of channels you've muted.
     */
    public val channelMutes: StateFlow<List<ChannelMute>>

    /**
     * if the current user is banned or not.
     */
    public val banned: StateFlow<Boolean>

    /**
     * Map of typing users in all active channel.
     * Use [Channel.cid] to access events for a particular channel.
     *
     * @see [TypingEvent]
     */
    public val typingChannels: StateFlow<Map<String, TypingEvent>>
}
