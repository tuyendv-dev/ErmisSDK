package network.ermis.client.persistance.noop

import network.ermis.client.persistance.QueryChannelsRepository
import network.ermis.client.query.QueryChannelsSpec
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.querysort.QuerySorter

/**
 * No-Op QueryChannelsRepository.
 */
internal object NoOpQueryChannelsRepository : QueryChannelsRepository {
    override suspend fun insertQueryChannels(queryChannelsSpec: QueryChannelsSpec) { /* No-Op */ }
    override suspend fun selectBy(filter: FilterObject, querySort: QuerySorter<Channel>): QueryChannelsSpec? = null
    override suspend fun clear() { /* No-Op */ }
}
