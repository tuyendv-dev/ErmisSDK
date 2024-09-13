package network.ermis.client.channel

import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import network.ermis.client.ErmisClient
import network.ermis.client.ChatEventListener
import network.ermis.client.api.models.PinnedMessagesPagination
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.SendActionRequest
import network.ermis.client.api.models.WatchChannelRequest
import network.ermis.client.api.model.response.AttachmentResponse
import network.ermis.client.api.model.response.EmptyResponse
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
import network.ermis.client.uploader.FileUploader
import network.ermis.client.uploader.ImageMimeTypes
import network.ermis.client.utils.ProgressCallback
import network.ermis.client.utils.observable.Disposable
import network.ermis.core.models.Attachment
import network.ermis.core.models.BannedUser
import network.ermis.core.models.BannedUsersSort
import network.ermis.core.models.Channel
import network.ermis.core.models.EventType
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Filters
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.Mute
import network.ermis.core.models.Reaction
import network.ermis.core.models.UploadedFile
import network.ermis.core.models.UploadedImage
import network.ermis.core.models.querysort.QuerySortByField
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.Call
import java.io.File
import java.util.Date

@Suppress("TooManyFunctions")
public class ChannelClient internal constructor(
    public val channelType: String,
    public val channelId: String,
    private val client: ErmisClient,
) {

    public val cid: String = "$channelType:$channelId"

    /**
     * Creates the id-based channel.
     * @see [ErmisClient.createChannel]
     *
     * @param memberIds The list of members' ids.
     * @param extraData Map of key-value pairs that let you store extra data
     *
     * @return Executable async [Call] responsible for creating the channel.
     */
    @CheckResult
    public fun create(memberIds: List<String>, extraData: Map<String, Any>): Call<Channel> {
        return client.createChannel(
            channelType = channelType,
            channelId = channelId,
            memberIds = memberIds,
            extraData = extraData,
        )
    }

    public fun subscribe(listener: ChatEventListener<ChatEvent>): Disposable {
        return client.subscribe(filterRelevantEvents(listener))
    }

    public fun subscribeFor(
        vararg eventTypes: String,
        listener: ChatEventListener<ChatEvent>,
    ): Disposable {
        return client.subscribeFor(*eventTypes, listener = filterRelevantEvents(listener))
    }

    public fun subscribeFor(
        lifecycleOwner: LifecycleOwner,
        vararg eventTypes: String,
        listener: ChatEventListener<ChatEvent>,
    ): Disposable {
        return client.subscribeFor(
            lifecycleOwner,
            *eventTypes,
            listener = filterRelevantEvents(listener),
        )
    }

    public fun subscribeFor(
        vararg eventTypes: Class<out ChatEvent>,
        listener: ChatEventListener<ChatEvent>,
    ): Disposable {
        return client.subscribeFor(*eventTypes, listener = filterRelevantEvents(listener))
    }

    public fun subscribeFor(
        lifecycleOwner: LifecycleOwner,
        vararg eventTypes: Class<out ChatEvent>,
        listener: ChatEventListener<ChatEvent>,
    ): Disposable {
        return client.subscribeFor(
            lifecycleOwner,
            *eventTypes,
            listener = filterRelevantEvents(listener),
        )
    }

    public fun subscribeForSingle(
        eventType: String,
        listener: ChatEventListener<ChatEvent>,
    ): Disposable {
        return client.subscribeForSingle(eventType, listener = filterRelevantEvents(listener))
    }

    public fun <T : ChatEvent> subscribeForSingle(
        eventType: Class<T>,
        listener: ChatEventListener<T>,
    ): Disposable {
        return client.subscribeForSingle(eventType, listener = filterRelevantEvents(listener))
    }

    private fun <T : ChatEvent> filterRelevantEvents(
        listener: ChatEventListener<T>,
    ): ChatEventListener<T> {
        return ChatEventListener { event: T ->
            if (isRelevantForChannel(event)) {
                listener.onEvent(event)
            }
        }
    }

    @Suppress("ComplexMethod")
    private fun isRelevantForChannel(event: ChatEvent): Boolean {
        return when (event) {
            is ChannelDeletedEvent -> event.cid == cid
            is ChannelHiddenEvent -> event.cid == cid
            is ChannelTruncatedEvent -> event.cid == cid
            is ChannelUpdatedEvent -> event.cid == cid
            is ChannelUpdatedByUserEvent -> event.cid == cid
            is ChannelVisibleEvent -> event.cid == cid
            is MemberAddedEvent -> event.cid == cid
            is MemberRemovedEvent -> event.cid == cid
            is MemberUpdatedEvent -> event.cid == cid
            is MessageDeletedEvent -> event.cid == cid
            is MessageReadEvent -> event.cid == cid
            is MessageUpdatedEvent -> event.cid == cid
            is NewMessageEvent -> event.cid == cid
            is NotificationAddedToChannelEvent -> event.cid == cid
            is NotificationChannelDeletedEvent -> event.cid == cid
            is NotificationChannelTruncatedEvent -> event.cid == cid
            is NotificationInviteAcceptedEvent -> event.cid == cid
            is NotificationInviteRejectedEvent -> event.cid == cid
            is NotificationInvitedEvent -> event.cid == cid
            is NotificationMarkReadEvent -> event.cid == cid
            is NotificationMarkUnreadEvent -> event.cid == cid
            is NotificationMessageNewEvent -> event.cid == cid
            is NotificationRemovedFromChannelEvent -> event.cid == cid
            is ReactionDeletedEvent -> event.cid == cid
            is ReactionNewEvent -> event.cid == cid
            is ReactionUpdateEvent -> event.cid == cid
            is TypingStartEvent -> event.cid == cid
            is TypingStopEvent -> event.cid == cid
            is ChannelUserBannedEvent -> event.cid == cid
            is UserStartWatchingEvent -> event.cid == cid
            is UserStopWatchingEvent -> event.cid == cid
            is ChannelUserUnbannedEvent -> event.cid == cid
            is UnknownEvent -> event.rawData["cid"] == cid
            is SignalWebrtcEvent -> event.cid == cid
            is HealthEvent,
            is NotificationChannelMutesUpdatedEvent,
            is NotificationMutesUpdatedEvent,
            is GlobalUserBannedEvent,
            is UserDeletedEvent,
            is UserPresenceChangedEvent,
            is GlobalUserUnbannedEvent,
            is UserUpdatedEvent,
            is ConnectedEvent,
            is ConnectingEvent,
            is DisconnectedEvent,
            is ErrorEvent,
            is MarkAllReadEvent,
            -> false
        }
    }

    @CheckResult
    public fun query(request: QueryChannelRequest): Call<Channel> {
        return client.queryChannel(channelType, channelId, request)
    }

    @CheckResult
    public fun watch(request: WatchChannelRequest): Call<Channel> {
        return client.queryChannel(channelType, channelId, request)
    }

    @CheckResult
    public fun watch(data: Map<String, Any>): Call<Channel> {
        val request = WatchChannelRequest()
        request.data.putAll(data)
        return watch(request)
    }

    @CheckResult
    public fun watch(): Call<Channel> {
        return client.queryChannel(channelType, channelId, WatchChannelRequest())
    }

    @CheckResult
    public fun stopWatching(): Call<Unit> {
        return client.stopWatching(channelType, channelId)
    }

    @CheckResult
    public fun getMessage(messageId: String): Call<Message> {
        return client.getMessage(messageId)
    }

    @CheckResult
    public fun updateMessage(message: Message): Call<Message> {
        return client.updateMessage(message)
    }

    @CheckResult
    @JvmOverloads
    public fun deleteMessage(messageId: String, channelId: String, channelType: String, hard: Boolean = false): Call<Message> {
        return client.deleteMessage(messageId = messageId, channelId = channelId, channelType = channelType, hard = hard)
    }

    /**
     * Sends the message to the given channel with side effects if there is any plugin added in the client.
     *
     * @param message Message to send.
     * @param isRetrying True if this message is being retried.
     *
     * @return Executable async [Call] responsible for sending a message.
     */
    @CheckResult
    @JvmOverloads
    public fun sendMessage(message: Message, isRetrying: Boolean = false): Call<Message> {
        return client.sendMessage(channelType, channelId, message, isRetrying)
    }

    @CheckResult
    public fun banUser(targetId: String, reason: String?, timeout: Int?): Call<Unit> {
        return client.banUser(
            targetId = targetId,
            channelType = channelType,
            channelId = channelId,
            reason = reason,
            timeout = timeout,
        )
    }

    @CheckResult
    public fun unbanUser(targetId: String): Call<Unit> {
        return client.unbanUser(
            targetId = targetId,
            channelType = channelType,
            channelId = channelId,
        )
    }

    @CheckResult
    public fun shadowBanUser(targetId: String, reason: String?, timeout: Int?): Call<Unit> {
        return client.shadowBanUser(
            targetId = targetId,
            channelType = channelType,
            channelId = channelId,
            reason = reason,
            timeout = timeout,
        )
    }

    @CheckResult
    public fun removeShadowBan(targetId: String): Call<Unit> {
        return client.removeShadowBan(
            targetId = targetId,
            channelType = channelType,
            channelId = channelId,
        )
    }

    @CheckResult
    @JvmOverloads
    public fun queryBannedUsers(
        filter: FilterObject? = null,
        sort: QuerySorter<BannedUsersSort> = QuerySortByField.ascByName("created_at"),
        offset: Int? = null,
        limit: Int? = null,
        createdAtAfter: Date? = null,
        createdAtAfterOrEqual: Date? = null,
        createdAtBefore: Date? = null,
        createdAtBeforeOrEqual: Date? = null,
    ): Call<List<BannedUser>> {
        val channelCidFilter = Filters.eq("channel_cid", cid)
        return client.queryBannedUsers(
            filter = filter?.let { Filters.and(channelCidFilter, it) } ?: channelCidFilter,
            sort = sort,
            offset = offset,
            limit = limit,
            createdAtAfter = createdAtAfter,
            createdAtAfterOrEqual = createdAtAfterOrEqual,
            createdAtBefore = createdAtBefore,
            createdAtBeforeOrEqual = createdAtBeforeOrEqual,
        )
    }

    @CheckResult
    public fun markMessageRead(messageId: String): Call<Unit> {
        return client.markMessageRead(channelType, channelId, messageId)
    }

    @CheckResult
    public fun markUnread(messageId: String): Call<Unit> {
        return client.markUnread(channelType, channelId, messageId)
    }

    @CheckResult
    public fun markRead(): Call<Unit> {
        return client.markRead(channelType, channelId)
    }

    @CheckResult
    public fun delete(): Call<Channel> {
        return client.deleteChannel(channelType, channelId)
    }

    @CheckResult
    public fun show(): Call<Unit> {
        return client.showChannel(channelType, channelId)
    }

    /**
     * Hides the channel.
     * @see [ErmisClient.hideChannel]
     *
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     *
     * @return Executable async [Call] responsible for hiding a channel.
     */
    @CheckResult
    public fun hide(clearHistory: Boolean = false): Call<Unit> {
        return client.hideChannel(channelType, channelId, clearHistory)
    }

    /**
     * Removes all of the messages of the channel but doesn't affect the channel data or members.
     *
     * @param systemMessage The system message object that will be shown in the channel.
     *
     * @return Executable async [Call] which completes with [Result] having data equal to the truncated channel
     * if the channel was successfully truncated.
     */
    @CheckResult
    @JvmOverloads
    public fun truncate(systemMessage: Message? = null): Call<Channel> {
        return client.truncateChannel(channelType, channelId, systemMessage)
    }

    /**
     * Uploads a file for the given channel. Progress can be accessed via [callback].
     *
     * The Stream CDN imposes the following restrictions on file uploads:
     * - The maximum file size is 100 MB
     *
     * @param file The file that needs to be uploaded.
     * @param callback The callback to track progress.
     *
     * @return Executable async [Call] which completes with [Result] containing an instance of [UploadedFile]
     * if the file was successfully uploaded.
     *
     * @see FileUploader
     * @see <a href="https://getstream.io/chat/docs/android/file_uploads/?language=kotlin">File Uploads</a>
     */
    @CheckResult
    @JvmOverloads
    public fun sendFile(file: File, callback: ProgressCallback? = null): Call<UploadedFile> {
        return client.sendFile(channelType, channelId, file, callback)
    }

    /**
     * Uploads an image for the given channel. Progress can be accessed via [callback].
     *
     * The Stream CDN imposes the following restrictions on image uploads:
     * - The maximum image size is 100 MB
     * - Supported MIME types are listed in [ImageMimeTypes.SUPPORTED_IMAGE_MIME_TYPES]
     *
     * @param file The image file that needs to be uploaded.
     * @param callback The callback to track progress.
     *
     * @return Executable async [Call] which completes with [Result] containing an instance of [UploadedImage]
     * if the image was successfully uploaded.
     *
     * @see FileUploader
     * @see ImageMimeTypes.SUPPORTED_IMAGE_MIME_TYPES
     * @see <a href="https://getstream.io/chat/docs/android/file_uploads/?language=kotlin">File Uploads</a>
     */
    @CheckResult
    @JvmOverloads
    public fun sendImage(file: File, callback: ProgressCallback? = null): Call<UploadedImage> {
        return client.sendImage(channelType, channelId, file, callback)
    }

    /**
     * Deletes the file represented by [url] from the given channel.
     *
     * @param url The URL of the file to be deleted.
     *
     * @return Executable async [Call] responsible for deleting a file.
     *
     * @see FileUploader
     * @see <a href="https://getstream.io/chat/docs/android/file_uploads/?language=kotlin">File Uploads</a>
     */
    @CheckResult
    public fun deleteFile(url: String): Call<Unit> {
        return client.deleteFile(channelType, channelId, url)
    }

    /**
     * Deletes the image represented by [url] from the given channel.
     *
     * @param url The URL of the image to be deleted.
     *
     * @return Executable async [Call] responsible for deleting an image.
     *
     * @see FileUploader
     * @see <a href="https://getstream.io/chat/docs/android/file_uploads/?language=kotlin">File Uploads</a>
     */
    @CheckResult
    public fun deleteImage(url: String): Call<Unit> {
        return client.deleteImage(channelType, channelId, url)
    }

    /**
     * Sends the reaction.
     * Use [enforceUnique] parameter to specify whether the reaction should replace other reactions added by the
     * current user.
     *
     * @see [ErmisClient.sendReaction]
     *
     * @param reaction The [Reaction] to send.
     * @param enforceUnique Flag to determine whether the reaction should replace other ones added by the current user.
     *
     * @return Executable async [Call] responsible for sending the reaction.
     */
    @CheckResult
    public fun sendReaction(reaction: Reaction, enforceUnique: Boolean = false): Call<Reaction> {
        return client.sendReaction(reaction, enforceUnique, cid)
    }

    @CheckResult
    public fun sendAction(request: SendActionRequest): Call<Message> {
        return client.sendAction(request)
    }

    /**
     * Deletes the reaction associated with the message with the given message id.
     *
     * @see [ErmisClient.deleteReaction]
     *
     * @param messageId The id of the message to which reaction belongs.
     * @param reactionType The type of reaction.
     *
     * @return Executable async [Call] responsible for deleting the reaction.
     */
    @CheckResult
    public fun deleteReaction(messageId: String, reactionType: String): Call<Message> {
        return client.deleteReaction(messageId = messageId, reactionType = reactionType, cid = cid)
    }

    @CheckResult
    public fun getReactions(messageId: String, offset: Int, limit: Int): Call<List<Reaction>> {
        return client.getReactions(messageId, offset, limit)
    }

    @CheckResult
    public fun getReactions(
        messageId: String,
        firstReactionId: String,
        limit: Int,
    ): Call<List<Message>> {
        return client.getRepliesMore(messageId, firstReactionId, limit)
    }

    /**
     * Updates all of the channel data. Any data that is present on the channel and not included in a full update
     * will be deleted.
     *
     * @param message The message object allowing you to show a system message in the channel.
     * @param extraData The updated channel extra data.
     *
     * @return Executable async [Call] responsible for updating channel data.
     */
    @CheckResult
    public fun update(extraData: Map<String, Any> = emptyMap(), message: Message? = null): Call<Channel> {
        return client.updateChannel(channelType, channelId, message, extraData)
    }

    @CheckResult
    public fun banMembersChannel(userIds: List<String>): Call<Channel> {
        return client.banMembersChannel(channelType, channelId, userIds)
    }

    @CheckResult
    public fun unbanMembersChannel(userIds: List<String>): Call<Channel> {
        return client.unbanMembersChannel(channelType, channelId, userIds)
    }

    @CheckResult
    public fun demoteMembersChannel(userIds: List<String>): Call<Channel> {
        return client.demoteMembersChannel(channelType, channelId, userIds)
    }

    @CheckResult
    public fun promoteMembersChannel(userIds: List<String>): Call<Channel> {
        return client.promoteMembersChannel(channelType, channelId, userIds)
    }

    @CheckResult
    public fun updatePermisionMembersChannel(add: List<String>, delete: List<String>): Call<Channel> {
        return client.updatePermisionMembersChannel(channelType, channelId, add, delete)
    }

    @CheckResult
    public fun getAttachmentsOfChannel(): Call<AttachmentResponse> {
        return client.getAttachmentsOfChannel(channelType, channelId)
    }

    @CheckResult
    public fun searchMessageOfChannel(
        searchTerm: String,
        limit: Int,
        offset: Int): Call<List<Message>> {
        return client.searchMessageOfChannel(cid, searchTerm, limit, offset)
    }

    /**
     * Updates specific fields of channel data retaining the custom data fields which were set previously.
     *
     * @param set The key-value data which will be added to the existing channel data object.
     * @param unset The list of fields which will be removed from the existing channel data object.
     */
    @CheckResult
    public fun updatePartial(set: Map<String, Any> = emptyMap(), unset: List<String> = emptyList()): Call<Channel> {
        return client.updateChannelPartial(channelType, channelId, set, unset)
    }

    /**
     * Enables slow mode for the channel. When slow mode is enabled, users can only send a message every
     * [cooldownTimeInSeconds] time interval. The [cooldownTimeInSeconds] is specified in seconds, and should be
     * between 1-120.
     *
     * @param cooldownTimeInSeconds The duration of the time interval users have to wait between messages.
     *
     * @return Executable async [Call] responsible for enabling slow mode.
     */
    @CheckResult
    public fun enableSlowMode(cooldownTimeInSeconds: Int): Call<Channel> =
        client.enableSlowMode(channelType, channelId, cooldownTimeInSeconds)

    /**
     * Disables slow mode for the channel.
     *
     * @return Executable async [Call] responsible for disabling slow mode.
     */
    @CheckResult
    public fun disableSlowMode(): Call<Channel> =
        client.disableSlowMode(channelType, channelId)

    /**
     * Adds members to a given channel.
     *
     * @see [ErmisClient.addMembers]
     *
     * @param memberIds The list of the member ids to be added.
     * @param systemMessage The system message object that will be shown in the channel.
     * @param hideHistory Hides the history of the channel to the added member.
     * @param skipPush Skip sending push notifications.
     *
     * @return Executable async [Call] responsible for adding the members.
     */
    @CheckResult
    public fun addMembers(
        memberIds: List<String>,
        systemMessage: Message? = null,
        hideHistory: Boolean? = null,
        skipPush: Boolean? = null,
    ): Call<Channel> {
        return client.addMembers(
            channelType = channelType,
            channelId = channelId,
            memberIds = memberIds,
            systemMessage = systemMessage,
            hideHistory = hideHistory,
            skipPush = skipPush,
        )
    }

    /**
     * Removes members from a given channel.
     *
     * @see [ErmisClient.removeMembers]
     *
     * @param memberIds The list of the member ids to be removed.
     * @param systemMessage The system message object that will be shown in the channel.
     * @param skipPush Skip sending push notifications.
     *
     * @return Executable async [Call] responsible for removing the members.
     */
    @CheckResult
    public fun removeMembers(
        memberIds: List<String>,
        systemMessage: Message? = null,
        skipPush: Boolean? = null,
    ): Call<Channel> {
        return client.removeMembers(
            channelType = channelType,
            channelId = channelId,
            memberIds = memberIds,
            systemMessage = systemMessage,
            skipPush = skipPush,
        )
    }

    /**
     * Invites members to a given channel.
     *
     * @see [ErmisClient.inviteMembers]
     *
     * @param memberIds The list of the member ids to be invited.
     * @param systemMessage The system message object that will be shown in the channel.
     * @param skipPush Skip sending push notifications.
     *
     * @return Executable async [Call] responsible for inviting the members.
     */
    @CheckResult
    public fun inviteMembers(
        memberIds: List<String>,
        systemMessage: Message? = null,
        skipPush: Boolean? = null,
    ): Call<Channel> {
        return client.inviteMembers(
            channelType = channelType,
            channelId = channelId,
            memberIds = memberIds,
            systemMessage = systemMessage,
            skipPush = skipPush,
        )
    }

    @CheckResult
    public fun acceptInvite(message: String?): Call<EmptyResponse> {
        return client.acceptInvite(channelType, channelId, message)
    }

    @CheckResult
    public fun rejectInvite(): Call<EmptyResponse> {
        return client.rejectInvite(channelType, channelId)
    }

    /**
     * Mutes a channel for the current user. Messages added to the channel will not trigger
     * push notifications, and will not change the unread count for the users that muted it.
     * By default, mutes stay in place indefinitely until the user removes it. However, you
     * can optionally set an expiration time. Triggers `notification.channel_mutes_updated`
     * event.
     *
     * @param expiration The duration of mute in **millis**.
     *
     * @return Executable async [Call] responsible for muting a channel.
     *
     * @see [NotificationChannelMutesUpdatedEvent]
     */
    @JvmOverloads
    @CheckResult
    public fun mute(expiration: Int? = null): Call<Unit> {
        return client.muteChannel(channelType, channelId, expiration)
    }

    /**
     * Unmutes a channel for the current user. Triggers `notification.channel_mutes_updated`
     * event.
     *
     * @return Executable async [Call] responsible for unmuting a channel.
     *
     * @see [NotificationChannelMutesUpdatedEvent]
     */
    @CheckResult
    public fun unmute(): Call<Unit> {
        return client.unmuteChannel(channelType, channelId)
    }

    /**
     * Mutes a user. Messages from muted users will not trigger push notifications. By default,
     * mutes stay in place indefinitely until the user removes it. However, you can optionally
     * set a mute timeout. Triggers `notification.mutes_updated` event.
     *
     * @param userId The user id to mute.
     * @param timeout The timeout in **minutes** until the mute is expired.
     *
     * @return Executable async [Call] responsible for muting a user.
     *
     * @see [NotificationMutesUpdatedEvent]
     */
    @JvmOverloads
    @CheckResult
    public fun muteUser(userId: String, timeout: Int? = null): Call<Mute> {
        return client.muteUser(userId, timeout)
    }

    /**
     * Unmutes a previously muted user. Triggers `notification.mutes_updated` event.
     *
     * @param userId The user id to unmute.
     *
     * @return Executable async [Call] responsible for unmuting a user.
     *
     * @see [NotificationMutesUpdatedEvent]
     */
    @CheckResult
    public fun unmuteUser(userId: String): Call<Unit> {
        return client.unmuteUser(userId)
    }

    @CheckResult
    public fun muteCurrentUser(): Call<Mute> {
        return client.muteCurrentUser()
    }

    @CheckResult
    public fun unmuteCurrentUser(): Call<Unit> {
        return client.unmuteCurrentUser()
    }

    /**
     * Sends a start typing event [EventType.TYPING_START] in this channel to the server.
     *
     * @param parentId Set this field to `message.id` to indicate that typing event is happening in a thread.
     *
     * @return Executable async [Call] which completes with [Result] having [ChatEvent] data if successful or
     * [Error] if fails.
     */
    @CheckResult
    @JvmOverloads
    public fun keystroke(parentId: String? = null): Call<ChatEvent> {
        return client.keystroke(channelType, channelId, parentId)
    }

    /**
     * Sends a stop typing event [EventType.TYPING_STOP] in this channel to the server.
     *
     * @param parentId Set this field to `message.id` to indicate that typing event is happening in a thread.
     *
     * @return Executable async [Call] which completes with [Result] having [ChatEvent] data if successful or
     * [Error] if fails.
     */
    @CheckResult
    @JvmOverloads
    public fun stopTyping(parentId: String? = null): Call<ChatEvent> {
        return client.stopTyping(channelType, channelId, parentId)
    }

    /**
     * Sends an event to all users watching the channel.
     *
     * @param eventType The event name.
     * @param extraData The event payload.
     *
     * @return Executable async [Call] responsible for sending an event.
     */
    @CheckResult
    public fun sendEvent(
        eventType: String,
        extraData: Map<Any, Any> = emptyMap(),
    ): Call<ChatEvent> {
        return client.sendEvent(eventType, channelType, channelId, extraData)
    }

    /**
     * Queries members for this channel.
     *
     * @param offset Offset limit.
     * @param limit Number of members to fetch.
     * @param filter [FilterObject] to filter members of certain type.
     * @param sort Sort the list of members.
     * @param members List of members to search in distinct channels.
     *
     * @return [Call] with a list of members or an error.
     */
    @CheckResult
    public fun queryMembers(
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member> = emptyList(),
    ): Call<List<Member>> {
        return client.queryMembers(channelType, channelId, offset, limit, filter, sort, members)
    }

    @CheckResult
    public fun getFileAttachments(offset: Int, limit: Int): Call<List<Attachment>> =
        client.getFileAttachments(channelType, channelId, offset, limit)

    @CheckResult
    public fun getImageAttachments(offset: Int, limit: Int): Call<List<Attachment>> =
        client.getImageAttachments(channelType, channelId, offset, limit)

    /**
     * Returns a [Call] with messages that contain at least one desired type attachment but
     * not necessarily all of them will have a specified type.
     *
     * @param offset The messages offset.
     * @param limit Max limit messages to be fetched.
     * @param types Desired attachment's types list.
     */
    @CheckResult
    public fun getMessagesWithAttachments(offset: Int, limit: Int, types: List<String>): Call<List<Message>> {
        return client.getMessagesWithAttachments(
            channelType = channelType,
            channelId = channelId,
            offset = offset,
            limit = limit,
            types = types,
        )
    }

    /**
     * Returns a list of messages pinned in the channel.
     * You can sort the list by specifying [sort] parameter.
     * Keep in mind that for now we only support sorting by [Message.pinnedAt].
     * The list can be paginated in a few different ways using [limit] and [pagination].
     * @see [PinnedMessagesPagination]
     *
     * @param limit Max limit of messages to be fetched.
     * @param sort Parameter by which we sort the messages.
     * @param pagination Provides different options for pagination.
     *
     * @return Executable async [Call] responsible for getting pinned messages.
     */
    @CheckResult
    public fun getPinnedMessages(
        limit: Int,
        sort: QuerySorter<Message>,
        pagination: PinnedMessagesPagination,
    ): Call<List<Message>> {
        return client.getPinnedMessages(
            channelType = channelType,
            channelId = channelId,
            limit = limit,
            sort = sort,
            pagination = pagination,
        )
    }

    @CheckResult
    public fun pinMessage(message: Message, expirationDate: Date?): Call<Message> {
        return client.pinMessage(message, expirationDate)
    }

    @CheckResult
    public fun pinMessage(message: Message, timeout: Int): Call<Message> {
        return client.pinMessage(message, timeout)
    }

    @CheckResult
    public fun unpinMessage(message: Message): Call<Message> = client.unpinMessage(message)
}
