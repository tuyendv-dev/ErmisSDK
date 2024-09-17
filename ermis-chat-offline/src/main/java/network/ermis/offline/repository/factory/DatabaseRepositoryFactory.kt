package network.ermis.offline.repository.factory

import kotlinx.coroutines.CoroutineScope
import network.ermis.client.persistance.ChannelConfigRepository
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.QueryChannelsRepository
import network.ermis.client.persistance.ReactionRepository
import network.ermis.client.persistance.SyncStateRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.persistance.factory.RepositoryFactory
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.offline.repository.database.ChatDatabase
import network.ermis.offline.repository.domain.channel.internal.DatabaseChannelRepository
import network.ermis.offline.repository.domain.channelconfig.DatabaseChannelConfigRepository
import network.ermis.offline.repository.domain.message.internal.DatabaseMessageRepository
import network.ermis.offline.repository.domain.queryChannels.DatabaseQueryChannelsRepository
import network.ermis.offline.repository.domain.reaction.DatabaseReactionRepository
import network.ermis.offline.repository.domain.syncState.DatabaseSyncStateRepository
import network.ermis.offline.repository.domain.user.DatabaseUserRepository

private const val DEFAULT_CACHE_SIZE = 1000

internal class DatabaseRepositoryFactory(
    private val database: ChatDatabase,
    private val currentUser: User,
    private val scope: CoroutineScope,
) : RepositoryFactory {

    private var repositoriesCache: MutableMap<Class<out Any>, Any> = mutableMapOf()

    override fun createUserRepository(): UserRepository {
        val databaseUserRepository = repositoriesCache[UserRepository::class.java] as? DatabaseUserRepository?

        return databaseUserRepository ?: run {
            DatabaseUserRepository(scope, database.userDao(), DEFAULT_CACHE_SIZE).also { repository ->
                repositoriesCache[UserRepository::class.java] = repository
            }
        }
    }

    override fun createChannelConfigRepository(): ChannelConfigRepository {
        val databaseChannelConfigRepository =
            repositoriesCache[ChannelConfigRepository::class.java] as? DatabaseChannelConfigRepository?

        return databaseChannelConfigRepository ?: run {
            DatabaseChannelConfigRepository(database.channelConfigDao()).also { repository ->
                repositoriesCache[ChannelConfigRepository::class.java] = repository
            }
        }
    }

    override fun createChannelRepository(
        getUser: suspend (userId: String) -> User,
        getMessage: suspend (messageId: String) -> Message?,
    ): ChannelRepository {
        val databaseChannelRepository = repositoriesCache[ChannelRepository::class.java] as? DatabaseChannelRepository?

        return databaseChannelRepository ?: run {
            DatabaseChannelRepository(scope, database.channelStateDao(), getUser, getMessage)
                .also { repository ->
                    repositoriesCache[ChannelRepository::class.java] = repository
                }
        }
    }

    override fun createQueryChannelsRepository(): QueryChannelsRepository {
        val databaseQueryChannelsRepository =
            repositoriesCache[QueryChannelsRepository::class.java] as? QueryChannelsRepository?

        return databaseQueryChannelsRepository ?: run {
            DatabaseQueryChannelsRepository(database.queryChannelsDao()).also { repository ->
                repositoriesCache[QueryChannelsRepository::class.java] = repository
            }
        }
    }

    override fun createMessageRepository(
        getUser: suspend (userId: String) -> User,
    ): MessageRepository {
        val databaseMessageRepository = repositoriesCache[MessageRepository::class.java] as? DatabaseMessageRepository?

        return databaseMessageRepository ?: run {
            DatabaseMessageRepository(
                scope,
                database.messageDao(),
                database.replyMessageDao(),
                getUser,
                currentUser,
                DEFAULT_CACHE_SIZE,
            ).also { repository ->
                repositoriesCache[MessageRepository::class.java] = repository
            }
        }
    }

    override fun createReactionRepository(getUser: suspend (userId: String) -> User): ReactionRepository {
        val databaseReactionRepository =
            repositoriesCache[ReactionRepository::class.java] as? DatabaseReactionRepository?

        return databaseReactionRepository ?: run {
            DatabaseReactionRepository(database.reactionDao(), getUser).also { repository ->
                repositoriesCache[ReactionRepository::class.java] = repository
            }
        }
    }

    override fun createSyncStateRepository(): SyncStateRepository {
        val databaseSyncStateRepository =
            repositoriesCache[SyncStateRepository::class.java] as? DatabaseSyncStateRepository?

        return databaseSyncStateRepository ?: run {
            DatabaseSyncStateRepository(database.syncStateDao()).also { repository ->
                repositoriesCache[SyncStateRepository::class.java] = repository
            }
        }
    }
}
