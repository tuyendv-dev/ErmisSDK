package network.ermis.state.plugin.logic.channel

import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import network.ermis.client.ErmisClient
import network.ermis.client.api.models.Pagination
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.WatchChannelRequest
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.events.ChannelDeletedEvent
import network.ermis.client.events.ChannelHiddenEvent
import network.ermis.client.events.ChannelTruncatedEvent
import network.ermis.client.events.ChannelUpdatedByUserEvent
import network.ermis.client.events.ChannelUpdatedEvent
import network.ermis.client.events.ChannelUserBannedEvent
import network.ermis.client.events.ChannelUserUnbannedEvent
import network.ermis.client.events.ChannelVisibleEvent
import network.ermis.client.events.ChatEvent
import network.ermis.client.events.ConnectedEvent
import network.ermis.client.events.ConnectingEvent
import network.ermis.client.events.DisconnectedEvent
import network.ermis.client.events.ErrorEvent
import network.ermis.client.events.GlobalUserBannedEvent
import network.ermis.client.events.GlobalUserUnbannedEvent
import network.ermis.client.events.HealthEvent
import network.ermis.client.events.MarkAllReadEvent
import network.ermis.client.events.MemberAddedEvent
import network.ermis.client.events.MemberRemovedEvent
import network.ermis.client.events.MemberUpdatedEvent
import network.ermis.client.events.MessageDeletedEvent
import network.ermis.client.events.MessageReadEvent
import network.ermis.client.events.MessageUpdatedEvent
import network.ermis.client.events.NewMessageEvent
import network.ermis.client.events.NotificationAddedToChannelEvent
import network.ermis.client.events.NotificationChannelDeletedEvent
import network.ermis.client.events.NotificationChannelMutesUpdatedEvent
import network.ermis.client.events.NotificationChannelTruncatedEvent
import network.ermis.client.events.NotificationInviteAcceptedEvent
import network.ermis.client.events.NotificationInviteRejectedEvent
import network.ermis.client.events.NotificationInvitedEvent
import network.ermis.client.events.NotificationMarkReadEvent
import network.ermis.client.events.NotificationMarkUnreadEvent
import network.ermis.client.events.NotificationMessageNewEvent
import network.ermis.client.events.NotificationMutesUpdatedEvent
import network.ermis.client.events.NotificationRemovedFromChannelEvent
import network.ermis.client.events.ReactionDeletedEvent
import network.ermis.client.events.ReactionNewEvent
import network.ermis.client.events.ReactionUpdateEvent
import network.ermis.client.events.SignalWebrtcEvent
import network.ermis.client.events.TypingStartEvent
import network.ermis.client.events.TypingStopEvent
import network.ermis.client.events.UnknownEvent
import network.ermis.client.events.UserDeletedEvent
import network.ermis.client.events.UserPresenceChangedEvent
import network.ermis.client.events.UserStartWatchingEvent
import network.ermis.client.events.UserStopWatchingEvent
import network.ermis.client.events.UserUpdatedEvent
import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.query.pagination.AnyChannelPaginationRequest
import network.ermis.client.utils.extensions.getCreatedAtOrDefault
import network.ermis.client.utils.extensions.getCreatedAtOrNull
import network.ermis.client.utils.extensions.internal.NEVER
import network.ermis.client.utils.extensions.internal.applyPagination
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.state.event.handler.utils.toChannelUserRead
import network.ermis.state.model.QueryChannelPaginationRequest
import network.ermis.state.model.toAnyChannelPaginationRequest
import network.ermis.state.plugin.state.channel.ChannelMutableState
import java.util.Date

/**
 * This class contains all the logic to manipulate and modify the state of the corresponding channel.
 *
 * @property repos [RepositoryFacade] that interact with data sources. The this object should be used only
 * to read data and never update data as the state module should never change the database.
 * @property userPresence [Boolean] true if user presence is enabled, false otherwise.
 * @property channelStateLogic [ChannelStateLogic]
 */
