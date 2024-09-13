package network.ermis.offline.repository.domain.channelconfig

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface ChannelConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(channelConfigEntities: List<ChannelConfigEntity>) {
        insertConfigs(channelConfigEntities.map(ChannelConfigEntity::channelConfigInnerEntity))
        insertCommands(channelConfigEntities.flatMap(ChannelConfigEntity::commands))
    }

    @Transaction
    suspend fun insert(channelConfigEntity: ChannelConfigEntity) {
        insertConfig(channelConfigEntity.channelConfigInnerEntity)
        insertCommands(channelConfigEntity.commands)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(channelConfigInnerEntity: ChannelConfigInnerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(channelConfigInnerEntities: List<ChannelConfigInnerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommands(commands: List<CommandInnerEntity>)

    @Transaction
    @Query("SELECT * FROM stream_chat_channel_config LIMIT 100")
    suspend fun selectAll(): List<ChannelConfigEntity>

    @Query("DELETE FROM $COMMAND_INNER_ENTITY_TABLE_NAME")
    suspend fun deleteCommands()

    @Query("DELETE FROM $CHANNEL_CONFIG_INNER_ENTITY_TABLE_NAME")
    suspend fun deleteConfigs()

    suspend fun deleteAll() {
        deleteConfigs()
        deleteCommands()
    }
}
