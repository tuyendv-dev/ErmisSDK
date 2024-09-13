package network.ermis.client.api

import androidx.annotation.CheckResult
import network.ermis.client.api.models.PinnedMessagesPagination
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.api.models.QueryUsersRequest
import network.ermis.client.api.models.SearchMessagesRequest
import network.ermis.client.api.models.SendActionRequest
import network.ermis.client.api.model.requests.UpdateUserProfileRequest
import network.ermis.client.api.model.requests.UsersByIdsRequest
import network.ermis.client.api.model.response.AttachmentResponse
import network.ermis.client.api.model.response.EmptyResponse
import network.ermis.client.api.model.response.ErmisChainResponse
import network.ermis.client.api.model.response.ErmisClientModel
import network.ermis.client.api.model.response.ErmisProject
import network.ermis.client.api.model.response.GetTokenLoginResponse
import network.ermis.client.api.model.response.SignalCallResponse
import network.ermis.client.api.model.response.UserLoginResponse
import network.ermis.client.api.model.response.UserRegisterResponse
import network.ermis.client.api.model.response.WalletConnectResponse
import network.ermis.client.call.RetrofitCall
import network.ermis.client.events.ChatEvent
import network.ermis.client.utils.ProgressCallback
import network.ermis.core.models.AppSettings
import network.ermis.core.models.Attachment
import network.ermis.core.models.BannedUser
import network.ermis.core.models.BannedUsersSort
import network.ermis.core.models.Channel
import network.ermis.core.models.Device
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Flag
import network.ermis.core.models.GuestUser
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.Mute
import network.ermis.core.models.Reaction
import network.ermis.core.models.SearchMessagesResult
import network.ermis.core.models.UploadedFile
import network.ermis.core.models.UploadedImage
import network.ermis.core.models.User
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.result.call.Call
import network.ermis.client.api.model.requests.DeleteDeviceRequest
import network.ermis.client.api.model.requests.JoinProjectRequest
import okhttp3.ResponseBody
import java.io.File
import java.util.Date

@Suppress("TooManyFunctions", "LongParameterList")
internal interface ChatApi {

    fun userRegister(userId: String, userName: String, password: String): Call<UserRegisterResponse>
    fun userLogin(userId: String, password: String): Call<UserLoginResponse>

    fun setConnection(userId: String, connectionId: String)

    fun setProjectId(projectId: String?)

    fun appSettings(): Call<AppSettings>

    @CheckResult
    fun sendFile(
        channelType: String,
        channelId: String,
        file: File,
        callback: ProgressCallback? = null,
    ): Call<UploadedFile>

    @CheckResult
    fun sendImage(
        channelType: String,
        channelId: String,
        file: File,
        callback: ProgressCallback? = null,
    ): Call<UploadedImage>

    @CheckResult
    fun deleteFile(channelType: String, channelId: String, url: String): Call<Unit>

    @CheckResult
    fun deleteImage(channelType: String, channelId: String, url: String): Call<Unit>

    @CheckResult
    fun addDevice(device: Device): Call<Unit>

    @CheckResult
    fun deleteDevice(device: DeleteDeviceRequest): Call<Unit>

    @CheckResult
    fun getDevices(): Call<List<Device>>

    @CheckResult
    fun searchMessages(request: SearchMessagesRequest): Call<List<Message>>

    @CheckResult
    fun searchMessages(
        channelFilter: FilterObject,
        messageFilter: FilterObject,
        offset: Int?,
        limit: Int?,
        next: String?,
        sort: QuerySorter<Message>?,
    ): Call<SearchMessagesResult>

    @CheckResult
    fun getRepliesMore(
        messageId: String,
        firstId: String,
        limit: Int,
    ): Call<List<Message>>

    @CheckResult
    fun getReplies(messageId: String, limit: Int): Call<List<Message>>

    @CheckResult
    fun getReactions(
        messageId: String,
        offset: Int,
        limit: Int,
    ): Call<List<Reaction>>

    @CheckResult
    fun sendReaction(reaction: Reaction, enforceUnique: Boolean): Call<Reaction>

    @CheckResult
    fun sendReaction(
        messageId: String,
        reactionType: String,
        enforceUnique: Boolean,
    ): Call<Reaction> {
        return sendReaction(
            reaction = Reaction(
                messageId = messageId,
                type = reactionType,
                score = 0,
            ),
            enforceUnique = enforceUnique,
        )
    }

