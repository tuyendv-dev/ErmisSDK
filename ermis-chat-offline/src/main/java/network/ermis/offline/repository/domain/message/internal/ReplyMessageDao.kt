package network.ermis.offline.repository.domain.message.internal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import network.ermis.offline.repository.domain.message.attachment.ReplyAttachmentEntity

@Dao
internal interface ReplyMessageDao {

    @Query("SELECT * FROM $REPLY_MESSAGE_ENTITY_TABLE_NAME WHERE id = :id")
    @Transaction
    suspend fun selectById(id: String): ReplyMessageEntity?

    @Transaction
    suspend fun insert(replyMessageEntities: List<ReplyMessageEntity>) {
        insertAttachments(replyMessageEntities.flatMap(ReplyMessageEntity::attachments))
        insertInnerEntity(replyMessageEntities.map(ReplyMessageEntity::replyMessageInnerEntity))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInnerEntity(replyMessageEntities: List<ReplyMessageInnerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachmentEntities: List<ReplyAttachmentEntity>)

    @Delete
    suspend fun delete(replyMessageInnerEntity: ReplyMessageInnerEntity)

    @Query("DELETE FROM $REPLY_MESSAGE_ENTITY_TABLE_NAME")
    suspend fun deleteAll()
}
