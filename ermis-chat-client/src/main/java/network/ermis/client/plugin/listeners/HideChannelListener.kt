package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import io.getstream.result.Result

/**
 * Listener of [ErmisClient.hideChannel] requests.
 */
public interface HideChannelListener {

    /**
     * Run precondition for the request. If it returns [Result.Success] then the request is run otherwise it returns
     * [Result.Failure] and no request is made.
     *
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     *
     * @return [Result.Success] if precondition passes otherwise [Result.Failure]
     */
    public suspend fun onHideChannelPrecondition(
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ): Result<Unit>

    /**
     * Runs side effect before the request is launched.
     *
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     */
    public suspend fun onHideChannelRequest(
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    )

    /**
     * Runs this function on the result of the request.
     *
     * @param result Result of this request.
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     */
    public suspend fun onHideChannelResult(
        result: Result<Unit>,
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    )
}
