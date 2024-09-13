package network.ermis.offline.plugin.listener

import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.plugin.listeners.QueryChannelListener
import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelConfig
import io.getstream.result.Result
import io.getstream.result.onSuccessSuspend

/**
 * Implementation for [QueryChannelListener] that handles database update.
 */
internal class QueryChannelListenerDatabase(private val repos: RepositoryFacade) :
    network.ermis.client.plugin.listeners.QueryChannelListener {

    override suspend fun onQueryChannelPrecondition(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onQueryChannelRequest(channelType: String, channelId: String, request: QueryChannelRequest) {
        /*
         * Nothing to do. This class handles only the result of the API request, which can be separated from the
         * state logic.
         */
    }

    /**
     * Updates the database of the SDK once the query for channel is complete successfully.
     *
     * @param result Result<Channel>
     * @param channelType String
     * @param channelId String
     * @param request [QueryChannelRequest]
     */
    override suspend fun onQueryChannelResult(
        result: Result<Channel>,
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ) {
        result.onSuccessSuspend { channel ->
            // first thing here needs to be updating configs otherwise we have a race with receiving events
            repos.insertChannelConfig(ChannelConfig(channel.type, channel.config))
            repos.storeStateForChannel(channel)
        }
    }
}
