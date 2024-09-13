package network.ermis.client.persistance

import androidx.annotation.VisibleForTesting
import network.ermis.client.utils.extensions.enrichWithCid
import network.ermis.client.utils.extensions.internal.users
import network.ermis.client.persistance.factory.RepositoryFactory
import network.ermis.client.query.pagination.AnyChannelPaginationRequest
import network.ermis.client.query.pagination.isRequestingMoreThanLastMessage
import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelConfig
import network.ermis.core.models.Config
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.Date

@SuppressWarnings("LongParameterList")
public class RepositoryFacade private constructor(
    private val userRepository: UserRepository,
    private val configsRepository: ChannelConfigRepository,
    private val channelsRepository: ChannelRepository,
    private val queryChannelsRepository: QueryChannelsRepository,
    private val messageRepository: MessageRepository,
    private val reactionsRepository: ReactionRepository,
    private val syncStateRepository: SyncStateRepository,
    private val scope: CoroutineScope,
    private val defaultConfig: Config,
) : UserRepository by userRepository,
    ChannelRepository by channelsRepository,
    ReactionRepository by reactionsRepository,
    MessageRepository by messageRepository,
    ChannelConfigRepository by configsRepository,
    QueryChannelsRepository by queryChannelsRepository,
    SyncStateRepository by syncStateRepository {

    private val logger by taggedLogger("Chat:RepositoryFacade")

    override suspend fun selectChannels(channelCIDs: List<String>): List<Channel> =
        selectChannels(channelCIDs, null)

    public suspend fun selectChannels(
        channelIds: List<String>,
        pagination: AnyChannelPaginationRequest?,
    ): List<Channel> {
        // fetch the channel entities from room
        val channels = channelsRepository.selectChannels(channelIds)
        // TODO why it is not compared this way?
        //  pagination?.isRequestingMoreThanLastMessage() == true
        val messagesMap = if (pagination?.isRequestingMoreThanLastMessage() != false) {
            // with postgres this could be optimized into a single query instead of N, not sure about sqlite on android
            // sqlite has window functions: https://sqlite.org/windowfunctions.html
            // but android runs a very dated version: https://developer.android.com/reference/android/database/sqlite/package-summary
            channelIds.map { cid ->
                scope.async { cid to selectMessagesForChannel(cid, pagination) }
            }.awaitAll().toMap()
        } else {
            emptyMap()
        }

        return channels.map { channel ->
            channel.enrichChannel(messagesMap, defaultConfig)
        }
    }

    @VisibleForTesting
    public fun Channel.enrichChannel(messageMap: Map<String, List<Message>>, defaultConfig: Config): Channel = copy(
        config = selectChannelConfig(type)?.config ?: defaultConfig,
        messages = if (messageMap.containsKey(cid)) {
            val fullList = (messageMap[cid] ?: error("Messages must be in the map")) + messages
            fullList.distinctBy(Message::id)
        } else {
            messages
        },
    )

    override suspend fun insertChannel(channel: Channel) {
        insertUsers(channel.let(Channel::users))
        channelsRepository.insertChannel(channel)
    }

    override suspend fun insertChannels(channels: Collection<Channel>) {
        insertUsers(channels.flatMap(Channel::users))
        channelsRepository.insertChannels(channels)
    }

    override suspend fun insertMessage(message: Message) {
        insertUsers(message.users())
        messageRepository.insertMessage(message)
    }

    override suspend fun insertMessages(messages: List<Message>) {
        insertUsers(messages.flatMap(Message::users))
        messageRepository.insertMessages(messages)
    }

    /**
     * Deletes channel messages before [hideMessagesBefore] and removes channel from the cache.
     */
    override suspend fun deleteChannelMessagesBefore(cid: String, hideMessagesBefore: Date) {
        messageRepository.deleteChannelMessagesBefore(cid, hideMessagesBefore)
    }

    override suspend fun deleteChannelMessage(message: Message) {
        messageRepository.deleteChannelMessage(message)
        channelsRepository.deleteChannelMessage(message)
    }

    override suspend fun insertReaction(reaction: Reaction) {
        val messageId = reaction.messageId
        if (messageId.isEmpty()) {
            logger.w { "[insertReaction] rejected (message id cannot be empty)" }
            return
        }
        val user = reaction.user
        if (user == null) {
            logger.w { "[insertReaction] rejected (user cannot be null)" }
            return
        }
        if (messageRepository.selectMessage(messageId) == null) {
            logger.w { "[insertReaction] rejected (message cannot be found in local DB)" }
            return
        }
        logger.d { "[insertReaction] reaction: ${reaction.type}, messageId: $messageId" }
        insertUser(user)
        reactionsRepository.insertReaction(reaction)
    }

    override suspend fun updateMembersForChannel(cid: String, members: List<Member>) {
        insertUsers(members.map(Member::user))
        channelsRepository.updateMembersForChannel(cid, members)
    }

    public suspend fun storeStateForChannels(channels: Collection<Channel>) {
        insertChannelConfigs(channels.map { ChannelConfig(it.type, it.config) })
        insertChannels(channels)
        insertMessages(
            channels.flatMap { channel ->
                channel.messages.map { it.enrichWithCid(channel.cid) }
            },
        )
    }

    override suspend fun deleteChannel(cid: String) {
        channelsRepository.deleteChannel(cid)
        messageRepository.deleteChannelMessages(cid)
    }

    public suspend fun storeStateForChannel(channel: Channel) {
        storeStateForChannels(listOf(channel))
    }

    override suspend fun clear() {
        userRepository.clear()
        channelsRepository.clear()
        reactionsRepository.clear()
        messageRepository.clear()
        configsRepository.clear()
        queryChannelsRepository.clear()
        syncStateRepository.clear()
    }

    public companion object {

        /**
         * Creates a new instance of [RepositoryFacade] and populate the Singleton instance. This method should be
         * used mainly for tests or internally by other constructor methods.
         *
         * @param factory [RepositoryFacade]
         * @param scope [CoroutineScope]
         * @param defaultConfig [Config]
         */
        public fun create(
            factory: RepositoryFactory,
            scope: CoroutineScope,
            defaultConfig: Config = Config(),
        ): RepositoryFacade {
            val userRepository = factory.createUserRepository()
            val getUser: suspend (userId: String) -> User = { userId ->
                requireNotNull(userRepository.selectUser(userId)) {
                    "User with the userId: `$userId` has not been found"
                }
            }

            val messageRepository = factory.createMessageRepository(getUser)
            val getMessage: suspend (messageId: String) -> Message? = messageRepository::selectMessage

            return RepositoryFacade(
                userRepository = userRepository,
                configsRepository = factory.createChannelConfigRepository(),
                channelsRepository = factory.createChannelRepository(getUser, getMessage),
                queryChannelsRepository = factory.createQueryChannelsRepository(),
                messageRepository = messageRepository,
                reactionsRepository = factory.createReactionRepository(getUser),
                syncStateRepository = factory.createSyncStateRepository(),
                scope = scope,
                defaultConfig = defaultConfig,
            )
        }
    }
}
