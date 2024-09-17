@file:JvmName("ChatClientExtensions")

package network.ermis.state.extensions

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.annotation.CheckResult
import io.getstream.log.StreamLog
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.CoroutineCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.client.utils.internal.validateCidWithResult
import network.ermis.client.utils.message.isEphemeral
import network.ermis.core.internal.coroutines.DispatcherProvider
import network.ermis.core.models.Attachment
import network.ermis.core.models.Channel
import network.ermis.core.models.InitializationState
import network.ermis.core.models.Message
import network.ermis.state.event.chat.ChatEventHandler
import network.ermis.state.event.chat.factory.ChatEventHandlerFactory
import network.ermis.state.plugin.config.StatePluginConfig
import network.ermis.state.plugin.factory.StreamStatePluginFactory
import network.ermis.state.plugin.internal.StatePlugin
import network.ermis.state.plugin.logic.LogicRegistry
import network.ermis.state.plugin.state.StateRegistry
import network.ermis.state.plugin.state.channel.thread.ThreadState
import network.ermis.state.plugin.state.global.GlobalState
import network.ermis.state.plugin.state.internal.ChatClientStateCalls
import network.ermis.state.plugin.state.querychannels.QueryChannelsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "Chat:Client-StatePlugin"

/**
 * [StateRegistry] instance that contains all state objects exposed in offline plugin.
 * The instance is being initialized after connecting the user!
 *
 * @throws IllegalArgumentException If the state was not initialized yet.
 */
public val ErmisClient.state: StateRegistry
    @Throws(IllegalArgumentException::class)
    get() = resolveDependency<StatePlugin, StateRegistry>()

/**
 * [GlobalState] instance that contains information about the current user, unreads, etc.
 *
 * @throws IllegalArgumentException If the GlobalState was not initialized yet.
 */
public val ErmisClient.globalState: GlobalState
    @Throws(IllegalArgumentException::class)
    get() = resolveDependency<StatePlugin, GlobalState>()

/**
 * [StatePluginConfig] instance used to configure [io.getstream.chat.android.state.plugin.internal.StatePlugin].
 *
 * @throws IllegalArgumentException If the StatePluginConfig was not initialized yet.
 */
internal val ErmisClient.stateConfig: StatePluginConfig
    get() = resolveDependency<StreamStatePluginFactory, StatePluginConfig>()

/**
 * Performs [ErmisClient.queryChannels] under the hood and returns [QueryChannelsState] associated with the query.
 * The [QueryChannelsState] cannot be created before connecting the user therefore, the method returns a StateFlow
 * that emits a null when the user has not been connected yet and the new value every time the user changes.
 *
 * You can pass option [chatEventHandlerFactory] parameter which will be associated with this query channels request.
 *
 * @see [ChatEventHandler]
 *
 * @param request The request's parameters combined into [QueryChannelsRequest] class.
 * @param chatEventHandlerFactory The instance of [ChatEventHandlerFactory] that will be used to create [ChatEventHandler].
 * @param coroutineScope The [CoroutineScope] used for executing the request.
 *
 * @return A StateFlow object that emits a null when the user has not been connected yet and the new [QueryChannelsState] when the user changes.
 */
@JvmOverloads
public fun ErmisClient.queryChannelsAsState(
    request: QueryChannelsRequest,
    chatEventHandlerFactory: ChatEventHandlerFactory = ChatEventHandlerFactory(clientState),
    coroutineScope: CoroutineScope = CoroutineScope(DispatcherProvider.IO),
): StateFlow<QueryChannelsState?> {
    StreamLog.d(TAG) { "[queryChannelsAsState] request: $request" }
    return getStateOrNull(coroutineScope) {
        requestsAsState(coroutineScope).queryChannels(request, chatEventHandlerFactory)
    }
}

