package network.ermis.client.api

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
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.CoroutineCall
import io.getstream.result.call.map
import io.getstream.result.call.toUnitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import network.ermis.client.api.endpoint.ChannelApi
import network.ermis.client.api.endpoint.ConfigApi
import network.ermis.client.api.endpoint.DeviceApi
import network.ermis.client.api.endpoint.ErmisClientApi
import network.ermis.client.api.endpoint.FileDownloadApi
import network.ermis.client.api.endpoint.GeneralApi
import network.ermis.client.api.endpoint.GuestApi
import network.ermis.client.api.endpoint.LoginRegisterUserApi
import network.ermis.client.api.endpoint.MessageApi
import network.ermis.client.api.endpoint.ModerationApi
import network.ermis.client.api.endpoint.OpenGraphApi
import network.ermis.client.api.endpoint.UserApi
import network.ermis.client.api.endpoint.VideoCallApi
import network.ermis.client.api.endpoint.WalletApi
import network.ermis.client.api.mapping.toDomain
import network.ermis.client.api.mapping.toDto
import network.ermis.client.api.model.dto.ChatEventDto
import network.ermis.client.api.model.dto.DeviceDto
import network.ermis.client.api.model.dto.DownstreamMemberDto
import network.ermis.client.api.model.dto.DownstreamMessageDto
import network.ermis.client.api.model.dto.DownstreamReactionDto
import network.ermis.client.api.model.dto.DownstreamUserDto
import network.ermis.client.api.model.dto.PartialUpdateUserDto
import network.ermis.client.api.model.dto.UpstreamUserDto
import network.ermis.client.api.model.requests.AddDeviceRequest
import network.ermis.client.api.model.requests.AddMembersRequest
import network.ermis.client.api.model.requests.BanMembersRequest
import network.ermis.client.api.model.requests.BanUserRequest
import network.ermis.client.api.model.requests.DeleteDeviceRequest
import network.ermis.client.api.model.requests.DemoteMemberRequest
import network.ermis.client.api.model.requests.GetClientRequest
import network.ermis.client.api.model.requests.GetProjectRequest
import network.ermis.client.api.model.requests.GuestUserRequest
import network.ermis.client.api.model.requests.HideChannelRequest
import network.ermis.client.api.model.requests.InviteMembersRequest
import network.ermis.client.api.model.requests.JoinProjectRequest
import network.ermis.client.api.model.requests.MarkReadRequest
import network.ermis.client.api.model.requests.MarkUnreadRequest
import network.ermis.client.api.model.requests.MuteChannelRequest
import network.ermis.client.api.model.requests.MuteUserRequest
import network.ermis.client.api.model.requests.PartialUpdateMessageRequest
import network.ermis.client.api.model.requests.PartialUpdateUsersRequest
import network.ermis.client.api.model.requests.PinnedMessagesRequest
import network.ermis.client.api.model.requests.PromoteMemberRequest
import network.ermis.client.api.model.requests.QueryBannedUsersRequest
import network.ermis.client.api.model.requests.QueryMembersRequest
import network.ermis.client.api.model.requests.RemoveMembersRequest
import network.ermis.client.api.model.requests.SearchMessage
import network.ermis.client.api.model.requests.SendActionRequest
import network.ermis.client.api.model.requests.SendEventRequest
import network.ermis.client.api.model.requests.SendMessageRequest
import network.ermis.client.api.model.requests.SignalCall
import network.ermis.client.api.model.requests.SyncHistoryRequest
import network.ermis.client.api.model.requests.TruncateChannelRequest
import network.ermis.client.api.model.requests.UnBanMembersRequest
import network.ermis.client.api.model.requests.UpdateChannelPartialRequest
import network.ermis.client.api.model.requests.UpdateChannelRequest
import network.ermis.client.api.model.requests.UpdateCooldownRequest
import network.ermis.client.api.model.requests.UpdateMessageRequest
import network.ermis.client.api.model.requests.UpdatePermissionMembersRequest
import network.ermis.client.api.model.requests.UpdateUserProfileRequest
import network.ermis.client.api.model.requests.UpdateUsersRequest
import network.ermis.client.api.model.requests.UserLoginRequest
import network.ermis.client.api.model.requests.UserRegisterRequest
import network.ermis.client.api.model.requests.UsersByIdsRequest
import network.ermis.client.api.model.requests.VideoCallSignalRequest
import network.ermis.client.api.model.requests.WalletConnectRequest
import network.ermis.client.api.model.requests.WalletSigninRequest
import network.ermis.client.api.model.response.AppSettingsResponse
import network.ermis.client.api.model.response.AttachmentResponse
import network.ermis.client.api.model.response.BannedUserResponse
import network.ermis.client.api.model.response.ChannelResponse
import network.ermis.client.api.model.response.EmptyResponse
import network.ermis.client.api.model.response.ErmisChainResponse
import network.ermis.client.api.model.response.ErmisClientModel
import network.ermis.client.api.model.response.ErmisProject
import network.ermis.client.api.model.response.GetTokenLoginResponse
import network.ermis.client.api.model.response.SignalCallResponse
import network.ermis.client.api.model.response.TranslateMessageRequest
import network.ermis.client.api.model.response.UserLoginResponse
import network.ermis.client.api.model.response.UserRegisterResponse
import network.ermis.client.api.model.response.WalletConnectResponse
import network.ermis.client.api.models.PinnedMessagesPagination
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.api.models.QueryUsersRequest
import network.ermis.client.api.models.SearchMessagesRequest
import network.ermis.client.call.RetrofitCall
import network.ermis.client.events.ChatEvent
import network.ermis.client.helpers.CallPostponeHelper
import network.ermis.client.parser.toMap
import network.ermis.client.scope.UserScope
import network.ermis.client.uploader.FileUploader
import network.ermis.client.utils.ProgressCallback
import network.ermis.client.utils.extensions.channelIdToProjectId
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.client.utils.extensions.enrichWithCid
import network.ermis.client.utils.extensions.getMediaType
import network.ermis.client.utils.extensions.syncUnreadCountWithReads
import network.ermis.core.models.querysort.QuerySorter
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.util.Date
import network.ermis.client.api.models.SendActionRequest as DomainSendActionRequest

