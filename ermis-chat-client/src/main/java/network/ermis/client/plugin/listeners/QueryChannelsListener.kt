package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.core.models.Channel
import io.getstream.result.Result

/**
 * Listener of [ErmisClient.queryChannels] requests.
 */
public interface QueryChannelsListener {

    /**
     * Run precondition for the request. If it returns [Result.Success] then the request is run otherwise it returns
     * [Result.Failure] and no request is made.
     *
     * @param request [QueryChannelRequest] which is going to be used for the request.
     *
     * @return [Result.Success] if precondition passes otherwise [Result.Failure]
     */
    public suspend fun onQueryChannelsPrecondition(
        request: QueryChannelsRequest,
    ): Result<Unit>

    /**
     * Runs side effect before the request is launched.
     *
     * @param request [QueryChannelsRequest] which is going to be used for the request.
     */
    public suspend fun onQueryChannelsRequest(request: QueryChannelsRequest)

    /**
     * Runs this function on the [Result] of this [QueryChannelsRequest].
     */
    public suspend fun onQueryChannelsResult(result: Result<List<Channel>>, request: QueryChannelsRequest)
}
