package network.ermis.state.event.handler

import io.getstream.log.StreamLog
import io.getstream.log.taggedLogger
import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.utils.extensions.internal.updateLastMessage
import network.ermis.client.utils.extensions.internal.updateUsers
import network.ermis.client.utils.extensions.internal.users
import network.ermis.client.utils.message.latestOrNull
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.state.plugin.state.global.GlobalState
import java.util.Date

/**
 * EventBatchUpdate helps you efficiently implement a 4 step batch update process
 * It updates multiple messages, users and channels at once.
 *
 * val batchBuilder = EventBatchUpdate.Builder()
 *
 * as a first step specify which channels and messages to fetch
 * batchBuilder.addToFetchChannels()
 * batchBuilder.addToFetchMessages()
 *
 * as a second step, load the required data for batch updating using
 * val batch = batchBuilder.build(domainImpl)
 *
 * third, add the required updates via
 * batch.addUser, addChannel and addMessage methods
 *
 * fourth, execute the batch using
 * batch.execute()
 */
@Suppress("LongParameterList")
internal class EventBatchUpdate private constructor(
    private val id: Int,
    private val currentUserId: String,
    private val globalState: GlobalState,
    private val repos: RepositoryFacade,
    private val channelMap: MutableMap<String, Channel>,
    private val messageMap: MutableMap<String, Message>,
    private val userMap: MutableMap<String, User>,
) {

    private val logger by taggedLogger(TAG)

    /**
     * Adds the message and updates the last message for the given channel.
     * Increments the unread count if the right conditions apply.
     */
    fun addMessageData(receivedEventDate: Date, cid: String, message: Message) {
        addMessage(message)
        getCurrentChannel(cid)
            ?.updateLastMessage(receivedEventDate, message, currentUserId)
            ?.let(::addChannel)
    }

    fun addChannel(channel: Channel) {
        logger.v {
            "[addChannel] id: $id" +
                ", channel.lastMessageAt: ${channel.lastMessageAt}" +
                ", channel.latestMessageId: ${channel.messages.latestOrNull()?.id}"
        }
        // ensure we store all users for this channel
        addUsers(channel.users())
        // TODO: this overwrites members which in the case when you have > 100 members isn't the right behaviour
        channelMap += (channel.cid to channel)
    }

    fun getCurrentChannel(cId: String): Channel? = channelMap[cId]
    fun getCurrentMessage(messageId: String): Message? = messageMap[messageId]

    fun addMessage(message: Message) {
        // ensure we store all users for this channel
        addUsers(message.users())
        messageMap += (message.id to message)
    }

    fun addUsers(newUsers: List<User>) {
        newUsers.forEach { user ->
            if (userMap.containsKey(user.id).not()) {
                userMap[user.id] = user
            }
        }
    }

    fun addUser(newUser: User) {
        userMap += (newUser.id to newUser)
    }

    suspend fun execute() {
        // actually insert the data
        currentUserId?.let { userMap -= it }
        // TODO delete if regression goes well
        // enrichChannelsWithCapabilities()
        logger.v { "[execute] id: $id, channelMap.size: ${channelMap.size}" }

        repos.insertUsers(userMap.values.toList())
        repos.insertChannels(channelMap.values.updateUsers(userMap))
        repos.insertMessages(messageMap.values.toList().updateUsers(userMap))
    }

    /**
     * Enriches channels with capabilities if needed.
     * Channels from events don't contain ownCapabilities field therefore,
     * they need to be enriched based on capabilities stored in the cache.
     */
    private suspend fun enrichChannelsWithCapabilities() {
        val channelsWithoutCapabilities = channelMap.values
            .filter { channel -> channel.ownCapabilities.isEmpty() }
            .map { channel -> channel.cid }
        val cachedChannels = repos.selectChannels(channelsWithoutCapabilities)
        logger.v { "[enrichChannelsWithCapabilities] id: $id, cachedChannels.size: ${cachedChannels.size}" }
        // TODO the logic below seems to be wrong, we should be adding capabilities to the channels from
        //  the channelMap, otherwise we just replaced the channels in channelMap with the cachedChannels
        //  which may be wrong, cause we may have some changes in the channelMap that we want to keep.
        //
        // FIXME
        //  For instance this breaks the logic removing a member from a channel, cachedChannels will have
        //  the stale member list, and we will lose the member removal.
        //
        // We should be adding the capabilities from the cachedChannels to the channels in channelMap
        channelMap.putAll(cachedChannels.associateBy(Channel::cid))
    }

    internal class Builder(
        private val id: Int,
    ) {
        private val channelsToFetch = mutableSetOf<String>()
        private val channelsToRemove = mutableSetOf<String>()
        private val messagesToFetch = mutableSetOf<String>()
        private val users = mutableSetOf<User>()

        fun addToFetchChannels(cIds: List<String>) {
            channelsToFetch += cIds
        }

        fun addToRemoveChannels(cIds: List<String>) {
            channelsToRemove += cIds
        }

        fun addToFetchChannels(cId: String) {
            channelsToFetch += cId
        }

        fun addToFetchMessages(ids: List<String>) {
            messagesToFetch += ids
        }

        fun addToFetchMessages(id: String) {
            messagesToFetch += id
        }

        fun addUsers(usersToAdd: List<User>) {
            users += usersToAdd
        }

        suspend fun build(
            globalState: GlobalState,
            repos: RepositoryFacade,
            currentUserId: String,
        ): EventBatchUpdate {
            channelsToRemove.forEach { repos.deleteChannel(it) }
            // Update users in DB in order to fetch channels and messages with sync data.
            repos.insertUsers(users)
            val messageMap: Map<String, Message> =
                repos.selectMessages(messagesToFetch.toList()).associateBy(Message::id)
            val channelMap: Map<String, Channel> =
                repos.selectChannels(channelsToFetch.toList()).associateBy(Channel::cid)
            StreamLog.v(TAG) {
                "[builder.build] id: $id, messageMap.size: ${messageMap.size}" +
                    ", channelMap.size: ${channelMap.size}"
            }
            return EventBatchUpdate(
                id,
                currentUserId,
                globalState,
                repos,
                channelMap.toMutableMap(),
                messageMap.toMutableMap(),
                users.associateBy(User::id).toMutableMap(),
            )
        }
    }

    private companion object {
        private const val TAG = "Chat:EventBatchUpdate"
    }
}