@Suppress("TooManyFunctions", "LargeClass")
internal class ChannelLogic(
    private val repos: RepositoryFacade,
    private val userPresence: Boolean,
    private val channelStateLogic: ChannelStateLogic,
    private val coroutineScope: CoroutineScope,
    private val getCurrentUserId: () -> String?,
) {

    private val mutableState: ChannelMutableState = channelStateLogic.writeChannelState()
    private val logger by taggedLogger("Chat:ChannelLogicDB")

    val cid: String
        get() = mutableState.cid

    suspend fun updateStateFromDatabase(request: QueryChannelRequest) {
        logger.d { "[updateStateFromDatabase] request: $request" }
        if (request.isNotificationUpdate) return
        channelStateLogic.refreshMuteState()

        /* It is not possible to guarantee that the next page of newer messages is the same of backend,
         * so we force the backend usage */
        if (!request.isFilteringNewerMessages()) {
            runChannelQueryOffline(request)
        }
    }

    /**
     * Returns the state of Channel. Useful to check how it the state of the channel of the [ChannelLogic]
     *
     * @return [ChannelState]
     */
    internal fun state(): ChannelState = mutableState

    internal fun stateLogic(): ChannelStateLogic {
        return channelStateLogic
    }

    /**
     * Starts to watch this channel.
     *
     * @param messagesLimit The limit of messages inside the channel that should be requested.
     * @param userPresence Flag to determine if the SDK is going to receive UserPresenceChanged events. Used by the SDK to indicate if the user is online or not.
     */
    internal suspend fun watch(messagesLimit: Int = 30, userPresence: Boolean): Result<Channel> {
        logger.i { "[watch] messagesLimit: $messagesLimit, userPresence: $userPresence" }
        // Otherwise it's too easy for devs to create UI bugs which DDOS our API
        if (mutableState.loading.value) {
            logger.i { "Another request to watch this channel is in progress. Ignoring this request." }
            return Result.Failure(
                Error.GenericError(
                    "Another request to watch this channel is in progress. Ignoring this request.",
                ),
            )
        }
        return runChannelQuery(
            "watch",
            QueryChannelPaginationRequest(messagesLimit).toWatchChannelRequest(userPresence).apply {
                shouldRefresh = true
            },
        )
    }

    /**
     * Loads a list of messages after the newest message in the current list.
     *
     * @param messageId Id of message after which to fetch messages.
     * @param limit Number of messages to fetch after this message.
     *
     * @return [Result] of [Channel] with fetched messages.
     */
    internal suspend fun loadNewerMessages(messageId: String, limit: Int): Result<Channel> {
        logger.i { "[loadNewerMessages] messageId: $messageId, limit: $limit" }
        channelStateLogic.loadingNewerMessages()
        return runChannelQuery("loadNewerMessages", newerWatchChannelRequest(limit = limit, baseMessageId = messageId))
    }

    /**
     * Loads a list of messages before the message with particular message id.
     *
     * @param messageLimit Number of messages to fetch before this message.
     * @param baseMessageId Id of message before which to fetch messages. Last available message will be calculated if the parameter is null.
     *
     * @return [Result] of [Channel] with fetched messages.
     */
    internal suspend fun loadOlderMessages(messageLimit: Int, baseMessageId: String? = null): Result<Channel> {
        logger.i { "[loadOlderMessages] messageLimit: $messageLimit, baseMessageId: $baseMessageId" }
        channelStateLogic.loadingOlderMessages()
        return runChannelQuery(
            "loadOlderMessages",
            olderWatchChannelRequest(limit = messageLimit, baseMessageId = baseMessageId),
        )
    }

    internal suspend fun loadMessagesAroundId(aroundMessageId: String): Result<Channel> {
        logger.i { "[loadMessagesAroundId] aroundMessageId: $aroundMessageId" }
        return runChannelQuery("loadMessagesAroundId", aroundIdWatchChannelRequest(aroundMessageId))
    }

    private suspend fun runChannelQuery(
        src: String,
        request: WatchChannelRequest,
    ): Result<Channel> {
        logger.d { "[runChannelQuery] #$src; request: $request" }
        val loadedMessages = mutableState.messageList.value
        val offlineChannel = runChannelQueryOffline(request)

        val onlineResult = runChannelQueryOnline(request)
            .onSuccess { fillTheGap(request.messagesLimit(), loadedMessages, it.messages) }

        return when {
            onlineResult is Result.Success -> onlineResult
            offlineChannel != null -> Result.Success(offlineChannel)
            else -> onlineResult
        }
    }

    /**
     * Query the API and return a channel object.
     *
     * @param request The request object for the query.
     */
    private suspend fun runChannelQueryOnline(request: WatchChannelRequest): Result<Channel> =
        ErmisClient.instance()
            .queryChannel(mutableState.channelType, mutableState.channelId, request, skipOnRequest = true)
            .await()

    /**
     * Fills the gap between the loaded messages and the requested messages.
     * This is used to keep the messages sorted by date and avoid gaps in the pagination.
     *
     * @param messageLimit The limit of messages inside the channel that should be requested.
     * @param loadedMessages The list of messages that were loaded before the request.
     * @param requestedMessages The list of messages that were loaded by the previous request.
     */
    private fun fillTheGap(
        messageLimit: Int,
        loadedMessages: List<Message>,
        requestedMessages: List<Message>,
    ) {
        if (loadedMessages.isEmpty() || requestedMessages.isEmpty() || messageLimit <= 0) return
        coroutineScope.launch {
            val loadedMessageIds = loadedMessages
                .filter { it.getCreatedAtOrNull() != null }
                .sortedBy { it.getCreatedAtOrDefault(NEVER) }
                .map { it.id }
            val requestedMessageIds = requestedMessages
                .filter { it.getCreatedAtOrNull() != null }
                .sortedBy { it.getCreatedAtOrDefault(NEVER) }
                .map { it.id }
            val intersection = loadedMessageIds.intersect(requestedMessageIds.toSet())
            val loadedMessagesOlderDate = loadedMessages.minOf { it.getCreatedAtOrDefault(Date()) }
            val loadedMessagesNewerDate = loadedMessages.maxOf { it.getCreatedAtOrDefault(NEVER) }
            val requestedMessagesOlderDate = requestedMessages.minOf { it.getCreatedAtOrDefault(Date()) }
            val requestedMessagesNewerDate = requestedMessages.maxOf { it.getCreatedAtOrDefault(NEVER) }
            if (intersection.isEmpty()) {
                when {
                    loadedMessagesOlderDate > requestedMessagesNewerDate ->
                        runChannelQueryOnline(
                            newerWatchChannelRequest(
                                messageLimit,
                                requestedMessageIds.last(),
                            ),
                        )

                    loadedMessagesNewerDate < requestedMessagesOlderDate ->
                        runChannelQueryOnline(
                            olderWatchChannelRequest(
                                messageLimit,
                                requestedMessageIds.first(),
                            ),
                        )

                    else -> null
                }?.onSuccess { fillTheGap(messageLimit, loadedMessages, it.messages) }
            }
        }
    }

    private suspend fun runChannelQueryOffline(request: QueryChannelRequest): Channel? {
        /* It is not possible to guarantee that the next page of newer messages or the page surrounding a certain
         * message is the same as the one on backend, so we force the backend usage */
        if (request.isFilteringNewerMessages() || request.isFilteringAroundIdMessages()) return null

        return selectAndEnrichChannel(mutableState.cid, request)?.also { channel ->
            logger.v {
                "[runChannelQueryOffline] completed; channel.cid: ${channel.cid}, " +
                    "channel.messages.size: ${channel.messages.size}"
            }
            if (request.filteringOlderMessages()) {
                updateOldMessagesFromLocalChannel(channel)
            } else {
                updateDataFromLocalChannel(
                    localChannel = channel,
                    isNotificationUpdate = request.isNotificationUpdate,
                    messageLimit = request.messagesLimit(),
                    scrollUpdate = request.isFilteringMessages() && !request.isFilteringAroundIdMessages(),
                    shouldRefreshMessages = request.shouldRefresh,
                    isChannelsStateUpdate = true,
                )
            }
        }
    }

    private fun updateDataFromLocalChannel(
        localChannel: Channel,
        isNotificationUpdate: Boolean,
        messageLimit: Int,
        scrollUpdate: Boolean,
        shouldRefreshMessages: Boolean,
        isChannelsStateUpdate: Boolean = false,
    ) {
        logger.v {
            "[updateDataFromLocalChannel] localChannel.cid: ${localChannel.cid}, messageLimit: $messageLimit, " +
                "scrollUpdate: $scrollUpdate, shouldRefreshMessages: $shouldRefreshMessages, " +
                "isChannelsStateUpdate: $isChannelsStateUpdate"
        }
        localChannel.hidden?.let(channelStateLogic::toggleHidden)
        localChannel.hiddenMessagesBefore?.let(channelStateLogic::hideMessagesBefore)
        updateDataForChannel(
            localChannel,
            messageLimit = messageLimit,
            shouldRefreshMessages = shouldRefreshMessages,
            scrollUpdate = scrollUpdate,
            isNotificationUpdate = isNotificationUpdate,
            isChannelsStateUpdate = isChannelsStateUpdate,
        )
    }

    private fun updateOldMessagesFromLocalChannel(localChannel: Channel) {
        logger.v { "[updateOldMessagesFromLocalChannel] localChannel.cid: ${localChannel.cid}" }
        localChannel.hidden?.let(channelStateLogic::toggleHidden)
        channelStateLogic.updateOldMessagesFromChannel(localChannel)
    }

    private suspend fun selectAndEnrichChannel(
        channelId: String,
        pagination: QueryChannelRequest,
    ): Channel? = selectAndEnrichChannels(listOf(channelId), pagination.toAnyChannelPaginationRequest()).getOrNull(0)

    private suspend fun selectAndEnrichChannels(
        channelIds: List<String>,
        pagination: AnyChannelPaginationRequest,
    ): List<Channel> = repos.selectChannels(channelIds, pagination).applyPagination(pagination)

    internal fun updateDataForChannel(
        channel: Channel,
        messageLimit: Int,
        shouldRefreshMessages: Boolean = false,
        scrollUpdate: Boolean = false,
        isNotificationUpdate: Boolean = false,
        isChannelsStateUpdate: Boolean = false,
    ) {
        channelStateLogic.updateDataForChannel(
            channel,
            messageLimit,
            shouldRefreshMessages,
            scrollUpdate,
            isNotificationUpdate,
            isChannelsStateUpdate = isChannelsStateUpdate,
        )
    }

    internal fun deleteMessage(message: Message) {
        channelStateLogic.deleteMessage(message)
    }

    internal fun upsertMessage(message: Message) = channelStateLogic.upsertMessage(message)

    internal fun upsertMessages(messages: List<Message>) {
        channelStateLogic.upsertMessages(messages)
    }

    /**
     * Sets the date of the last message sent by the current user.
     *
     * @param lastSentMessageDate The date of the last message.
     */
    internal fun setLastSentMessageDate(lastSentMessageDate: Date?) {
        channelStateLogic.setLastSentMessageDate(lastSentMessageDate)
    }

    /**
     * Returns instance of [WatchChannelRequest] to obtain older messages of a channel.
     *
     * @param limit Message limit in this request.
     * @param baseMessageId Message id of the last available message. Request will fetch messages older than this.
     */
    private fun olderWatchChannelRequest(limit: Int, baseMessageId: String?): WatchChannelRequest =
        watchChannelRequest(Pagination.LESS_THAN, limit, baseMessageId)

    /**
     * Returns instance of [WatchChannelRequest] to obtain newer messages of a channel.
     *
     * @param limit Message limit in this request.
     * @param baseMessageId Message id of the last available message. Request will fetch messages newer than this.
     */
    private fun newerWatchChannelRequest(limit: Int, baseMessageId: String?): WatchChannelRequest =
        watchChannelRequest(Pagination.GREATER_THAN, limit, baseMessageId)

    private fun aroundIdWatchChannelRequest(aroundMessageId: String): WatchChannelRequest {
        return QueryChannelPaginationRequest().apply {
            messageFilterDirection = Pagination.AROUND_ID
            messageFilterValue = aroundMessageId
        }.toWatchChannelRequest(userPresence).apply {
            shouldRefresh = true
        }
    }

    /**
     * Creates instance of [WatchChannelRequest] according to [Pagination].
     *
     * @param pagination Pagination parameter which defines should we request older/newer messages.
     * @param limit Message limit in this request.
     * @param baseMessageId Message id of the last available. Can be null then it calculates the last available message.
     */
    private fun watchChannelRequest(pagination: Pagination, limit: Int, baseMessageId: String?): WatchChannelRequest {
        logger.d { "[watchChannelRequest] pagination: $pagination, limit: $limit, baseMessageId: $baseMessageId" }
        val messageId = baseMessageId ?: getLoadMoreBaseMessage(pagination)?.also {
            logger.v { "[watchChannelRequest] baseMessage(${it.id}): ${it.text}" }
        }?.id
        return QueryChannelPaginationRequest(limit).apply {
            messageId?.let {
                messageFilterDirection = pagination
                messageFilterValue = it
            }
        }.toWatchChannelRequest(userPresence)
    }

    /**
     * Calculates base messageId for [WatchChannelRequest] depending on [Pagination] when requesting more messages.
     *
     * @param direction [Pagination] instance which shows direction of pagination.
     */
    private fun getLoadMoreBaseMessage(direction: Pagination): Message? {
        val messages = mutableState.sortedMessages.value.takeUnless(Collection<Message>::isEmpty) ?: return null
        return when (direction) {
            Pagination.GREATER_THAN_OR_EQUAL,
            Pagination.GREATER_THAN,
            -> messages.last()
            Pagination.LESS_THAN,
            Pagination.LESS_THAN_OR_EQUAL,
            Pagination.AROUND_ID,
            -> messages.first()
        }
    }

    /**
     * Removes messages before the given date and optionally adds a system message
     * that was coming with the event.
     *
     * @param date The date used for generating result.
     * @param systemMessage The system message to display.
     */
    private fun removeMessagesBefore(date: Date, systemMessage: Message? = null) {
        channelStateLogic.removeMessagesBefore(date, systemMessage)
    }

    /**
     * Hides the messages created before the given date.
     *
     * @param date The date used for generating result.
     */
    internal fun hideMessagesBefore(date: Date) {
        channelStateLogic.hideMessagesBefore(date)
    }

    private fun upsertEventMessage(message: Message) {
        channelStateLogic.upsertMessage(
            message.copy(ownReactions = getMessage(message.id)?.ownReactions ?: message.ownReactions),
            updateCount = false,
        )
    }

    /**
     * Returns message stored in [ChannelMutableState] if exists and wasn't hidden.
     *
     * @param messageId The id of the message.
     *
     * @return [Message] if exists and wasn't hidden, null otherwise.
     */
    internal fun getMessage(messageId: String): Message? =
        mutableState.visibleMessages.value[messageId]?.copy()

    private fun upsertUserPresence(user: User) {
        channelStateLogic.upsertUserPresence(user)
    }

    private fun upsertUser(user: User) {
        upsertUserPresence(user)
    }

    /**
     * Handles events received from the socket.
     *
     * @see [handleEvent]
     */
    internal fun handleEvents(events: List<ChatEvent>) {
        for (event in events) {
            handleEvent(event)
        }
    }

    /**
     * Handles event received from the socket.
     * Responsible for synchronizing [ChannelStateLogic].
     */
    internal fun handleEvent(event: ChatEvent) {
        val currentUserId = getCurrentUserId()
        logger.d { "[handleEvent] cid: $cid, currentUserId: $currentUserId, event: $event" }
        when (event) {
            is NewMessageEvent -> {
                upsertEventMessage(event.message)
                channelStateLogic.updateCurrentUserRead(event.createdAt, event.message)
                channelStateLogic.toggleHidden(false)
            }
            is MessageUpdatedEvent -> {
                event.message.copy(
                    replyTo = event.message.replyMessageId
                        ?.let { mutableState.getMessageById(it) }
                        ?: event.message.replyTo,
                ).let(::upsertEventMessage)

                channelStateLogic.toggleHidden(false)
            }
            is MessageDeletedEvent -> {
                if (event.hardDelete) {
                    deleteMessage(event.message)
                } else {
                    upsertEventMessage(event.message)
                }
                channelStateLogic.toggleHidden(false)
            }
            is NotificationMessageNewEvent -> {
                if (!mutableState.insideSearch.value) {
                    upsertEventMessage(event.message)
                }
                channelStateLogic.updateCurrentUserRead(event.createdAt, event.message)
                channelStateLogic.toggleHidden(false)
            }
            is ReactionNewEvent -> {
                upsertEventMessage(event.message)
            }
            is ReactionUpdateEvent -> {
                upsertEventMessage(event.message)
            }
            is ReactionDeletedEvent -> {
                upsertEventMessage(event.message)
            }
            is MemberRemovedEvent -> {
                if (event.user.id == currentUserId) {
                    logger.i { "[handleEvent] skip MemberRemovedEvent for currentUser" }
                    return
                }
                channelStateLogic.deleteMember(event.member)
            }
            is NotificationRemovedFromChannelEvent -> {
                channelStateLogic.setMembers(event.channel.members, event.channel.memberCount)
                channelStateLogic.setWatchers(event.channel.watchers, event.channel.watcherCount)
            }
            is MemberAddedEvent -> {
                channelStateLogic.addMember(event.member)
            }
            is MemberUpdatedEvent -> {
                channelStateLogic.upsertMember(event.member)
            }
            is NotificationAddedToChannelEvent -> {
                channelStateLogic.upsertMembers(event.channel.members)
            }
            is UserPresenceChangedEvent -> {
                upsertUserPresence(event.user)
            }
            is UserUpdatedEvent -> {
                upsertUser(event.user)
            }
            is UserStartWatchingEvent -> {
                channelStateLogic.upsertWatcher(event)
            }
            is UserStopWatchingEvent -> {
                channelStateLogic.deleteWatcher(event)
            }
            is ChannelUpdatedEvent -> {
                channelStateLogic.updateChannelData(event.channel)
            }
            is ChannelUpdatedByUserEvent -> {
                channelStateLogic.updateChannelData(event.channel)
            }
            is ChannelHiddenEvent -> {
                channelStateLogic.toggleHidden(true)
            }
            is ChannelVisibleEvent -> {
                channelStateLogic.toggleHidden(false)
            }
            is ChannelDeletedEvent -> {
                removeMessagesBefore(event.createdAt)
                channelStateLogic.deleteChannel(event.createdAt)
            }
            is ChannelTruncatedEvent -> {
                removeMessagesBefore(event.createdAt, event.message)
            }
            is NotificationChannelTruncatedEvent -> {
                removeMessagesBefore(event.createdAt)
            }
            is TypingStopEvent -> {
                channelStateLogic.setTyping(event.user.id, null)
            }
            is TypingStartEvent -> {
                channelStateLogic.setTyping(event.user.id, event)
            }
            is MessageReadEvent -> {
                channelStateLogic.updateRead(event.toChannelUserRead())
            }
            is NotificationMarkReadEvent -> {
                channelStateLogic.updateRead(event.toChannelUserRead())
            }
            is MarkAllReadEvent -> {
                channelStateLogic.updateRead(event.toChannelUserRead())
            }
            is NotificationMarkUnreadEvent -> {
                channelStateLogic.updateRead(event.toChannelUserRead())
            }
            is NotificationInviteAcceptedEvent -> {
                channelStateLogic.addMember(event.member)
                channelStateLogic.updateChannelData(event.channel)
            }
            is NotificationInviteRejectedEvent -> {
                channelStateLogic.deleteMember(event.member)
                channelStateLogic.updateChannelData(event.channel)
            }
            is NotificationChannelMutesUpdatedEvent -> {
                event.me.channelMutes.any { mute ->
                    mute.channel.cid == mutableState.cid
                }.let(channelStateLogic::updateMute)
            }
            is ChannelUserBannedEvent -> {
                channelStateLogic.updateMemberBanned(
                    memberUserId = event.user.id,
                    banned = true,
                    shadow = event.shadow,
                )
            }
            is ChannelUserUnbannedEvent -> {
                channelStateLogic.updateMemberBanned(
                    memberUserId = event.user.id,
                    banned = false,
                    shadow = false,
                )
            }
            is NotificationChannelDeletedEvent,
            is NotificationInvitedEvent,
            is ConnectedEvent,
            is ConnectingEvent,
            is DisconnectedEvent,
            is ErrorEvent,
            is GlobalUserBannedEvent,
            is GlobalUserUnbannedEvent,
            is HealthEvent,
            is NotificationMutesUpdatedEvent,
            is UnknownEvent,
            is UserDeletedEvent,
            is SignalWebrtcEvent,
            -> Unit // Ignore these events
        }
    }

    fun toChannel(): Channel = mutableState.toChannel()

    internal fun replyMessage(repliedMessage: Message?) {
        channelStateLogic.replyMessage(repliedMessage)
    }
}
