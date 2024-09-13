package network.ermis.offline.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import network.ermis.offline.repository.database.converter.DateConverter
import network.ermis.offline.repository.database.converter.ExtraDataConverter
import network.ermis.offline.repository.database.converter.FilterObjectConverter
import network.ermis.offline.repository.database.converter.ListConverter
import network.ermis.offline.repository.database.converter.MapConverter
import network.ermis.offline.repository.database.converter.MemberConverter
import network.ermis.offline.repository.database.converter.ModerationDetailsConverter
import network.ermis.offline.repository.database.converter.QuerySortConverter
import network.ermis.offline.repository.database.converter.SetConverter
import network.ermis.offline.repository.database.converter.SyncStatusConverter
import network.ermis.offline.repository.domain.channel.internal.ChannelDao
import network.ermis.offline.repository.domain.channel.internal.ChannelEntity
import network.ermis.offline.repository.domain.channelconfig.ChannelConfigDao
import network.ermis.offline.repository.domain.channelconfig.ChannelConfigInnerEntity
import network.ermis.offline.repository.domain.channelconfig.CommandInnerEntity
import network.ermis.offline.repository.domain.message.attachment.AttachmentDao
import network.ermis.offline.repository.domain.message.attachment.AttachmentEntity
import network.ermis.offline.repository.domain.message.attachment.ReplyAttachmentEntity
import network.ermis.offline.repository.domain.message.internal.MessageDao
import network.ermis.offline.repository.domain.message.internal.MessageInnerEntity
import network.ermis.offline.repository.domain.message.internal.ReplyMessageDao
import network.ermis.offline.repository.domain.message.internal.ReplyMessageInnerEntity
import network.ermis.offline.repository.domain.queryChannels.QueryChannelsDao
import network.ermis.offline.repository.domain.queryChannels.QueryChannelsEntity
import network.ermis.offline.repository.domain.reaction.ReactionDao
import network.ermis.offline.repository.domain.reaction.ReactionEntity
import network.ermis.offline.repository.domain.syncState.SyncStateDao
import network.ermis.offline.repository.domain.syncState.SyncStateEntity
import network.ermis.offline.repository.domain.user.UserDao
import network.ermis.offline.repository.domain.user.UserEntity

@Database(
    entities = [
        QueryChannelsEntity::class,
        MessageInnerEntity::class,
        ReplyMessageInnerEntity::class,
        AttachmentEntity::class,
        ReplyAttachmentEntity::class,
        UserEntity::class,
        ReactionEntity::class,
        ChannelEntity::class,
        ChannelConfigInnerEntity::class,
        CommandInnerEntity::class,
        SyncStateEntity::class,
    ],
    version = 70,
    exportSchema = false,
)
@TypeConverters(
    FilterObjectConverter::class,
    ListConverter::class,
    MapConverter::class,
    QuerySortConverter::class,
    ExtraDataConverter::class,
    SetConverter::class,
    SyncStatusConverter::class,
    DateConverter::class,
    MemberConverter::class,
    ModerationDetailsConverter::class,
)
internal abstract class ChatDatabase : RoomDatabase() {
    abstract fun queryChannelsDao(): QueryChannelsDao
    abstract fun userDao(): UserDao
    abstract fun reactionDao(): ReactionDao
    abstract fun messageDao(): MessageDao
    abstract fun replyMessageDao(): ReplyMessageDao
    abstract fun channelStateDao(): ChannelDao
    abstract fun channelConfigDao(): ChannelConfigDao
    abstract fun syncStateDao(): SyncStateDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        @Volatile
        private var INSTANCES: MutableMap<String, ChatDatabase?> = mutableMapOf()

        fun getDatabase(context: Context, userId: String): ChatDatabase {
            if (!INSTANCES.containsKey(userId)) {
                synchronized(this) {
                    val db = Room.databaseBuilder(
                        context.applicationContext,
                        ChatDatabase::class.java,
                        "stream_chat_database_$userId",
                    ).fallbackToDestructiveMigration()
                        .addCallback(
                            object : Callback() {
                                override fun onOpen(db: SupportSQLiteDatabase) {
                                    db.execSQL("PRAGMA synchronous = 1")
                                }
                            },
                        )
                        .build()
                    INSTANCES[userId] = db
                }
            }
            return INSTANCES[userId] ?: error("DB not created")
        }
    }
}
