package network.ermis.state.plugin.logic.querychannels

import network.ermis.client.persistance.ChannelConfigRepository
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.QueryChannelsRepository
import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.query.QueryChannelsSpec
import network.ermis.client.query.pagination.AnyChannelPaginationRequest
import network.ermis.client.utils.extensions.internal.applyPagination
import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelConfig

@Suppress("LongParameterList")
internal class QueryChannelsDatabaseLogic(
    private val queryChannelsRepository: QueryChannelsRepository,
    private val channelConfigRepository: ChannelConfigRepository,
    private val channelRepository: ChannelRepository,
    private val repositoryFacade: RepositoryFacade,
) {

    internal suspend fun storeStateForChannels(channels: Collection<Channel>) {
        repositoryFacade.storeStateForChannels(channels)
    }

    /**
     * Fetch channels from database
     *
     * @param pagination [AnyChannelPaginationRequest]
     * @param queryChannelsSpec [QueryChannelsSpec]
     */
    internal suspend fun fetchChannelsFromCache(
        pagination: AnyChannelPaginationRequest,
        queryChannelsSpec: QueryChannelsSpec?,
    ): List<Channel> {
        val query = queryChannelsSpec?.run {
            queryChannelsRepository.selectBy(queryChannelsSpec.filter, queryChannelsSpec.querySort)
        } ?: return emptyList()

        return repositoryFacade.selectChannels(query.cids.toList(), pagination).applyPagination(pagination)
    }

    /**
     * Select a channel from database without fetching messages
     *
     * @param cid String
     */
    internal suspend fun selectChannel(cid: String): Channel? {
        return channelRepository.selectChannel(cid)
    }

    /**
     * Select channels from database without fetching messages
     *
     * @param cids List<String>
     */
    internal suspend fun selectChannels(cids: List<String>): List<Channel> {
        return channelRepository.selectChannels(cids)
    }

    /**
     * Insert a query spec that was made in the database.
     *
     * @param queryChannelsSpec QueryChannelsSpec
     */
    internal suspend fun insertQueryChannels(queryChannelsSpec: QueryChannelsSpec) {
        return queryChannelsRepository.insertQueryChannels(queryChannelsSpec)
    }

    /**
     * Insert the configs of the channels
     *
     * @param configs Collection<ChannelConfig>
     */
    internal suspend fun insertChannelConfigs(configs: Collection<ChannelConfig>) {
        return channelConfigRepository.insertChannelConfigs(configs)
    }
}