/**
 * Performs [ErmisClient.queryChannel] with watch = true under the hood and returns [ChannelState] associated with the query.
 * The [ChannelState] cannot be created before connecting the user therefore, the method returns a StateFlow
 * that emits a null when the user has not been connected yet and the new value every time the user changes.
 *
 * @param cid The full channel id, i.e. "messaging:123"
 * @param messageLimit The number of messages that will be initially loaded.
 * @param coroutineScope The [CoroutineScope] used for executing the request.
 *
 * @return A StateFlow object that emits a null when the user has not been connected yet and the new [ChannelState] when the user changes.
 */
@JvmOverloads
public fun ErmisClient.watchChannelAsState(
    cid: String,
    messageLimit: Int,
    coroutineScope: CoroutineScope = CoroutineScope(DispatcherProvider.IO),
): StateFlow<ChannelState?> {
    StreamLog.i(TAG) { "[watchChannelAsState] cid: $cid, messageLimit: $messageLimit" }
    return getStateOrNull(coroutineScope) {
        requestsAsState(coroutineScope).watchChannel(cid, messageLimit, stateConfig.userPresence)
    }
}

/**
 * Same class of ChatClient.getReplies, but provides the result as [ThreadState]
 *
 * @param messageId The ID of the original message the replies were made to.
 * @param messageLimit The number of messages that will be initially loaded.
 * @param coroutineScope The [CoroutineScope] used for executing the request.
 *
 * @return [ThreadState]
 */
@JvmOverloads
public suspend fun ErmisClient.getRepliesAsState(
    messageId: String,
    messageLimit: Int,
    coroutineScope: CoroutineScope = CoroutineScope(DispatcherProvider.IO),
): ThreadState {
    StreamLog.d(TAG) { "[getRepliesAsState] messageId: $messageId, messageLimit: $messageLimit" }
    return requestsAsState(coroutineScope).getReplies(messageId, messageLimit)
}

/**
 * Returns thread replies in the form of [ThreadState], however, unlike [getRepliesAsState]
 * it will return it only after the API call made to get replies has ended. Thread state
 * will be returned regardless if the API call has succeeded or failed, the only difference is
 * in how up to date the replies in the thread state are.
 *
 * @param messageId The ID of the original message the replies were made to.
 * @param messageLimit The number of messages that will be initially loaded.
 * @param coroutineScope The [CoroutineScope] used for executing the request.
 *
 * @return [ThreadState] wrapped inside a [Call].
 */
public suspend fun ErmisClient.awaitRepliesAsState(
    messageId: String,
    messageLimit: Int,
): ThreadState {
    StreamLog.d(TAG) { "[awaitRepliesAsState] messageId: $messageId, messageLimit: $messageLimit" }
    return coroutineScope {
        requestsAsState(scope = this).awaitReplies(messageId, messageLimit)
    }
}

/**
 * Provides an ease-of-use piece of functionality that checks if the user is available or not. If it's not, we don't emit
 * any state, but rather return an empty StateFlow.
 *
 * If the user is set, we fetch the state using the provided operation and provide it to the user.
 */
private fun <T> ErmisClient.getStateOrNull(
    coroutineScope: CoroutineScope,
    producer: suspend () -> T,
): StateFlow<T?> {
    return clientState.initializationState.combine(clientState.user) { initializationState, user ->
        if (initializationState == InitializationState.COMPLETE && user != null) {
            producer()
        } else {
            null
        }
    }.distinctUntilChanged().stateIn(coroutineScope, SharingStarted.Eagerly, null)
}

/**
 * Set the reply state for the channel.
 *
 * @param cid CID of the channel where reply state is being set.
 * @param message The message we want reply to. The null value means dismiss reply state.
 *
 * @return Executable async [Call].
 */
@CheckResult
public fun ErmisClient.setMessageForReply(cid: String, message: Message?): Call<Unit> {
    return CoroutineCall(inheritScope { Job(it) }) {
        when (val cidValidationResult = validateCidWithResult(cid)) {
            is Result.Success -> {
                val (channelType, channelId) = cid.cidToTypeAndId()
                state.mutableChannel(channelType = channelType, channelId = channelId).run {
                    setRepliedMessage(message)
                }
                Result.Success(Unit)
            }
            is Result.Failure -> cidValidationResult
        }
    }
}