@Suppress("TooManyFunctions", "LargeClass")
internal class MoshiChatApi
@Suppress("LongParameterList")
constructor(
    private val fileUploader: FileUploader,
    private val loginRegister: LoginRegisterUserApi,
    private val userApi: UserApi,
    private val guestApi: GuestApi,
    private val messageApi: MessageApi,
    private val channelApi: ChannelApi,
    private val deviceApi: DeviceApi,
    private val moderationApi: ModerationApi,
    private val generalApi: GeneralApi,
    private val configApi: ConfigApi,
    private val callApi: VideoCallApi,
    private val fileDownloadApi: FileDownloadApi,
    private val ogApi: OpenGraphApi,
    private val walletApi: WalletApi,
    private val ermisClienApi: ErmisClientApi,
    private val coroutineScope: CoroutineScope,
    private val userScope: UserScope,
) : ChatApi {

    private val logger by taggedLogger("Chat:MoshiChatApi")

    private val callPostponeHelper: CallPostponeHelper by lazy {
        CallPostponeHelper(
            awaitConnection = {
                _connectionId.first { id -> id.isNotEmpty() }
            },
            userScope = userScope,
        )
    }

    @Volatile
    private var userId: String = ""
        get() {
            if (field == "") {
                logger.e { "userId accessed before being set. Did you forget to call ChatClient.connectUser()?" }
            }
            return field
        }

    private val _projectId: MutableStateFlow<String?> = MutableStateFlow("")

    private val projectId: String?
        get() {
            if (_projectId.value == "") {
                logger.e { "projectId accessed before being set. Did you forget to call ChatClient.setProject()?" }
            }
            return _projectId.value
        }

    private val _connectionId: MutableStateFlow<String> = MutableStateFlow("")

    private val connectionId: String
        get() {
            if (_connectionId.value == "") {
                logger.e { "connectionId accessed before being set. Did you forget to call ChatClient.connectUser()?" }
            }
            return _connectionId.value
        }

    override fun userRegister(userId: String, userName: String, password: String): Call<UserRegisterResponse> {
        return loginRegister
            .userRegister(UserRegisterRequest(user_id = userId, user_name = userName, password = password))
    }

    override fun userLogin(userId: String, password: String): Call<UserLoginResponse> {
        return loginRegister
            .userLogin(UserLoginRequest(user_id = userId, password = password))
    }

    override fun setConnection(userId: String, connectionId: String) {
        logger.d { "[setConnection] userId: '$userId', connectionId: '$connectionId'" }
        this.userId = userId
        this._connectionId.value = connectionId
    }

    override fun setProjectId(projectId: String?) {
        logger.d { "[setProjectId] projectId: '$projectId'" }
        this._projectId.value = projectId
    }

    override fun releaseConnection() {
        this._connectionId.value = ""
    }

    override fun appSettings(): Call<AppSettings> {
        return configApi.getAppSettings().map(AppSettingsResponse::toDomain)
    }

    override fun sendMessage(
        channelType: String,
        channelId: String,
        message: Message,
    ): Call<Message> {
        return messageApi.sendMessage(
            channelType = channelType,
            channelId = channelId,
            message = SendMessageRequest(
                message = message.toDto(),
                skip_push = message.skipPushNotification,
                skip_enrich_url = message.skipEnrichUrl,
            ),
        ).map { response -> response.message.toDomain() }
    }

    override fun updateMessage(
        message: Message,
    ): Call<Message> {
        val (channelType, channelId) = message.cid.cidToTypeAndId()
        return messageApi.updateMessage(
            channelType = channelType,
            channelId = channelId,
            messageId = message.id,
            message = UpdateMessageRequest(
                text = message.text,
            ),
        ).map { response -> response.message.toDomain() }
    }

    override fun partialUpdateMessage(
        messageId: String,
        set: Map<String, Any>,
        unset: List<String>,
        skipEnrichUrl: Boolean,
    ): Call<Message> {
        return messageApi.partialUpdateMessage(
            messageId = messageId,
            body = PartialUpdateMessageRequest(
                set = set,
                unset = unset,
                skip_enrich_url = skipEnrichUrl,
            ),
        ).map { response -> response.message.toDomain() }
    }

    override fun getMessage(messageId: String): Call<Message> {
        return messageApi.getMessage(
            messageId = messageId,
        ).map { response -> response.message.toDomain() }
    }

    override fun deleteMessage(messageId: String, channelId: String, channelType: String, hard: Boolean): Call<Message> {
        return messageApi.deleteMessage(
            channelType = channelType,
            channelId = channelId,
            messageId = messageId,
            // hard = if (hard) true else null,
        ).map { response -> response.message.toDomain() }
    }

    override fun getReactions(
        messageId: String,
        offset: Int,
        limit: Int,
    ): Call<List<Reaction>> {
        return messageApi.getReactions(
            messageId = messageId,
            offset = offset,
            limit = limit,
        ).map { response -> response.reactions.map(DownstreamReactionDto::toDomain) }
    }

    override fun sendReaction(reaction: Reaction, enforceUnique: Boolean): Call<Reaction> {
        return messageApi.sendReaction(
            messageId = reaction.messageId,
            channelType = reaction.channelType,
            channelId = reaction.channelId,
            reactionType = reaction.type,
            // request = ReactionRequest(
            //     reaction = reaction.toDto(),
            //     enforce_unique = enforceUnique,
            // ),
        ).map { response -> response.reaction.toDomain() }
    }

    override fun deleteReaction(
        messageId: String,
        reactionType: String,
        channelType: String,
        channelId: String
    ): Call<Message> {
        return messageApi.deleteReaction(
            channelType = channelType,
            channelId = channelId,
            messageId = messageId,
            reactionType = reactionType,
        ).map { response -> response.message.toDomain() }
    }

    override fun addDevice(device: Device): Call<Unit> {
        return deviceApi.addDevices(
            connectionId = connectionId,
            request = AddDeviceRequest(
                device.token,
                device.pushProvider.key,
                device.providerName,
            ),
        ).toUnitCall()
    }

    override fun deleteDevice(device: DeleteDeviceRequest): Call<Unit> {
        return deviceApi.deleteDevice(
            connectionId = connectionId,
            deviceId = device
        ).toUnitCall()
    }

    override fun getDevices(): Call<List<Device>> {
        return deviceApi.getDevices().map { response -> response.devices.map(DeviceDto::toDomain) }
    }

    override fun muteCurrentUser(): Call<Mute> {
        return muteUser(
            userId = userId,
            timeout = null,
        )
    }

    override fun unmuteCurrentUser(): Call<Unit> {
        return unmuteUser(userId)
    }

    override fun muteUser(
        userId: String,
        timeout: Int?,
    ): Call<Mute> {
        return moderationApi.muteUser(
            body = MuteUserRequest(
                target_id = userId,
                user_id = this.userId,
                timeout = timeout,
            ),
        ).map { response -> response.mute.toDomain() }
    }

    override fun unmuteUser(userId: String): Call<Unit> {
        return moderationApi.unmuteUser(
            body = MuteUserRequest(
                target_id = userId,
                user_id = this.userId,
                timeout = null,
            ),
        ).toUnitCall()
    }

    override fun muteChannel(
        channelType: String,
        channelId: String,
        expiration: Int?,
    ): Call<Unit> {
        return moderationApi.muteChannel(
            body = MuteChannelRequest(
                channel_cid = "$channelType:$channelId",
                expiration = expiration,
            ),
        ).toUnitCall()
    }

    override fun unmuteChannel(
        channelType: String,
        channelId: String,
    ): Call<Unit> {
        return moderationApi.unmuteChannel(
            body = MuteChannelRequest(
                channel_cid = "$channelType:$channelId",
                expiration = null,
            ),
        ).toUnitCall()
    }

    override fun sendFile(
        channelType: String,
        channelId: String,
        file: File,
        callback: ProgressCallback?,
    ): Call<UploadedFile> {
        return CoroutineCall(coroutineScope) {
            if (callback != null) {
                fileUploader.sendFile(
                    channelType = channelType,
                    channelId = channelId,
                    userId = userId,
                    file = file,
                    callback,
                )
            } else {
                fileUploader.sendFile(
                    channelType = channelType,
                    channelId = channelId,
                    userId = userId,
                    file = file,
                )
            }
        }
    }

    override fun sendImage(
        channelType: String,
        channelId: String,
        file: File,
        callback: ProgressCallback?,
    ): Call<UploadedImage> {
        return CoroutineCall(coroutineScope) {
            if (callback != null) {
                fileUploader.sendImage(
                    channelType = channelType,
                    channelId = channelId,
                    userId = userId,
                    file = file,
                    callback,
                )
            } else {
                fileUploader.sendImage(
                    channelType = channelType,
                    channelId = channelId,
                    userId = userId,
                    file = file,
                )
            }
        }
    }

    override fun deleteFile(channelType: String, channelId: String, url: String): Call<Unit> {
        return CoroutineCall(coroutineScope) {
            fileUploader.deleteFile(
                channelType = channelType,
                channelId = channelId,
                userId = userId,
                url = url,
            )
            Result.Success(Unit)
        }
    }

    override fun deleteImage(channelType: String, channelId: String, url: String): Call<Unit> {
        return CoroutineCall(coroutineScope) {
            fileUploader.deleteImage(
                channelType = channelType,
                channelId = channelId,
                userId = userId,
                url = url,
            )
            Result.Success(Unit)
        }
    }

    override fun flagUser(userId: String): Call<Flag> =
        flag(mutableMapOf("target_user_id" to userId))

    override fun unflagUser(userId: String): Call<Flag> =
        unflag(mutableMapOf("target_user_id" to userId))

    override fun flagMessage(messageId: String): Call<Flag> =
        flag(mutableMapOf("target_message_id" to messageId))

    override fun unflagMessage(messageId: String): Call<Flag> =
        unflag(mutableMapOf("target_message_id" to messageId))

    private fun flag(body: MutableMap<String, String>): Call<Flag> {
        return moderationApi.flag(body = body).map { response -> response.flag.toDomain() }
    }

    private fun unflag(body: MutableMap<String, String>): Call<Flag> {
        return moderationApi.unflag(body = body).map { response -> response.flag.toDomain() }
    }

    override fun banUser(
        targetId: String,
        timeout: Int?,
        reason: String?,
        channelType: String,
        channelId: String,
        shadow: Boolean,
    ): Call<Unit> {
        return moderationApi.banUser(
            body = BanUserRequest(
                target_user_id = targetId,
                timeout = timeout,
                reason = reason,
                type = channelType,
                id = channelId,
                shadow = shadow,
            ),
        ).toUnitCall()
    }

    override fun unbanUser(
        targetId: String,
        channelType: String,
        channelId: String,
        shadow: Boolean,
    ): Call<Unit> {
        return moderationApi.unbanUser(
            targetUserId = targetId,
            channelId = channelId,
            channelType = channelType,
            shadow = shadow,
        ).toUnitCall()
    }

    override fun queryBannedUsers(
        filter: FilterObject,
        sort: QuerySorter<BannedUsersSort>,
        offset: Int?,
        limit: Int?,
        createdAtAfter: Date?,
        createdAtAfterOrEqual: Date?,
        createdAtBefore: Date?,
        createdAtBeforeOrEqual: Date?,
    ): Call<List<BannedUser>> {
        return moderationApi.queryBannedUsers(
            payload = QueryBannedUsersRequest(
                filter_conditions = filter.toMap(),
                sort = sort.toDto(),
                offset = offset,
                limit = limit,
                created_at_after = createdAtAfter,
                created_at_after_or_equal = createdAtAfterOrEqual,
                created_at_before = createdAtBefore,
                created_at_before_or_equal = createdAtBeforeOrEqual,
            ),
        ).map { response -> response.bans.map(BannedUserResponse::toDomain) }
    }

    override fun enableSlowMode(
        channelType: String,
        channelId: String,
        cooldownTimeInSeconds: Int,
    ): Call<Channel> = updateCooldown(
        channelType = channelType,
        channelId = channelId,
        cooldownTimeInSeconds = cooldownTimeInSeconds,
    )

    override fun disableSlowMode(
        channelType: String,
        channelId: String,
    ): Call<Channel> = updateCooldown(
        channelType = channelType,
        channelId = channelId,
        cooldownTimeInSeconds = 0,
    )

    private fun updateCooldown(
        channelType: String,
        channelId: String,
        cooldownTimeInSeconds: Int,
    ): Call<Channel> {
        return channelApi.updateCooldown(
            channelType = channelType,
            channelId = channelId,
            body = UpdateCooldownRequest.create(cooldownTimeInSeconds),
        ).map(this::flattenChannel)
    }

    override fun stopWatching(channelType: String, channelId: String): Call<Unit> = postponeCall {
        channelApi.stopWatching(
            channelType = channelType,
            channelId = channelId,
            connectionId = connectionId,
            body = emptyMap(),
        ).toUnitCall()
    }

    override fun getPinnedMessages(
        channelType: String,
        channelId: String,
        limit: Int,
        sort: QuerySorter<Message>,
        pagination: PinnedMessagesPagination,
    ): Call<List<Message>> {
        return channelApi.getPinnedMessages(
            channelType = channelType,
            channelId = channelId,
            payload = PinnedMessagesRequest.create(
                limit = limit,
                sort = sort,
                pagination = pagination,
            ),
        ).map { response -> response.messages.map(DownstreamMessageDto::toDomain) }
    }

    override fun getListContactIdsFromUser(): Call<List<String>> {
        val body = if (projectId != null) mapOf("project_id" to projectId!!) else mapOf()
        return channelApi.getContactsList(body).map { it.project_id_user_ids.get(projectId!!) ?: listOf() }
    }

    override fun updateChannel(
        channelType: String,
        channelId: String,
        extraData: Map<String, Any>,
        updateMessage: Message?,
    ): Call<Channel> {
        return channelApi.updateChannel(
            channelType = channelType,
            channelId = channelId,
            body = UpdateChannelRequest(extraData, updateMessage?.toDto()),
        ).map(this::flattenChannel)
    }

    override fun banMembersChannel(channelType: String, channelId: String, userIds: List<String>): Call<Channel> {
        return channelApi.banMembers(channelType, channelId,
            BanMembersRequest(userIds)
        )
            .map(this::flattenChannel)
    }

    override fun unbanMembersChannel(channelType: String, channelId: String, userIds: List<String>): Call<Channel> {
        return channelApi.unbanMembers(channelType, channelId,
            UnBanMembersRequest(userIds)
        )
            .map(this::flattenChannel)
    }

    override fun demoteMembersChannel(channelType: String, channelId: String, userIds: List<String>): Call<Channel> {
        return channelApi.demoteMembers(channelType, channelId,
            DemoteMemberRequest(userIds)
        )
            .map(this::flattenChannel)
    }

    override fun promoteMembersChannel(channelType: String, channelId: String, userIds: List<String>): Call<Channel> {
        return channelApi.promoteMembers(channelType, channelId,
            PromoteMemberRequest(userIds)
        )
            .map(this::flattenChannel)
    }

    override fun updatePermisionMembersChannel(
        channelType: String,
        channelId: String,
        add: List<String>,
        delete: List<String>
    ): Call<Channel> {
        return channelApi.updateMemberPermission(channelType, channelId, UpdatePermissionMembersRequest(
            remove_capabilities = delete,
            add_capabilities = add
        )
        ).map(this::flattenChannel)
    }

    override fun getAttachmentsOfChannel(channelType: String, channelId: String): Call<AttachmentResponse> {
        return channelApi.getAttachmentsOfChannel(channelType, channelId)
    }

    override fun searchMessageOfChannel(
        cid: String,
        search_term: String,
        limit: Int,
        offset: Int
    ): Call<List<Message>> {
        return channelApi.searchMessageOfChannel(
            SearchMessage(cid =cid, search_term = search_term, limit = limit, offset = offset)
        ).map { response ->
            response.search_result.messages.map { it ->
                Message(id = it.id, text = it.text, createdAt = it.created_at, user = User(id = it.user_id))
            }
        }
    }

    override fun updateChannelPartial(
        channelType: String,
        channelId: String,
        set: Map<String, Any>,
        unset: List<String>,
    ): Call<Channel> {
        return channelApi.updateChannelPartial(
            channelType = channelType,
            channelId = channelId,
            body = UpdateChannelPartialRequest(set, unset),
        ).map(this::flattenChannel)
    }

    override fun showChannel(
        channelType: String,
        channelId: String,
    ): Call<Unit> {
        return channelApi.showChannel(
            channelType = channelType,
            channelId = channelId,
            body = emptyMap(),
        ).toUnitCall()
    }

    override fun hideChannel(
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ): Call<Unit> {
        return channelApi.hideChannel(
            channelType = channelType,
            channelId = channelId,
            body = HideChannelRequest(clearHistory),
        ).toUnitCall()
    }

    override fun truncateChannel(
        channelType: String,
        channelId: String,
        systemMessage: Message?,
    ): Call<Channel> {
        return channelApi.truncateChannel(
            channelType = channelType,
            channelId = channelId,
            body = TruncateChannelRequest(message = systemMessage?.toDto()),
        ).map(this::flattenChannel)
    }

    override fun rejectInvite(channelType: String, channelId: String): Call<EmptyResponse> {
        return channelApi.rejectInvite(
            channelType = channelType,
            channelId = channelId,
        )
    }

    override fun acceptInvite(
        channelType: String,
        channelId: String,
        message: String?,
    ): Call<EmptyResponse> {
        return channelApi.acceptInvite(
            channelType = channelType,
            channelId = channelId,
            connectionId = connectionId,
        )
    }

    override fun deleteChannel(channelType: String, channelId: String): Call<Channel> {
        return channelApi.deleteChannel(
            channelType = channelType,
            channelId = channelId,
        ).map(this::flattenChannel)
    }

    override fun markRead(channelType: String, channelId: String, messageId: String): Call<Unit> {
        return channelApi.markRead(
            channelType = channelType,
            channelId = channelId,
            request = MarkReadRequest(messageId),
        ).toUnitCall()
    }

    override fun markUnread(channelType: String, channelId: String, messageId: String): Call<Unit> {
        return channelApi.markUnread(
            channelType = channelType,
            channelId = channelId,
            request = MarkUnreadRequest(messageId),
        ).toUnitCall()
    }

    override fun markAllRead(): Call<Unit> {
        return channelApi.markAllRead().toUnitCall()
    }

    override fun addMembers(
        channelType: String,
        channelId: String,
        members: List<String>,
        systemMessage: Message?,
        hideHistory: Boolean?,
        skipPush: Boolean?,
    ): Call<Channel> {
        return channelApi.addMembers(
            channelType = channelType,
            channelId = channelId,
            body = AddMembersRequest(
                members,
                systemMessage?.toDto(),
                hideHistory,
                skipPush
            ),
        ).map(this::flattenChannel)
    }

    override fun removeMembers(
        channelType: String,
        channelId: String,
        members: List<String>,
        systemMessage: Message?,
        skipPush: Boolean?,
    ): Call<Channel> {
        return channelApi.removeMembers(
            channelType = channelType,
            channelId = channelId,
            body = RemoveMembersRequest(
                members,
                systemMessage?.toDto(),
                skipPush
            ),
        ).map(this::flattenChannel)
    }

    override fun inviteMembers(
        channelType: String,
        channelId: String,
        members: List<String>,
        systemMessage: Message?,
        skipPush: Boolean?,
    ): Call<Channel> {
        return channelApi.inviteMembers(
            channelType = channelType,
            channelId = channelId,
            body = InviteMembersRequest(
                members,
                systemMessage?.toDto(),
                skipPush
            ),
        ).map(this::flattenChannel)
    }

    private fun flattenChannel(response: ChannelResponse): Channel {
        return response.channel.toDomain().let { channel ->
            channel.copy(
                watcherCount = response.watcher_count,
                read = response.read.map { it.toDomain(channel.lastMessageAt ?: it.last_read ?: Date()) },
                members = response.getMembers.map(DownstreamMemberDto::toDomain),
                membership = response.membership?.toDomain(),
                messages = response.messages.map { it.toDomain().enrichWithCid(channel.cid) },
                watchers = response.watchers.map(DownstreamUserDto::toDomain),
                hidden = response.hidden,
                hiddenMessagesBefore = response.hide_messages_before,
            ).syncUnreadCountWithReads()
        }
    }

    override fun getReplies(messageId: String, limit: Int): Call<List<Message>> {
        return messageApi.getReplies(
            messageId = messageId,
            limit = limit,
        ).map { response -> response.messages.map(DownstreamMessageDto::toDomain) }
    }

    override fun getRepliesMore(messageId: String, firstId: String, limit: Int): Call<List<Message>> {
        return messageApi.getRepliesMore(
            messageId = messageId,
            limit = limit,
            firstId = firstId,
        ).map { response -> response.messages.map(DownstreamMessageDto::toDomain) }
    }

    override fun sendAction(request: DomainSendActionRequest): Call<Message> {
        return messageApi.sendAction(
            messageId = request.messageId,
            request = SendActionRequest(
                channel_id = request.channelId,
                message_id = request.messageId,
                type = request.type,
                form_data = request.formData,
            ),
        ).map { response -> response.message.toDomain() }
    }

    override fun updateUsers(users: List<User>): Call<List<User>> {
        val map: Map<String, UpstreamUserDto> = users.associateBy({ it.id }, User::toDto)
        return userApi.updateUsers(
            connectionId = connectionId,
            body = UpdateUsersRequest(map),
        ).map { response ->
            response.users.values.map(DownstreamUserDto::toDomain)
        }
    }

    override fun getUserInfo(id: String, projectId: String?): Call<User> {
        return userApi.getUserInfo(id = id, projectId).map { response ->
            response.toDomain()
        }
    }

    override fun updateUserProfile(body: UpdateUserProfileRequest): Call<User> {
        return userApi.updateUserProfile(
            body = body
        ).map { response ->
            response.toDomain()
        }
    }

    override fun updateUserAvatar(file: File): Call<String> {
        val body = file.asRequestBody(file.getMediaType())
        val part = MultipartBody.Part.createFormData("avatar", file.name, body)
        return userApi.uploadAvatar(part).map { response ->
            response.avatar
        }
    }

    override fun partialUpdateUser(id: String, set: Map<String, Any>, unset: List<String>): Call<User> {
        return userApi.partialUpdateUsers(
            connectionId = connectionId,
            body = PartialUpdateUsersRequest(
                listOf(PartialUpdateUserDto(id = id, set = set, unset = unset)),
            ),
        ).map { response ->
            response.users[id]!!.toDomain()
        }
    }

    override fun getGuestUser(userId: String, userName: String): Call<GuestUser> {
        return guestApi.getGuestUser(
            body = GuestUserRequest.create(userId, userName),
        ).map { response -> GuestUser(response.user.toDomain(), response.access_token) }
    }

    override fun getUserInfoNoAuth(userId: String, projectId: String): Call<User> {
        return guestApi.getUserInfoNoAuth(userId, projectId)
            .map { response -> response.toDomain() }
    }

    override fun translate(messageId: String, language: String): Call<Message> {
        return messageApi.translate(
            messageId = messageId,
            request = TranslateMessageRequest(language),
        ).map { response -> response.message.toDomain() }
    }

    override fun og(url: String): Call<Attachment> {
        return ogApi.get(url).map { it.toDomain() }
    }

    override fun searchMessages(request: SearchMessagesRequest): Call<List<Message>> {
        val newRequest = network.ermis.client.api.model.requests.SearchMessagesRequest(
            filter_conditions = request.channelFilter.toMap(),
            message_filter_conditions = request.messageFilter.toMap(),
            offset = request.offset,
            limit = request.limit,
            next = request.next,
            sort = request.sort,
        )
        return generalApi.searchMessages(newRequest)
            .map { response ->
                response.results.map { resp ->
                    resp.message.toDomain()
                        .let { message ->
                            (message.cid.takeUnless(CharSequence::isBlank) ?: message.channelInfo?.cid)
                                ?.let(message::enrichWithCid)
                                ?: message
                        }
                }
            }
    }

    override fun searchMessages(
        channelFilter: FilterObject,
        messageFilter: FilterObject,
        offset: Int?,
        limit: Int?,
        next: String?,
        sort: QuerySorter<Message>?,
    ): Call<SearchMessagesResult> {
        val newRequest = network.ermis.client.api.model.requests.SearchMessagesRequest(
            filter_conditions = channelFilter.toMap(),
            message_filter_conditions = messageFilter.toMap(),
            offset = offset,
            limit = limit,
            next = next,
            sort = sort?.toDto(),
        )
        return generalApi.searchMessages(newRequest)
            .map { response ->
                val results = response.results

                val messages = results.map { resp ->
                    resp.message.toDomain().let { message ->
                        (message.cid.takeUnless(CharSequence::isBlank) ?: message.channelInfo?.cid)
                            ?.let(message::enrichWithCid)
                            ?: message
                    }
                }
                SearchMessagesResult(
                    messages = messages,
                    next = response.next,
                    previous = response.previous,
                    resultsWarning = response.resultsWarning?.toDomain(),
                )
            }
    }

    override fun queryChannels(query: QueryChannelsRequest): Call<List<Channel>> {
        logger.i { "[queryChannels] responsessss: $query"}

        val request = network.ermis.client.api.model.requests.QueryChannelsRequest(
            filter_conditions = query.filter.toMap(),
            offset = query.offset,
            limit = query.limit,
            sort = query.sort,
            message_limit = query.messageLimit,
            member_limit = query.memberLimit,
            state = query.state,
            watch = query.watch,
            presence = query.presence,
        )

        val lazyQueryChannelsCall = {
            logger.i { "[queryChannels] channelApi.queryChannels: $request"}
            channelApi.queryChannels(
                connectionId = connectionId,
                request = request,
            ).map { response ->
                logger.i { "[queryChannels] response: $response"}
                response.channels.map(this::flattenChannel)
            }
        }

        val isConnectionRequired = query.watch || query.presence
        return if (connectionId.isBlank() && isConnectionRequired) {
            logger.i { "[queryChannels] postponing because an active connection is required" }
            postponeCall(lazyQueryChannelsCall)
        } else {
            lazyQueryChannelsCall()
        }
    }

    override fun queryChannel(channelType: String, channelId: String, query: QueryChannelRequest): Call<Channel> {
        val request = network.ermis.client.api.model.requests.QueryChannelRequest(
            state = query.state,
            watch = query.watch,
            presence = query.presence,
            messages = query.messages,
            watchers = query.watchers,
            members = query.members,
            data = query.data,
            project_id = if (channelId.isEmpty()) projectId else channelId.channelIdToProjectId(),
        )

        val lazyQueryChannelCall = {
            if (channelId.isEmpty()) {
                channelApi.queryChannel(
                    channelType = channelType,
                    connectionId = connectionId,
                    request = request,
                )
            } else {
                channelApi.queryChannel(
                    channelType = channelType,
                    channelId = channelId,
                    connectionId = connectionId,
                    request = request,
                )
            }.map(::flattenChannel)
        }

        val isConnectionRequired = query.watch || query.presence
        return if (connectionId.isBlank() && isConnectionRequired) {
            logger.i { "[queryChannel] postponing because an active connection is required" }
            postponeCall(lazyQueryChannelCall)
        } else {
            lazyQueryChannelCall()
        }
    }

    override fun queryUsers(queryUsers: QueryUsersRequest, projectId: String?): Call<List<User>> {
        val lazyQueryUsersCall = {
            userApi.queryUsers(
                projectId = projectId,
                name = queryUsers.name,
                limit = queryUsers.limit,
                page = queryUsers.page,
            ).map { response -> response.data.map(DownstreamUserDto::toDomain) }
        }

        return if (connectionId.isBlank()) {
            postponeCall(lazyQueryUsersCall)
        } else {
            lazyQueryUsersCall()
        }
    }

    override fun getUsersByIds(body: UsersByIdsRequest): Call<List<User>> {
        return userApi.getUsersByIds(body = body)
            .map { response -> response.data.map(DownstreamUserDto::toDomain) }
    }

    override fun queryMembers(
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    ): Call<List<Member>> {
        val request = QueryMembersRequest(
            type = channelType,
            id = channelId,
            filter_conditions = filter.toMap(),
            offset = offset,
            limit = limit,
            sort = sort.toDto(),
            members = members.map(Member::toDto),
        )

        return generalApi.queryMembers(request)
            .map { response -> response.members.map(DownstreamMemberDto::toDomain) }
    }

    override fun sendVideoCallSignal(cid: String, action: String, type: String, sdp: String): Call<SignalCallResponse>{
        return callApi.sendCallSignal(
            connectionId = connectionId,
            request = VideoCallSignalRequest(
                cid = cid,
                action = action,
                signal = SignalCall(type = type, sdp = sdp)
            )
        )
    }

    override fun sendEvent(
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
    ): Call<ChatEvent> {
        val map = mutableMapOf<Any, Any>("type" to eventType)
        map.putAll(extraData)

        return channelApi.sendEvent(
            channelType = channelType,
            channelId = channelId,
            request = SendEventRequest(map),
        ).map { response -> response.event.toDomain() }
    }

    override fun getSyncHistory(channelIds: List<String>, lastSyncAt: String): Call<List<ChatEvent>> {
        return generalApi.getSyncHistory(
            body = SyncHistoryRequest(channelIds, lastSyncAt),
            connectionId = connectionId,
        ).map { response -> response.events.map(ChatEventDto::toDomain) }
    }

    override fun downloadFile(fileUrl: String): Call<ResponseBody> {
        return fileDownloadApi.downloadFile(fileUrl)
    }

    override fun warmUp() {
        generalApi.warmUp().enqueue()
    }

    override fun walletConnect(address: String): Call<WalletConnectResponse> {
        return walletApi.walletConnect(WalletConnectRequest(address))
    }

    override fun walletSignin(address: String, signature: String, apiKey: String, nonce: String): Call<GetTokenLoginResponse> {
        return walletApi.walletSignin(WalletSigninRequest(signature = signature, address = address, nonce = nonce, apiKey = apiKey))
    }

    override fun getChains(): Call<ErmisChainResponse> {
        return ermisClienApi.getChains()
    }

    override fun getClientsByChainId(chainId: String): RetrofitCall<List<ErmisClientModel>> {
        return ermisClienApi.getClientsByChainId(GetClientRequest(chainId))
    }

    override fun getProjects(clientId: String, chainId: String): RetrofitCall<List<ErmisProject>> {
        return ermisClienApi.getProjects(
            GetProjectRequest(
                client_id = clientId,
                chain_id = chainId
            )
        )
    }

    override fun joinProject(params: JoinProjectRequest): Call<ErmisChainResponse> {
        return ermisClienApi.joinProject(params)
    }

    override fun queryAllChannelOfAllProject(): Call<List<Channel>> {
        val body = network.ermis.client.api.model.requests.QueryChannelsRequest(
            filter_conditions = emptyMap<String, String>(),
            offset = 0,
            limit = 10000,
            sort = listOf(),
            message_limit = 1,
            member_limit = 0,
            state = false,
            watch = false,
            presence = false,
        )
        return channelApi.queryAllChannelOfAllProject(body).map { response ->
            logger.i { "[queryChannels] response: $response"}
            response.channels.map(this::flattenChannel)
        }
    }

    private fun <T : Any> postponeCall(call: () -> Call<T>): Call<T> {
        return callPostponeHelper.postponeCall(call)
    }
}
