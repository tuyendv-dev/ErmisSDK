package network.ermis.offline.repository.domain.queryChannels

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface QueryChannelsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(queryChannelsEntity: QueryChannelsEntity)

    @Transaction
    @Query("SELECT * FROM $QUERY_CHANNELS_ENTITY_TABLE_NAME WHERE $QUERY_CHANNELS_ENTITY_TABLE_NAME.id=:id")
    suspend fun select(id: String): QueryChannelsEntity?

    @Query("DELETE FROM $QUERY_CHANNELS_ENTITY_TABLE_NAME")
    suspend fun deleteAll()
}
