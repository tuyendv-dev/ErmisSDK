package network.ermis.state.event.handler.utils

import network.ermis.client.utils.mergePartially
import network.ermis.core.models.User
import network.ermis.state.event.handler.model.SelfUser
import network.ermis.state.event.handler.model.SelfUserFull
import network.ermis.state.event.handler.model.SelfUserPart
import network.ermis.state.plugin.state.global.MutableGlobalState

/**
 * Updates [MutableGlobalState] with [SelfUser] instance.
 */
internal fun MutableGlobalState.updateCurrentUser(currentUser: User?, receivedUser: SelfUser) {
    val me = when (receivedUser) {
        is SelfUserFull -> receivedUser.me
        is SelfUserPart -> currentUser?.mergePartially(receivedUser.me) ?: receivedUser.me
    }

    setBanned(me.isBanned)
    setMutedUsers(me.mutes)
    setChannelMutes(me.channelMutes)
    setTotalUnreadCount(me.totalUnreadCount)
    setChannelUnreadCount(me.unreadChannels)
}