    @CheckResult
    fun deleteReaction(messageId: String, reactionType: String, channelType: String, channelId: String): Call<Message>

    @CheckResult
    fun deleteMessage(messageId: String, channelId: String, channelType: String, hard: Boolean = false): Call<Message>

    @CheckResult
    fun sendAction(request: SendActionRequest): Call<Message>

    @CheckResult
    fun getMessage(messageId: String): Call<Message>

    @CheckResult
    fun sendMessage(
        channelType: String,
        channelId: String,
        message: Message,
    ): Call<Message>

    @CheckResult
    fun muteChannel(
        channelType: String,
        channelId: String,
        expiration: Int?,
    ): Call<Unit>

    @CheckResult
    fun unmuteChannel(
        channelType: String,
        channelId: String,
    ): Call<Unit>

    @CheckResult
    fun updateMessage(
        message: Message,
    ): Call<Message>

    @CheckResult
    fun partialUpdateMessage(
        messageId: String,
        set: Map<String, Any>,
        unset: List<String>,
        skipEnrichUrl: Boolean = false,
    ): Call<Message>

    @CheckResult
    fun stopWatching(
        channelType: String,
        channelId: String,
    ): Call<Unit>

    @CheckResult
    fun getPinnedMessages(
        channelType: String,
        channelId: String,
        limit: Int,
        sort: QuerySorter<Message>,
        pagination: PinnedMessagesPagination,
    ): Call<List<Message>>

    @CheckResult
    fun getListContactIdsFromUser(): Call<List<String>>

    @CheckResult
    fun queryChannels(query: QueryChannelsRequest): Call<List<Channel>>

    @CheckResult
    fun updateUsers(users: List<User>): Call<List<User>>

    @CheckResult
    fun getUserInfo(id: String, projectId: String?): Call<User>

    fun updateUserProfile(body: UpdateUserProfileRequest): Call<User>

    fun updateUserAvatar(file: File): Call<String>

    @CheckResult
    fun partialUpdateUser(
        id: String,
        set: Map<String, Any>,
        unset: List<String>,
    ): Call<User>

    fun queryChannel(
        channelType: String,
        channelId: String = "",
        query: QueryChannelRequest,
    ): Call<Channel>

    @CheckResult
    fun updateChannel(
        channelType: String,
        channelId: String,
        extraData: Map<String, Any>,
        updateMessage: Message?,
    ): Call<Channel>

    @CheckResult
    fun banMembersChannel(
        channelType: String,
        channelId: String,
        userIds: List<String>
    ): Call<Channel>

    @CheckResult
    fun unbanMembersChannel(
        channelType: String,
        channelId: String,
        userIds: List<String>
    ): Call<Channel>

    @CheckResult
    fun promoteMembersChannel(
        channelType: String,
        channelId: String,
        userIds: List<String>
    ): Call<Channel>

    @CheckResult
    fun demoteMembersChannel(
        channelType: String,
        channelId: String,
        userIds: List<String>
    ): Call<Channel>

    @CheckResult
    fun updatePermisionMembersChannel(
        channelType: String,
        channelId: String,
        add: List<String>,
        delete: List<String>
    ): Call<Channel>

    @CheckResult
    fun getAttachmentsOfChannel(
        channelType: String,
        channelId: String,
    ): Call<AttachmentResponse>

    @CheckResult
    fun searchMessageOfChannel(
        cid: String,
        search_term: String,
        limit: Int,
        offset: Int
    ): Call<List<Message>>

    @CheckResult
    fun updateChannelPartial(
        channelType: String,
        channelId: String,
        set: Map<String, Any>,
        unset: List<String>,
    ): Call<Channel>

    @CheckResult
    fun enableSlowMode(
        channelType: String,
        channelId: String,
        cooldownTimeInSeconds: Int,
    ): Call<Channel>

    @CheckResult
    fun disableSlowMode(
        channelType: String,
        channelId: String,
    ): Call<Channel>

    @CheckResult
    fun markRead(
        channelType: String,
        channelId: String,
        messageId: String = "",
    ): Call<Unit>

    @CheckResult
    fun markUnread(
        channelType: String,
        channelId: String,
        messageId: String,
    ): Call<Unit>

    @CheckResult
    fun showChannel(channelType: String, channelId: String): Call<Unit>

    @CheckResult
    fun hideChannel(
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ): Call<Unit>

