package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.core.models.Channel
import io.getstream.result.Result

/**
 * Listener of [ErmisClient.queryChannel] requests.
 */
public interface QueryChannelListener {
    /**
     * Run precondition for the request. If it returns [Result.Success] then the request is run otherwise it returns
     * [Result.Failure] and no request is made.
     */
    public suspend fun onQueryChannelPrecondition(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ): Result<Unit>

    /**
     * Runs side effect before the request is launched.
     *
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param request [QueryChannelRequest] which is going to be used for the request.
     */
    public suspend fun onQueryChannelRequest(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    )

    /**
     * Runs this function on the result of the request.
     */
    public suspend fun onQueryChannelResult(
        result: Result<Channel>,
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    )
}
