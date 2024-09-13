package network.ermis.client.persistance.factory

import network.ermis.client.persistance.ChannelConfigRepository
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.QueryChannelsRepository
import network.ermis.client.persistance.ReactionRepository
import network.ermis.client.persistance.SyncStateRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.core.models.Message
import network.ermis.core.models.User

/**
 * Factory that creates all repositories of SDK.
 */
public interface RepositoryFactory {

    /**
     * Creates [UserRepository]
     */
    public fun createUserRepository(): UserRepository

    /**
     * Creates [ChannelConfigRepository]
     */
    public fun createChannelConfigRepository(): ChannelConfigRepository

    /**
     * Creates [ChannelRepository]
     *
     * @param getUser function that provides userId.
     * @param getMessage function that provides messageId.
     */
    public fun createChannelRepository(
        getUser: suspend (userId: String) -> User,
        getMessage: suspend (messageId: String) -> Message?,
    ): ChannelRepository

    /**
     * Creates [QueryChannelsRepository]
     */
    public fun createQueryChannelsRepository(): QueryChannelsRepository

    /**
     * Creates [MessageRepository]
     *
     * @param getUser function that provides userId.
     */
    public fun createMessageRepository(
        getUser: suspend (userId: String) -> User,
    ): MessageRepository

    /**
     * Creates [ReactionRepository]
     *
     * @param getUser function that provides userId.
     */
    public fun createReactionRepository(getUser: suspend (userId: String) -> User): ReactionRepository

    /**
     * Creates [SyncStateRepository]
     */
    public fun createSyncStateRepository(): SyncStateRepository

    /**
     * Interface to delegate creation of [RepositoryFactory].
     */
    public interface Provider {

        /**
         * Create a [RepositoryFactory] for the given [User].
         */
        public fun createRepositoryFactory(user: User): RepositoryFactory
    }
}
