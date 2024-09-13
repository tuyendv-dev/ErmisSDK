package network.ermis.client.plugin

import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.errorhandler.ErrorHandler
import network.ermis.client.events.ChatEvent
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.User
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.result.Result
import java.util.Date

/**
 * Plugin is an extension for [ErmisClient].
 */
@Suppress("TooManyFunctions")
public interface Plugin :
    DependencyResolver,
    network.ermis.client.plugin.listeners.QueryMembersListener,
    network.ermis.client.plugin.listeners.DeleteReactionListener,
    network.ermis.client.plugin.listeners.SendReactionListener,
    network.ermis.client.plugin.listeners.ThreadQueryListener,
    network.ermis.client.plugin.listeners.SendGiphyListener,
    network.ermis.client.plugin.listeners.ShuffleGiphyListener,
    network.ermis.client.plugin.listeners.DeleteMessageListener,
    network.ermis.client.plugin.listeners.SendMessageListener,
    network.ermis.client.plugin.listeners.SendAttachmentListener,
    network.ermis.client.plugin.listeners.EditMessageListener,
    network.ermis.client.plugin.listeners.QueryChannelListener,
    network.ermis.client.plugin.listeners.QueryChannelsListener,
    network.ermis.client.plugin.listeners.TypingEventListener,
    network.ermis.client.plugin.listeners.HideChannelListener,
    network.ermis.client.plugin.listeners.MarkAllReadListener,
    network.ermis.client.plugin.listeners.ChannelMarkReadListener,
    network.ermis.client.plugin.listeners.CreateChannelListener,
    network.ermis.client.plugin.listeners.DeleteChannelListener,
    network.ermis.client.plugin.listeners.GetMessageListener,
    network.ermis.client.plugin.listeners.FetchCurrentUserListener {

    public val errorHandler: ErrorHandler?

    override suspend fun onQueryMembersResult(
        result: Result<List<Member>>,
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    ) {
        /* No-Op */
    }

    override suspend fun onQueryUserListResult(result: Result<List<User>>) {
        /* No-Op */
    }

    override suspend fun onGetInfoCurrentUserResult(result: Result<User>) {
        /* No-Op */
    }

    override suspend fun getUserDB(userId: String): Result<User> = Result.Success(User(id = userId))

    override suspend fun onDeleteReactionRequest(
        cid: String?,
        messageId: String,
        reactionType: String,
        currentUser: User,
    ) {
        /* No-Op */
    }

    override suspend fun onDeleteReactionResult(
        cid: String?,
        messageId: String,
        reactionType: String,
        currentUser: User,
        result: Result<Message>,
    ) {
        /* No-Op */
    }

    override fun onDeleteReactionPrecondition(currentUser: User?): Result<Unit> = Result.Success(Unit)

    override suspend fun onSendReactionRequest(
        cid: String?,
        reaction: Reaction,
        enforceUnique: Boolean,
        currentUser: User,
    ) {
        /* No-Op */
    }

    override suspend fun onSendReactionResult(
        cid: String?,
        reaction: Reaction,
        enforceUnique: Boolean,
        currentUser: User,
        result: Result<Reaction>,
    ) {
        /* No-Op */
    }

    override suspend fun onSendReactionPrecondition(
        currentUser: User?,
        reaction: Reaction,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onGetRepliesPrecondition(
        messageId: String,
        limit: Int,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onGetRepliesRequest(
        messageId: String,
        limit: Int,
    ) {
        /* No-Op */
    }

    override suspend fun onGetRepliesResult(
        result: Result<List<Message>>,
        messageId: String,
        limit: Int,
    ) {
        /* No-Op */
    }

    override suspend fun onGetRepliesMorePrecondition(
        messageId: String,
        firstId: String,
        limit: Int,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onGetRepliesMoreRequest(
        messageId: String,
        firstId: String,
        limit: Int,
    ) {
        /* No-Op */
    }

    override suspend fun onGetRepliesMoreResult(
        result: Result<List<Message>>,
        messageId: String,
        firstId: String,
        limit: Int,
    ) {
        /* No-Op */
    }

    override fun onGiphySendResult(cid: String, result: Result<Message>) {
        /* No-Op */
    }

    override suspend fun onShuffleGiphyResult(cid: String, result: Result<Message>) {
        /* No-Op */
    }

    override suspend fun onMessageDeletePrecondition(messageId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun onMessageDeleteRequest(messageId: String) {
        /* No-Op */
    }

    override suspend fun onMessageDeleteResult(
        originalMessageId: String,
        result: Result<Message>,
    ) {
        /* No-Op */
    }

    override suspend fun onMessageSendResult(
        result: Result<Message>,
        channelType: String,
        channelId: String,
        message: Message,
    ) {
        /* No-Op */
    }

    override suspend fun onMessageEditRequest(message: Message) {
        /* No-Op */
    }

    override suspend fun onMessageEditResult(originalMessage: Message, result: Result<Message>) {
        /* No-Op */
    }

    override suspend fun onQueryChannelPrecondition(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onQueryChannelRequest(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ) {
        /* No-Op */
    }

    override suspend fun onQueryChannelResult(
        result: Result<Channel>,
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ) {
        /* No-Op */
    }

    override suspend fun onQueryChannelsPrecondition(request: QueryChannelsRequest): Result<Unit> =
        Result.Success(Unit)

    override suspend fun onQueryChannelsRequest(request: QueryChannelsRequest) {
        /* No-Op */
    }

    override suspend fun onQueryChannelsResult(
        result: Result<List<Channel>>,
        request: QueryChannelsRequest,
    ) {
        /* No-Op */
    }

    override fun onTypingEventPrecondition(
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
        eventTime: Date,
    ): Result<Unit> = Result.Success(Unit)

    override fun onTypingEventRequest(
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
        eventTime: Date,
    ) {
        /* No-Op */
    }

    override fun onTypingEventResult(
        result: Result<ChatEvent>,
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
        eventTime: Date,
    ) {
        /* No-Op */
    }

    override suspend fun onHideChannelPrecondition(
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onHideChannelRequest(
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ) {
        /* No-Op */
    }

    override suspend fun onHideChannelResult(
        result: Result<Unit>,
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ) {
        /* No-Op */
    }

    override suspend fun onMarkAllReadRequest() {
        /* No-Op */
    }

    override suspend fun onChannelMarkReadPrecondition(
        channelType: String,
        channelId: String,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onCreateChannelRequest(
        channelType: String,
        channelId: String,
        memberIds: List<String>,
        extraData: Map<String, Any>,
        currentUser: User,
    ) {
        /* No-Op */
    }

    override suspend fun onCreateChannelResult(
        channelType: String,
        channelId: String,
        memberIds: List<String>,
        result: Result<Channel>,
    ) {
        /* No-Op */
    }

    override fun onCreateChannelPrecondition(
        currentUser: User?,
        channelId: String,
        memberIds: List<String>,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onDeleteChannelRequest(
        currentUser: User?,
        channelType: String,
        channelId: String,
    ) {
        /* No-Op */
    }

    override suspend fun onDeleteChannelResult(
        channelType: String,
        channelId: String,
        result: Result<Channel>,
    ) {
        /* No-Op */
    }

    override suspend fun onDeleteChannelPrecondition(
        currentUser: User?,
        channelType: String,
        channelId: String,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun onAttachmentSendRequest(channelType: String, channelId: String, message: Message) {
        /* No-Op */
    }

    public fun onUserSet(user: User)

    public fun onUserDisconnected()

    public override suspend fun onGetMessageResult(
        messageId: String,
        result: Result<Message>,
    ) {
        /* No-Op */
    }

    public override suspend fun onFetchCurrentUserResult(
        result: Result<User>,
    ) {
        /* No-Op */
    }
}
