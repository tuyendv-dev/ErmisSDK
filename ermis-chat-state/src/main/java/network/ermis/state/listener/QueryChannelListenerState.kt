package network.ermis.state.listener

import io.getstream.log.taggedLogger
import io.getstream.result.Result
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.plugin.listeners.QueryChannelListener
import network.ermis.client.utils.stringify
import network.ermis.core.models.Channel
import network.ermis.state.plugin.logic.LogicRegistry

/**
 * Implementation of [QueryChannelListener] that handles state updates in the SDK.
 *
 * @param logic [LogicRegistry]
 */
internal class QueryChannelListenerState(private val logic: LogicRegistry) :
    network.ermis.client.plugin.listeners.QueryChannelListener {

    private val logger by taggedLogger("QueryChannelListenerS")

    override suspend fun onQueryChannelPrecondition(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ): Result<Unit> = Result.Success(Unit)

    /**
     * The method that should be called before a call to query channel is made.
     *
     * @param channelType [String]
     * @param channelId [String]
     * @param request [QueryChannelRequest]
     */
    override suspend fun onQueryChannelRequest(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ) {
        logger.d { "[onQueryChannelRequest] cid: $channelType:$channelId, request: $request" }
        logic.channel(channelType, channelId).updateStateFromDatabase(request)
    }

    /**
     * The method that should be called after a call to query channel is made.
     *
     * @param result Result<Channel>
     * @param channelType [String]
     * @param channelId [String]
     * @param request [QueryChannelRequest]
     */
    override suspend fun onQueryChannelResult(
        result: Result<Channel>,
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ) {
        logger.d {
            "[onQueryChannelResult] cid: $channelType:$channelId, " +
                "request: $request, result: ${result.stringify { it.cid }}"
        }
        val channelStateLogic = logic.channel(channelType, channelId).stateLogic()

        result.onSuccess { channel -> channelStateLogic.propagateChannelQuery(channel, request) }
            .onError(channelStateLogic::propagateQueryError)
    }
}
