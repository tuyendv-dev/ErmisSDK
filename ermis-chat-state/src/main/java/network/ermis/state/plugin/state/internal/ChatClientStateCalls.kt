package network.ermis.state.plugin.state.internal

import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.state.event.chat.factory.ChatEventHandlerFactory
import network.ermis.state.extensions.state
import network.ermis.state.model.QueryChannelPaginationRequest
import network.ermis.state.plugin.state.StateRegistry
import network.ermis.state.plugin.state.channel.thread.ThreadState
import network.ermis.state.plugin.state.querychannels.QueryChannelsState
import io.getstream.log.taggedLogger
import io.getstream.result.call.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first

/**
 * Adapter for [ErmisClient] that wraps some of it's request.
 */
internal class ChatClientStateCalls(
    private val chatClient: ErmisClient,
    private val scope: CoroutineScope,
) {
    private val logger by taggedLogger("Chat:ClientStateCalls")

    /**
     * Deferred value of StateRegistry.
     * It needs to be accessed after the user is connected to be sure needed plugins are initialized.
     */
    private val deferredState: Deferred<StateRegistry> = scope.async(start = CoroutineStart.LAZY) {
        chatClient.clientState.user.first { it != null }
        chatClient.state
    }

    /** Reference request of the channels query. */
    internal suspend fun queryChannels(
        request: QueryChannelsRequest,
        chatEventHandlerFactory: ChatEventHandlerFactory,
    ): QueryChannelsState {
        logger.d { "[queryChannels] request: $request" }
        chatClient.queryChannels(request).launch(scope)
        return deferredState
            .await()
            .queryChannels(request.filter, request.querySort)
            .also { queryChannelsState -> queryChannelsState.chatEventHandlerFactory = chatEventHandlerFactory }
    }

    /** Reference request of the channel query. */
    private suspend fun queryChannel(
        channelType: String,
        channelId: String,
        request: QueryChannelRequest,
    ): ChannelState {
        logger.v { "[queryChannel] cid: $channelType:$channelId, request: $request" }
        chatClient.queryChannel(channelType, channelId, request).launch(scope)
        return deferredState
            .await()
            .channel(channelType, channelId)
    }

    /** Reference request of the watch channel query. */
    internal suspend fun watchChannel(cid: String, messageLimit: Int, userPresence: Boolean): ChannelState {
        logger.d { "[watchChannel] cid: $cid, messageLimit: $messageLimit, userPresence: $userPresence" }
        val (channelType, channelId) = cid.cidToTypeAndId()
        val request = QueryChannelPaginationRequest(messageLimit)
            .toWatchChannelRequest(userPresence)
            .apply {
                this.shouldRefresh = false
                this.isWatchChannel = true
            }
        return queryChannel(channelType, channelId, request)
    }

    /** Reference request of the get thread replies query. */
    internal suspend fun getReplies(messageId: String, messageLimit: Int): ThreadState {
        logger.d { "[getReplies] messageId: $messageId, messageLimit: $messageLimit" }
        chatClient.getReplies(messageId, messageLimit).launch(scope)
        return deferredState
            .await()
            .thread(messageId)
    }

    /**
     * Fetches replies from the backend and returns them in the form of a [ThreadState].
     * Unlike [getReplies] which makes an API call and instantly returns [ThreadState], this function
     * will wait for the API call completion and then return [ThreadState].
     *
     * This is useful in situations such as when we want to focus on the last message in a thread after a PN
     * has been received, avoiding multiple [ThreadState] emissions simplifies handling it in the UI.
     *
     * @param messageId The id of the message we want to get replies for.
     * @param messageLimit The upper limit of how many replies should be fetched.
     *
     * @return The replies in the form of [ThreadState].
     */
    internal suspend fun awaitReplies(messageId: String, messageLimit: Int): ThreadState {
        logger.d { "[awaitReplies] messageId: $messageId, messageLimit: $messageLimit" }
        chatClient.getReplies(messageId, messageLimit).await()
        return deferredState
            .await()
            .thread(messageId)
    }
}
