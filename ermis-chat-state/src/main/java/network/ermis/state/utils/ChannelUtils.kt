package network.ermis.state.utils

import network.ermis.state.plugin.state.global.GlobalState

/**
 * Checks the given CID against the CIDs of channels muted for the current user.
 * Returns true for a muted channel, returns false otherwise.
 *
 * @param cid CID of the channel currently being checked.
 */
internal fun GlobalState.isChannelMutedForCurrentUser(cid: String): Boolean {
    return channelMutes.value.any { mutedChannel -> mutedChannel.channel.cid == cid }
}
