package network.ermis.offline.repository.domain.queryChannels

import network.ermis.client.persistance.QueryChannelsRepository
import network.ermis.client.query.QueryChannelsSpec
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.querysort.QuerySorter

/**
 * Repository for queries of channels. This implementation uses the database.
 */
internal class DatabaseQueryChannelsRepository(
    private val queryChannelsDao: QueryChannelsDao,
) : QueryChannelsRepository {

    /**
     * Inserts a query channels.
     *
     * @param queryChannelsSpec [QueryChannelsSpec]
     */
    override suspend fun insertQueryChannels(queryChannelsSpec: QueryChannelsSpec) {
        queryChannelsDao.insert(toEntity(queryChannelsSpec))
    }

    /**
     * Selects by a filter and query sort.
     *
     * @param filter [FilterObject]
     * @param querySort [QuerySorter]
     */
    override suspend fun selectBy(filter: FilterObject, querySort: QuerySorter<Channel>): QueryChannelsSpec? {
        return queryChannelsDao.select(generateId(filter, querySort))?.let(Companion::toModel)
    }

    override suspend fun clear() {
        queryChannelsDao.deleteAll()
    }

    private companion object {
        private fun generateId(filter: FilterObject, querySort: QuerySorter<Channel>): String {
            return "${filter.hashCode()}-${querySort.toDto().hashCode()}"
        }

        private fun toEntity(queryChannelsSpec: QueryChannelsSpec): QueryChannelsEntity =
            QueryChannelsEntity(
                generateId(queryChannelsSpec.filter, queryChannelsSpec.querySort),
                queryChannelsSpec.filter,
                queryChannelsSpec.querySort,
                queryChannelsSpec.cids.toList(),
            )

        private fun toModel(queryChannelsEntity: QueryChannelsEntity): QueryChannelsSpec =
            QueryChannelsSpec(
                queryChannelsEntity.filter,
                queryChannelsEntity.querySort,
            ).apply { cids = queryChannelsEntity.cids.toSet() }
    }
}