    @CheckResult
    fun truncateChannel(
        channelType: String,
        channelId: String,
        systemMessage: Message?,
    ): Call<Channel>

    @CheckResult
    fun rejectInvite(channelType: String, channelId: String): Call<EmptyResponse>

    @CheckResult
    fun muteCurrentUser(): Call<Mute>

    @CheckResult
    fun unmuteCurrentUser(): Call<Unit>

    @CheckResult
    fun acceptInvite(
        channelType: String,
        channelId: String,
        message: String?,
    ): Call<EmptyResponse>

    @CheckResult
    fun deleteChannel(channelType: String, channelId: String): Call<Channel>

    @CheckResult
    fun markAllRead(): Call<Unit>

    @CheckResult
    fun getGuestUser(userId: String, userName: String): Call<GuestUser>

    @CheckResult
    fun getUserInfoNoAuth(userId: String, projectId: String): Call<User>

    @CheckResult
    fun queryUsers(queryUsers: QueryUsersRequest, projectId: String?): Call<List<User>>

    @CheckResult
    fun getUsersByIds(body: UsersByIdsRequest): Call<List<User>>

    @CheckResult
    fun addMembers(
        channelType: String,
        channelId: String,
        members: List<String>,
        systemMessage: Message?,
        hideHistory: Boolean?,
        skipPush: Boolean?,
    ): Call<Channel>

    @CheckResult
    fun removeMembers(
        channelType: String,
        channelId: String,
        members: List<String>,
        systemMessage: Message?,
        skipPush: Boolean?,
    ): Call<Channel>

    @CheckResult
    fun inviteMembers(
        channelType: String,
        channelId: String,
        members: List<String>,
        systemMessage: Message?,
        skipPush: Boolean?,
    ): Call<Channel>

    @CheckResult
    fun queryMembers(
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    ): Call<List<Member>>

    @CheckResult
    fun muteUser(
        userId: String,
        timeout: Int?,
    ): Call<Mute>

    @CheckResult
    fun unmuteUser(
        userId: String,
    ): Call<Unit>

    @CheckResult
    fun flagUser(userId: String): Call<Flag>

    @CheckResult
    fun unflagUser(userId: String): Call<Flag>

    @CheckResult
    fun flagMessage(messageId: String): Call<Flag>

    @CheckResult
    fun unflagMessage(messageId: String): Call<Flag>

    @CheckResult
    fun banUser(
        targetId: String,
        timeout: Int?,
        reason: String?,
        channelType: String,
        channelId: String,
        shadow: Boolean,
    ): Call<Unit>

    @CheckResult
    fun unbanUser(
        targetId: String,
        channelType: String,
        channelId: String,
        shadow: Boolean,
    ): Call<Unit>

    @CheckResult
    fun queryBannedUsers(
        filter: FilterObject,
        sort: QuerySorter<BannedUsersSort>,
        offset: Int?,
        limit: Int?,
        createdAtAfter: Date?,
        createdAtAfterOrEqual: Date?,
        createdAtBefore: Date?,
        createdAtBeforeOrEqual: Date?,
    ): Call<List<BannedUser>>

    @CheckResult
    fun sendVideoCallSignal(cid: String, action: String, type: String, sdp: String): Call<SignalCallResponse>

    @CheckResult
    fun sendEvent(
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
    ): Call<ChatEvent>

    @CheckResult
    fun translate(messageId: String, language: String): Call<Message>

    @CheckResult
    fun og(url: String): Call<Attachment>

    @CheckResult
    fun getSyncHistory(channelIds: List<String>, lastSyncAt: String): Call<List<ChatEvent>>

    @CheckResult
    fun downloadFile(fileUrl: String): Call<ResponseBody>

    fun warmUp()

    fun releaseConnection()

    @CheckResult
    fun walletConnect(address: String): Call<WalletConnectResponse>

    @CheckResult
    fun walletSignin(address: String, signature: String, apiKey: String, nonce: String): Call<GetTokenLoginResponse>

    @CheckResult
    fun getChains(): Call<ErmisChainResponse>

    @CheckResult
    fun getClientsByChainId(chainId: String): RetrofitCall<List<ErmisClientModel>>

    @CheckResult
    fun getProjects(clientId: String, chainId: String): RetrofitCall<List<ErmisProject>>

    @CheckResult
    fun queryAllChannelOfAllProject(): Call<List<Channel>>

    @CheckResult
    fun joinProject(params: JoinProjectRequest): Call<ErmisChainResponse>
}
