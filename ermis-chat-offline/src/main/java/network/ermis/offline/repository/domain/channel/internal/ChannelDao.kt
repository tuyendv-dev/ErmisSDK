package network.ermis.offline.repository.domain.channel.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import network.ermis.core.models.SyncStatus
import java.util.Date

@SuppressWarnings("TooManyFunctions")
@Dao
internal interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channelEntity: ChannelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(channelEntities: List<ChannelEntity>)

    @Transaction
    @Query("SELECT cid FROM $CHANNEL_ENTITY_TABLE_NAME")
    suspend fun selectAllCids(): List<String>

    @Query(
        "SELECT cid FROM $CHANNEL_ENTITY_TABLE_NAME " +
            "WHERE syncStatus = :syncStatus " +
            "ORDER BY syncStatus ASC " +
            "LIMIT :limit",
    )
    suspend fun selectCidsBySyncNeeded(
        syncStatus: SyncStatus = SyncStatus.SYNC_NEEDED,
        limit: Int = NO_LIMIT,
    ): List<String>

    @Query(
        "SELECT * FROM $CHANNEL_ENTITY_TABLE_NAME " +
            "WHERE syncStatus = :syncStatus " +
            "ORDER BY syncStatus ASC " +
            "LIMIT :limit",
    )
    suspend fun selectSyncNeeded(
        syncStatus: SyncStatus = SyncStatus.SYNC_NEEDED,
        limit: Int = NO_LIMIT,
    ): List<ChannelEntity>

    @Query(
        "SELECT * FROM $CHANNEL_ENTITY_TABLE_NAME " +
            "WHERE $CHANNEL_ENTITY_TABLE_NAME.cid IN (:cids)",
    )
    suspend fun select(cids: List<String>): List<ChannelEntity>

    @Query(
        "SELECT * FROM $CHANNEL_ENTITY_TABLE_NAME " +
            "WHERE $CHANNEL_ENTITY_TABLE_NAME.cid IN (:cid)",
    )
    suspend fun select(cid: String?): ChannelEntity?

    @Query("DELETE from $CHANNEL_ENTITY_TABLE_NAME WHERE cid = :cid")
    suspend fun delete(cid: String)

    @Query("UPDATE $CHANNEL_ENTITY_TABLE_NAME SET deletedAt = :deletedAt WHERE cid = :cid")
    suspend fun setDeletedAt(cid: String, deletedAt: Date)

    @Query(
        "UPDATE $CHANNEL_ENTITY_TABLE_NAME " +
            "SET hidden = :hidden, hideMessagesBefore = :hideMessagesBefore " +
            "WHERE cid = :cid",
    )
    suspend fun setHidden(cid: String, hidden: Boolean, hideMessagesBefore: Date)

    @Query("UPDATE $CHANNEL_ENTITY_TABLE_NAME SET hidden = :hidden WHERE cid = :cid")
    suspend fun setHidden(cid: String, hidden: Boolean)

    @Query("DELETE FROM $CHANNEL_ENTITY_TABLE_NAME")
    suspend fun deleteAll()

    private companion object {
        private const val NO_LIMIT: Int = -1
    }
}
