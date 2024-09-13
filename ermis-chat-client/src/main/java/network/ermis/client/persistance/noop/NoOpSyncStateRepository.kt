package network.ermis.client.persistance.noop

import network.ermis.client.persistance.SyncStateRepository
import network.ermis.client.sync.SyncState

/**
 * No-Op SyncStateRepository.
 */
internal object NoOpSyncStateRepository : SyncStateRepository {
    override suspend fun insertSyncState(syncState: SyncState) { /* No-Op */ }
    override suspend fun selectSyncState(userId: String): SyncState? = null
    override suspend fun clear() { /* No-Op */ }
}