/**
 * Downloads the selected attachment to the "Download" folder in the public external storage directory.
 *
 * @param attachment The attachment to download.
 *
 * @return Executable async [Call] downloading attachment.
 */
@CheckResult
public fun ErmisClient.downloadAttachment(context: Context, attachment: Attachment): Call<Unit> {
    return CoroutineCall(inheritScope { Job(it) }) {
        val logger by taggedLogger("Chat:DownloadAttachment")

        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val url = attachment.assetUrl ?: attachment.imageUrl
            val subPath = attachment.name ?: attachment.title ?: attachment.parseAttachmentNameFromUrl()
                ?: createAttachmentFallbackName()

            logger.d { "Downloading attachment. Name: $subPath, Url: $url" }

            downloadManager.enqueue(
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(subPath)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED),
            )
            Result.Success(Unit)
        } catch (exception: Exception) {
            logger.d { "Downloading attachment failed. Error: ${exception.message}" }
            Result.Failure(Error.ThrowableError(message = "Could not download the attachment", cause = exception))
        }
    }
}

/**
 * Loads older messages for the channel.
 *
 * @param cid The full channel id i.e. "messaging:123".
 * @param messageLimit How many new messages to load.
 *
 * @return The channel wrapped in [Call]. This channel contains older requested messages.
 */
public fun ErmisClient.loadOlderMessages(cid: String, messageLimit: Int): Call<Channel> {
    StreamLog.d(TAG) { "[loadOlderMessages] cid: $cid, messageLimit: $messageLimit" }
    return CoroutineCall(inheritScope { Job(it) }) {
        when (val cidValidationResult = validateCidWithResult(cid)) {
            is Result.Success -> {
                val (channelType, channelId) = cid.cidToTypeAndId()
                logic.channel(channelType = channelType, channelId = channelId)
                    .loadOlderMessages(messageLimit = messageLimit)
            }
            is Result.Failure -> cidValidationResult
        }
    }
}

public fun ErmisClient.loadNewerMessages(
    channelCid: String,
    baseMessageId: String,
    messageLimit: Int,
): Call<Channel> {
    StreamLog.d(TAG) {
        "[loadNewerMessages] cid: $channelCid, " +
            "messageLimit: $messageLimit, baseMessageId: $baseMessageId"
    }
    return CoroutineCall(inheritScope { Job(it) }) {
        when (val cidValidationResult = validateCidWithResult(channelCid)) {
            is Result.Success -> {
                val (channelType, channelId) = channelCid.cidToTypeAndId()
                logic.channel(channelType = channelType, channelId = channelId)
                    .loadNewerMessages(messageId = baseMessageId, limit = messageLimit)
            }
            is Result.Failure -> cidValidationResult
        }
    }
}

/**
 * Cancels the message of "ephemeral" type.
 * Removes the message from local storage and state.
 *
 * @param message The `ephemeral` message to cancel.
 *
 * @return Executable async [Call] responsible for canceling ephemeral message.
 */
public fun ErmisClient.cancelEphemeralMessage(message: Message): Call<Boolean> {
    return CoroutineCall(inheritScope { Job(it) }) {
        when (val cidValidationResult = validateCidWithResult(message.cid)) {
            is Result.Success -> {
                try {
                    require(message.isEphemeral()) { "Only ephemeral message can be canceled" }
                    logic.channelFromMessage(message)?.deleteMessage(message)
                    logic.threadFromMessage(message)?.removeLocalMessage(message)
                    repositoryFacade.deleteChannelMessage(message)

                    Result.Success(true)
                } catch (exception: Exception) {
                    Result.Failure(
                        Error.ThrowableError(
                            message = "Could not cancel ephemeral message",
                            cause = exception,
                        ),
                    )
                }
            }
            is Result.Failure -> cidValidationResult
        }
    }
}

/**
 * Attempts to fetch the message from offline cache before making an API call.
 *
 * @param messageId The id of the message we are fetching.
 *
 * @return The message with the corresponding iID wrapped inside a [Call].
 */
