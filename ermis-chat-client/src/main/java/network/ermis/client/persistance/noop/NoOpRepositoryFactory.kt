package network.ermis.client.persistance.noop

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

/**
 * No-Op RepositoryFactory.
 */
internal object NoOpRepositoryFactory : RepositoryFactory {
    override fun createUserRepository(): UserRepository = NoOpUserRepository
    override fun createChannelConfigRepository(): ChannelConfigRepository = NoOpChannelConfigRepository
    override fun createQueryChannelsRepository(): QueryChannelsRepository = NoOpQueryChannelsRepository
    override fun createSyncStateRepository(): SyncStateRepository = NoOpSyncStateRepository
    override fun createReactionRepository(
        getUser: suspend (userId: String) -> User,
    ): ReactionRepository = NoOpReactionRepository

    override fun createMessageRepository(
        getUser: suspend (userId: String) -> User,
    ): MessageRepository = NoOpMessageRepository

    override fun createChannelRepository(
        getUser: suspend (userId: String) -> User,
        getMessage: suspend (messageId: String) -> Message?,
    ): ChannelRepository = NoOpChannelRepository

    object Provider : RepositoryFactory.Provider {
        override fun createRepositoryFactory(user: User): RepositoryFactory = NoOpRepositoryFactory
    }
}
