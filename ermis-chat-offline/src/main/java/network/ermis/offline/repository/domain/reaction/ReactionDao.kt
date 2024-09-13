package network.ermis.offline.repository.domain.reaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import network.ermis.core.models.SyncStatus
import java.util.Date

@Dao
internal interface ReactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reactionEntity: ReactionEntity)

    @Query("SELECT * FROM $REACTION_ENTITY_TABLE_NAME WHERE id = :id")
    suspend fun selectReactionById(id: Int): ReactionEntity?

    @Query("SELECT * FROM $REACTION_ENTITY_TABLE_NAME WHERE id IN (:ids)")
    suspend fun selectReactionsByIds(ids: List<Int>): List<ReactionEntity>

    @Query(
        "SELECT id FROM $REACTION_ENTITY_TABLE_NAME " +
            "WHERE syncStatus = :syncStatus " +
            "ORDER BY syncStatus ASC " +
            "LIMIT :limit",
    )
    suspend fun selectIdsSyncStatus(syncStatus: SyncStatus, limit: Int = NO_LIMIT): List<Int>

    @Query(
        "SELECT * FROM stream_chat_reaction " +
            "WHERE syncStatus = :syncStatus " +
            "ORDER BY syncStatus ASC " +
            "LIMIT :limit",
    )
    suspend fun selectSyncStatus(syncStatus: SyncStatus, limit: Int = NO_LIMIT): List<ReactionEntity>

    @Query(
        "SELECT * FROM $REACTION_ENTITY_TABLE_NAME " +
            "WHERE stream_chat_reaction.type = :reactionType " +
            "AND stream_chat_reaction.messageid = :messageId " +
            "AND userId = :userId",
    )
    suspend fun selectUserReactionToMessage(reactionType: String, messageId: String, userId: String): ReactionEntity?

    @Query(
        "SELECT * FROM $REACTION_ENTITY_TABLE_NAME " +
            "WHERE stream_chat_reaction.messageid = :messageId " +
            "AND userId = :userId",
    )
    suspend fun selectUserReactionsToMessage(messageId: String, userId: String): List<ReactionEntity>

    @Query(
        "UPDATE $REACTION_ENTITY_TABLE_NAME " +
            "SET deletedAt = :deletedAt " +
            "WHERE userId = :userId " +
            "AND messageId = :messageId",
    )
    suspend fun setDeleteAt(userId: String, messageId: String, deletedAt: Date)

    @Delete
    suspend fun delete(reactionEntity: ReactionEntity)

    @Query("DELETE FROM $REACTION_ENTITY_TABLE_NAME")
    suspend fun deleteAll()

    private companion object {
        private const val NO_LIMIT: Int = -1
    }
}
