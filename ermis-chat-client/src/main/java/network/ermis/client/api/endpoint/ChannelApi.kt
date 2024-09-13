package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.QueryParams
import network.ermis.client.api.UrlQueryPayload
import network.ermis.client.api.model.requests.UpdatePermissionMembersRequest
import network.ermis.client.api.model.response.AttachmentResponse
import network.ermis.client.api.model.response.ChannelResponse
import network.ermis.client.api.model.response.CompletableResponse
import network.ermis.client.api.model.response.EmptyResponse
import network.ermis.client.api.model.response.EventResponse
import network.ermis.client.api.model.response.ListContactIdsResponse
import network.ermis.client.api.model.response.MessagesResponse
import network.ermis.client.api.model.response.QueryChannelsResponse
import network.ermis.client.api.model.response.SearchMessageResponse
import network.ermis.client.call.RetrofitCall
import network.ermis.client.api.model.requests.AddMembersRequest
import network.ermis.client.api.model.requests.BanMembersRequest
import network.ermis.client.api.model.requests.DemoteMemberRequest
import network.ermis.client.api.model.requests.HideChannelRequest
import network.ermis.client.api.model.requests.InviteMembersRequest
import network.ermis.client.api.model.requests.MarkReadRequest
import network.ermis.client.api.model.requests.MarkUnreadRequest
import network.ermis.client.api.model.requests.PinnedMessagesRequest
import network.ermis.client.api.model.requests.PromoteMemberRequest
import network.ermis.client.api.model.requests.QueryChannelRequest
import network.ermis.client.api.model.requests.QueryChannelsRequest
import network.ermis.client.api.model.requests.RemoveMembersRequest
import network.ermis.client.api.model.requests.SearchMessage
import network.ermis.client.api.model.requests.SendEventRequest
import network.ermis.client.api.model.requests.TruncateChannelRequest
import network.ermis.client.api.model.requests.UnBanMembersRequest
import network.ermis.client.api.model.requests.UpdateChannelPartialRequest
import network.ermis.client.api.model.requests.UpdateChannelRequest
import network.ermis.client.api.model.requests.UpdateCooldownRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("TooManyFunctions")
@AuthenticatedApi
internal interface ChannelApi {

    @POST("/channels")
    fun queryChannels(
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body request: QueryChannelsRequest,
    ): RetrofitCall<QueryChannelsResponse>

    @POST("/channels/{type}/query")
    fun queryChannel(
        @Path("type") channelType: String,
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body request: QueryChannelRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/read")
    fun markAllRead(
        @Body map: Map<String, String> = emptyMap(),
    ): RetrofitCall<CompletableResponse>

    @POST("/channels/{type}/{id}")
    fun updateChannel(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: UpdateChannelRequest,
    ): RetrofitCall<ChannelResponse>

    @PATCH("/channels/{type}/{id}")
    @JvmSuppressWildcards // See issue: https://github.com/square/retrofit/issues/3275
    fun updateChannelPartial(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: UpdateChannelPartialRequest,
    ): RetrofitCall<ChannelResponse>

    @PATCH("/channels/{type}/{id}")
    @JvmSuppressWildcards // See issue: https://github.com/square/retrofit/issues/3275
    fun updateCooldown(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: UpdateCooldownRequest,
    ): RetrofitCall<ChannelResponse>

    @DELETE("/channels/{type}/{id}")
    fun deleteChannel(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
    ): RetrofitCall<ChannelResponse>
    @POST("/uss/v1/token_gate/join_channel/{type}?")
    fun acceptInvite(
        @Path("type") channelType: String,
        @Query("channel_id") channelId: String,
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
    ): RetrofitCall<EmptyResponse>

    @POST("/invites/{type}/{id}/reject")
    fun rejectInvite(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
    ): RetrofitCall<EmptyResponse>

    @POST("/channels/{type}/{id}")
    fun addMembers(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: AddMembersRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}")
    fun removeMembers(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: RemoveMembersRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}")
    fun inviteMembers(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: InviteMembersRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}/event")
    fun sendEvent(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body request: SendEventRequest,
    ): RetrofitCall<EventResponse>

    @POST("/channels/{type}/{id}/hide")
    fun hideChannel(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: HideChannelRequest,
    ): RetrofitCall<CompletableResponse>

    @POST("/channels/{type}/{id}/truncate")
    fun truncateChannel(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: TruncateChannelRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}/query")
    fun queryChannel(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body request: QueryChannelRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}/read")
    fun markRead(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body request: MarkReadRequest,
    ): RetrofitCall<CompletableResponse>

    @POST("/channels/{type}/{id}/unread")
    fun markUnread(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body request: MarkUnreadRequest,
    ): RetrofitCall<CompletableResponse>

    @POST("/channels/{type}/{id}/show")
    @JvmSuppressWildcards // See issue: https://github.com/square/retrofit/issues/3275
    fun showChannel(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: Map<Any, Any>,
    ): RetrofitCall<CompletableResponse>

    @POST("/channels/{type}/{id}/stop-watching")
    @JvmSuppressWildcards // See issue: https://github.com/square/retrofit/issues/3275
    fun stopWatching(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body body: Map<Any, Any>,
    ): RetrofitCall<CompletableResponse>

    @GET("/channels/{type}/{id}/pinned_messages")
    fun getPinnedMessages(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @UrlQueryPayload @Query("payload") payload: PinnedMessagesRequest,
    ): RetrofitCall<MessagesResponse>

    @POST("/contacts/list")
    fun getContactsList(@Body body: Map<String, String>): RetrofitCall<ListContactIdsResponse>

    @POST("/channels")
    fun queryAllChannelOfAllProject(@Body request: QueryChannelsRequest): RetrofitCall<QueryChannelsResponse>

    @POST("/channels/{type}/{id}")
    fun banMembers(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: BanMembersRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}")
    fun unbanMembers(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: UnBanMembersRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}")
    fun promoteMembers(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: PromoteMemberRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}")
    fun demoteMembers(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: DemoteMemberRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}")
    fun updateMemberPermission(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body body: UpdatePermissionMembersRequest,
    ): RetrofitCall<ChannelResponse>

    @POST("/channels/{type}/{id}/attachment")
    fun getAttachmentsOfChannel(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
    ): RetrofitCall<AttachmentResponse>

    @POST("/channels/search")
    fun searchMessageOfChannel(
        @Body body: SearchMessage,
    ): RetrofitCall<SearchMessageResponse>
}
