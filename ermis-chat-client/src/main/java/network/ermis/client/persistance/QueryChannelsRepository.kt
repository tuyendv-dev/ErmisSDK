package network.ermis.client.persistance

import network.ermis.client.query.QueryChannelsSpec
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.querysort.QuerySorter

/**
 * Repository for queries of channels.
 */
public interface QueryChannelsRepository {

    /**
     * Inserts a query channels.
     *
     * @param queryChannelsSpec [QueryChannelsSpec]
     */
    public suspend fun insertQueryChannels(queryChannelsSpec: QueryChannelsSpec)

    /**
     * Selects by a filter and query sort.
     *
     * @param filter [FilterObject]
     * @param querySort [QuerySorter]
     */
    public suspend fun selectBy(filter: FilterObject, querySort: QuerySorter<Channel>): QueryChannelsSpec?

    /**
     * Clear QueryChannels of this repository.
     */
    public suspend fun clear()
}
