package network.ermis.state.listener

import io.getstream.result.Result
import kotlinx.coroutines.flow.MutableStateFlow
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.plugin.listeners.QueryChannelsListener
import network.ermis.client.query.pagination.AnyChannelPaginationRequest
import network.ermis.core.models.Channel
import network.ermis.state.model.QueryChannelsPaginationRequest
import network.ermis.state.model.toAnyChannelPaginationRequest
import network.ermis.state.plugin.logic.LogicRegistry

/**
 * [QueryChannelsListener] implementation for [StatePlugin].
 * Handles querying the channel offline and managing local state updates.
 *
 * This class is considered to handle state. Although this class interacts with the database, it doesn't make sense
 * to use it only with the database and not state module. Therefore this class is considered as a state class and should
 * be initialized inside `StreamStatePluginFactory`. It is important to noticed that this class will be affected
 * by the inclusion/exclusion of the offline plugin. When using offline plugin, the database will be
 * used to update the state of the SDK, but when not, the state will be updated only with the results of the internet
 * request.
 *
 * @param logic [LogicRegistry] provided by the [StreamStatePluginFactory].
 */
internal class QueryChannelsListenerState(
    private val logicProvider: LogicRegistry,
    private val queryingChannelsFree: MutableStateFlow<Boolean>,
) : network.ermis.client.plugin.listeners.QueryChannelsListener {

    override suspend fun onQueryChannelsPrecondition(request: QueryChannelsRequest): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun onQueryChannelsRequest(request: QueryChannelsRequest) {
        queryingChannelsFree.value = false
        logicProvider.queryChannels(request).run {
            setCurrentRequest(request)
            queryOffline(request.toPagination())
        }
    }

    override suspend fun onQueryChannelsResult(result: Result<List<Channel>>, request: QueryChannelsRequest) {
        logicProvider.queryChannels(request).onQueryChannelsResult(result, request)
        queryingChannelsFree.value = true
    }

    private companion object {
        private fun QueryChannelsRequest.toPagination(): AnyChannelPaginationRequest =
            QueryChannelsPaginationRequest(
                sort = querySort,
                channelLimit = limit,
                channelOffset = offset,
                messageLimit = messageLimit,
                memberLimit = memberLimit,
            ).toAnyChannelPaginationRequest()
    }
}
