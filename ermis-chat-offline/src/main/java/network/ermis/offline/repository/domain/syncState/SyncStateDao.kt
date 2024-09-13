package network.ermis.offline.repository.domain.syncState

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface SyncStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncStateEntity: SyncStateEntity)

    @Query(
        "SELECT * FROM $SYNC_STATE_ENTITY_TABLE_NAME " +
            "WHERE stream_sync_state.userId = :userId",
    )
    suspend fun select(userId: String): SyncStateEntity?

    @Query("DELETE FROM $SYNC_STATE_ENTITY_TABLE_NAME")
    suspend fun deleteAll()
}
