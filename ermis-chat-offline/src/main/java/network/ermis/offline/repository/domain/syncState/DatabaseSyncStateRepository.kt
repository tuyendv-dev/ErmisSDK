package network.ermis.offline.repository.domain.syncState

import network.ermis.client.persistance.SyncStateRepository
import network.ermis.client.sync.SyncState

/**
 * Repository to read and write data about the sync state of the SDK. This implementation uses database
 */
internal class DatabaseSyncStateRepository(private val syncStateDao: SyncStateDao) : SyncStateRepository {

    /**
     * Inserts a sync state.
     *
     * @param syncState [SyncState]
     */
    override suspend fun insertSyncState(syncState: SyncState) {
        syncStateDao.insert(syncState.toEntity())
    }

    /**
     * Selects a sync state.
     *
     * @param userId String
     */
    override suspend fun selectSyncState(userId: String): SyncState? {
        return syncStateDao.select(userId)?.toModel()
    }

    override suspend fun clear() {
        syncStateDao.deleteAll()
    }
}
