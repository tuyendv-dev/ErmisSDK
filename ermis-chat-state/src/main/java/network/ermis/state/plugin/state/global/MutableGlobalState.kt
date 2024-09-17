package network.ermis.state.plugin.state.global

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import network.ermis.core.models.ChannelMute
import network.ermis.core.models.Mute
import network.ermis.core.models.TypingEvent
import network.ermis.state.plugin.internal.StatePlugin

/**
 * Mutable global state of [StatePlugin].
 */
internal class MutableGlobalState : GlobalState {

    private var _totalUnreadCount: MutableStateFlow<Int>? = MutableStateFlow(0)
    private var _channelUnreadCount: MutableStateFlow<Int>? = MutableStateFlow(0)
    private var _banned: MutableStateFlow<Boolean>? = MutableStateFlow(false)
    private var _mutedUsers: MutableStateFlow<List<Mute>>? = MutableStateFlow(emptyList())
    private var _channelMutes: MutableStateFlow<List<ChannelMute>>? = MutableStateFlow(emptyList())
    private var _typingChannels: MutableStateFlow<Map<String, TypingEvent>>? = MutableStateFlow(emptyMap())

    override val totalUnreadCount: StateFlow<Int> = _totalUnreadCount!!
    override val channelUnreadCount: StateFlow<Int> = _channelUnreadCount!!
    override val muted: StateFlow<List<Mute>> = _mutedUsers!!
    override val channelMutes: StateFlow<List<ChannelMute>> = _channelMutes!!
    override val banned: StateFlow<Boolean> = _banned!!
    override val typingChannels: StateFlow<Map<String, TypingEvent>> = _typingChannels!!

    /**
     * Destroys the state.
     */
    fun destroy() {
        _totalUnreadCount = null
        _channelUnreadCount = null
        _mutedUsers = null
        _channelMutes = null
        _banned = null
        _typingChannels = null
    }

    fun setTotalUnreadCount(totalUnreadCount: Int) {
        _totalUnreadCount?.value = totalUnreadCount
    }

    fun setChannelUnreadCount(channelUnreadCount: Int) {
        _channelUnreadCount?.value = channelUnreadCount
    }

    fun setBanned(banned: Boolean) {
        _banned?.value = banned
    }

    fun setChannelMutes(channelMutes: List<ChannelMute>) {
        _channelMutes?.value = channelMutes
    }

    fun setMutedUsers(mutedUsers: List<Mute>) {
        _mutedUsers?.value = mutedUsers
    }

    /**
     * Tries emit typing event for a particular channel.
     *
     * @param cid The full channel id, i.e. "messaging:123" to which the message with reaction belongs.
     * @param typingEvent [TypingEvent] with information about typing users. Current user is excluded.
     */
    fun tryEmitTypingEvent(cid: String, typingEvent: TypingEvent) {
        _typingChannels?.let {
            it.tryEmit(
                it.value.toMutableMap().apply {
                    if (typingEvent.users.isEmpty()) {
                        remove(cid)
                    } else {
                        this[cid] = typingEvent
                    }
                },
            )
        }
    }
}
