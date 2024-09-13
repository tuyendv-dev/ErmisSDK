package network.ermis.state.sync

import kotlinx.coroutines.flow.Flow
import network.ermis.client.events.ChatEvent

/**
 * Tries to sync, if necessary, when connection is reestablished or when a health check event happens.
 *
 * In addition it is responsible to sync messages, reactions and channel data.
 */
internal interface SyncHistoryManager {

    /**
     * History synced events.
     */
    val syncedEvents: Flow<List<ChatEvent>>

    /**
     * Starts history syncing based on WS events.
     */
    fun start()

    /**
     * Forces immediate history syncing.
     */
    suspend fun sync()

    /**
     * Awaits until history syncing process gets finished.
     */
    suspend fun awaitSyncing()

    /**
     * Stops history syncing based on WS events.
     */
    fun stop()
}