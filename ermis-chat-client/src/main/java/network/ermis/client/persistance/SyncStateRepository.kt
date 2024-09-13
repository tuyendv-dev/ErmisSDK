package network.ermis.client.persistance

import network.ermis.client.sync.SyncState

/**
 * Repository to read and write data about the sync state of the SDK.
 */
public interface SyncStateRepository {

    /**
     * Inserts a sync state.
     *
     * @param syncState [SyncState]
     */
    public suspend fun insertSyncState(syncState: SyncState)

    /**
     * Selects a sync state.
     *
     * @param userId String
     */
    public suspend fun selectSyncState(userId: String): SyncState?

    /**
     * Clear syncStates of this repository.
     */
    public suspend fun clear()
}
