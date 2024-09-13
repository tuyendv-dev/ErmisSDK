package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.UrlQueryPayload
import network.ermis.client.api.model.response.CompletableResponse
import network.ermis.client.api.model.response.FlagResponse
import network.ermis.client.api.model.response.MuteUserResponse
import network.ermis.client.api.model.response.QueryBannedUsersResponse
import network.ermis.client.call.RetrofitCall
import network.ermis.client.api.model.requests.BanUserRequest
import network.ermis.client.api.model.requests.MuteChannelRequest
import network.ermis.client.api.model.requests.MuteUserRequest
import network.ermis.client.api.model.requests.QueryBannedUsersRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@AuthenticatedApi
internal interface ModerationApi {

    @POST("/moderation/mute")
    fun muteUser(@Body body: MuteUserRequest): RetrofitCall<MuteUserResponse>

    @POST("/moderation/unmute")
    fun unmuteUser(@Body body: MuteUserRequest): RetrofitCall<CompletableResponse>

    @POST("/moderation/mute/channel")
    fun muteChannel(@Body body: MuteChannelRequest): RetrofitCall<CompletableResponse>

    @POST("/moderation/unmute/channel")
    fun unmuteChannel(@Body body: MuteChannelRequest): RetrofitCall<CompletableResponse>

    @POST("/moderation/flag")
    fun flag(@Body body: Map<String, String>): RetrofitCall<FlagResponse>

    @POST("/moderation/unflag")
    fun unflag(@Body body: Map<String, String>): RetrofitCall<FlagResponse>

    @POST("/moderation/ban")
    fun banUser(@Body body: BanUserRequest): RetrofitCall<CompletableResponse>

    @DELETE("/moderation/ban")
    fun unbanUser(
        @Query("target_user_id") targetUserId: String,
        @Query("type") channelType: String,
        @Query("id") channelId: String,
        @Query("shadow") shadow: Boolean,
    ): RetrofitCall<CompletableResponse>

    @GET("/query_banned_users")
    fun queryBannedUsers(
        @UrlQueryPayload @Query("payload") payload: QueryBannedUsersRequest,
    ): RetrofitCall<QueryBannedUsersResponse>
}
