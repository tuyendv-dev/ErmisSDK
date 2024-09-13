package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import io.getstream.result.Result

/**
 * Listener for [ErmisClient.markRead] requests.
 */
public interface ChannelMarkReadListener {

    /**
     * Run precondition for the request. If it returns [Result.Success] then the request is run otherwise it returns
     * [Result.Failure] and no request is made.
     *
     * @param channelType Type of the channel to mark as read.
     * @param channelId Id of the channel to mark as read.
     *
     * @return [Result.Success] if precondition passes, otherwise [Result.Failure].
     */
    public suspend fun onChannelMarkReadPrecondition(
        channelType: String,
        channelId: String,
    ): Result<Unit>
}
