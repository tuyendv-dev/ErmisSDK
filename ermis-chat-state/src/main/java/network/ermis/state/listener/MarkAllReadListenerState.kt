package network.ermis.state.listener

import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.client.plugin.listeners.MarkAllReadListener
import network.ermis.state.plugin.logic.LogicRegistry
import network.ermis.state.plugin.state.StateRegistry

/**
 * [MarkAllReadListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
 * Marks all active channels as read if needed.
 *
 * @param logic [LogicRegistry]
 * @param state [StateRegistry]
 */
internal class MarkAllReadListenerState(
    private val logic: LogicRegistry,
    private val state: StateRegistry,
) : network.ermis.client.plugin.listeners.MarkAllReadListener {

    /**
     * Marks all active channels as read if needed.
     *
     * @see [LogicRegistry.getActiveChannelsLogic]
     */
    override suspend fun onMarkAllReadRequest() {
        logic.getActiveChannelsLogic().map { channel ->
            val (channelType, channelId) = channel.cid.cidToTypeAndId()
            state.markChannelAsRead(channelType = channelType, channelId = channelId)
        }
    }
}
