package network.ermis.state.listener

import network.ermis.client.plugin.listeners.ChannelMarkReadListener
import network.ermis.state.plugin.state.StateRegistry
import io.getstream.result.Error
import io.getstream.result.Result

/**
 * [ChannelMarkReadListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
 * Checks if the channel can be marked as read and marks it locally if needed.
 *
 * @param state [StateRegistry]
 */
internal class ChannelMarkReadListenerState(private val state: StateRegistry) :
    network.ermis.client.plugin.listeners.ChannelMarkReadListener {

    /**
     * Checks if the channel can be marked as read and marks it locally if needed.
     *
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     *
     * @return [Result] with information if channel should be marked as read.
     */
    override suspend fun onChannelMarkReadPrecondition(channelType: String, channelId: String): Result<Unit> {
        val shouldMarkRead = state.markChannelAsRead(
            channelType = channelType,
            channelId = channelId,
        )

        return if (shouldMarkRead) {
            Result.Success(Unit)
        } else {
            Result.Failure(Error.GenericError("Can not mark channel as read with channel id: $channelId"))
        }
    }
}