@CheckResult
public fun ErmisClient.getMessageUsingCache(
    messageId: String,
): Call<Message> {
    return CoroutineCall(inheritScope { Job(it) }) {
        val message = logic.getMessageById(messageId) ?: logic.getMessageByIdFromDb(messageId)

        if (message != null) {
            Result.Success(message)
        } else {
            getMessage(messageId).await()
        }
    }
}

/**
 * Loads message for a given message id and channel id.
 *
 * @param cid The full channel id i. e. messaging:123.
 * @param messageId The id of the message.
 *
 * @return Executable async [Call] responsible for loading a message.
 */
@CheckResult
public fun ErmisClient.loadMessageById(
    cid: String,
    messageId: String,
): Call<Message> {
    StreamLog.d(TAG) { "[loadMessageById] cid: $cid, messageId: $messageId" }
    return CoroutineCall(inheritScope { Job(it) }) {
        loadMessageByIdInternal(cid, messageId)
    }
}

private suspend fun ErmisClient.loadMessageByIdInternal(
    cid: String,
    messageId: String,
): Result<Message> {
    val cidValidationResult = validateCidWithResult(cid)

    if (cidValidationResult is Result.Failure) {
        return cidValidationResult
    }

    val (channelType, channelId) = cid.cidToTypeAndId()
    val result = logic.channel(channelType = channelType, channelId = channelId)
        .loadMessagesAroundId(messageId)

    return when (result) {
        is Result.Success -> {
            val message = result.value.messages.firstOrNull { message ->
                message.id == messageId
            }

            if (message != null) {
                result.map { message }
            } else {
                Result.Failure(Error.GenericError("The message could not be found."))
            }
        }
        is Result.Failure -> Result.Failure(
            Error.GenericError("Error while fetching messages from backend. Messages around id: $messageId"),
        )
    }
}

/**
 * Loads the newest messages of a channel.
 *
 * @param cid The full channel id i. e. messaging:123.
 * @param messageLimit The number of messages to be loaded.
 * @param userPresence Flag to determine if the SDK is going to receive UserPresenceChanged events.
 * Used by the SDK to indicate if the user is online or not.
 *
 * @return Executable async [Call] responsible for loading the newest messages.
 */
@CheckResult
public fun ErmisClient.loadNewestMessages(
    cid: String,
    messageLimit: Int,
    userPresence: Boolean = true,
): Call<Channel> {
    StreamLog.d(TAG) { "[loadNewestMessages] cid: $cid, messageLimit: $messageLimit, userPresence: $userPresence" }
    return CoroutineCall(inheritScope { Job(it) }) {
        when (val cidValidationResult = validateCidWithResult(cid)) {
            is Result.Success -> {
                val (channelType, channelId) = cid.cidToTypeAndId()
                logic.channel(channelType = channelType, channelId = channelId)
                    .watch(messageLimit, userPresence)
            }
            is Result.Failure -> Result.Failure(cidValidationResult.value)
        }
    }
}

/**
 * Creates a fallback name for attachments without [Attachment.name] or [Attachment.title] properties.
 * Fallback names are generated in the following manner: "attachment_2022-16-12_12-15-06".
 */
private fun createAttachmentFallbackName(): String {
    val dateString = SimpleDateFormat(ATTACHMENT_FALLBACK_NAME_DATE_FORMAT, Locale.getDefault())
        .format(Date())
        .toString()

    return "attachment_$dateString"
}

/**
 * Date format pattern used for creating fallback names for attachments without [Attachment.name] or [Attachment.title]
 * properties
 */
private const val ATTACHMENT_FALLBACK_NAME_DATE_FORMAT: String = "yyyy-MM-dd_HH-mm-ss"

/**
 * [LogicRegistry] instance that contains all objects responsible for handling logic in offline plugin.
 */
internal val ErmisClient.logic: LogicRegistry
    get() = resolveDependency<StatePlugin, LogicRegistry>()

/**
 * Intermediate class to request ChatClient class as states
 *
 * @return [ChatClientStateCalls]
 */
internal fun ErmisClient.requestsAsState(scope: CoroutineScope): ChatClientStateCalls =
    ChatClientStateCalls(this, scope)
