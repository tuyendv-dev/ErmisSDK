package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.model.requests.UpdateMessageRequest
import network.ermis.client.api.model.response.MessageResponse
import network.ermis.client.api.model.response.MessagesResponse
import network.ermis.client.api.model.response.ReactionResponse
import network.ermis.client.api.model.response.ReactionsResponse
import network.ermis.client.api.model.response.TranslateMessageRequest
import network.ermis.client.call.RetrofitCall
import network.ermis.client.api.model.requests.PartialUpdateMessageRequest
import network.ermis.client.api.model.requests.SendActionRequest
import network.ermis.client.api.model.requests.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("TooManyFunctions")
@AuthenticatedApi
internal interface MessageApi {

    /**
     * [REST documentation](https://getstream.io/chat/docs/rest/#messages-sendmessage)
     */
    @POST("/channels/{type}/{id}/message")
    fun sendMessage(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Body message: SendMessageRequest,
    ): RetrofitCall<MessageResponse>

    @GET("/messages/{id}")
    fun getMessage(@Path("id") messageId: String): RetrofitCall<MessageResponse>

    // /**
    //  * [REST documentation]()https://getstream.io/chat/docs/rest/#messages-updatemessage)
    //  */
    // @POST("/messages/{id}")
    // fun updateMessage(
    //     @Path("id") messageId: String,
    //     @Body message: UpdateMessageRequest,
    // ): RetrofitCall<MessageResponse>

    @POST("/messages/{channelType}/{channelId}/{messageId}")
    fun updateMessage(
        @Path("channelType") channelType: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
        @Body message: UpdateMessageRequest,
    ): RetrofitCall<MessageResponse>

    /**
     * [Rest documentation](https://getstream.io/chat/docs/rest/#messages-updatemessagepartial-request)
     */
    @PUT("/messages/{id}")
    fun partialUpdateMessage(
        @Path("id") messageId: String,
        @Body body: PartialUpdateMessageRequest,
    ): RetrofitCall<MessageResponse>

    @DELETE("/messages/{channelType}/{channelId}/{messageId}")
    fun deleteMessage(
        @Path("channelType") channelType: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String
    ): RetrofitCall<MessageResponse>
    
    @POST("/messages/{id}/action")
    fun sendAction(
        @Path("id") messageId: String,
        @Body request: SendActionRequest,
    ): RetrofitCall<MessageResponse>

    @POST("/messages/{channelType}/{channelId}/{messageId}/reaction/{reactionType}")
    fun sendReaction(
        @Path("channelType") channelType: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
        @Path("reactionType") reactionType: String,
        // @Body request: ReactionRequest,
    ): RetrofitCall<ReactionResponse>

    @DELETE("/messages/{channelType}/{channelId}/{messageId}/reaction/{reactionType}")
    fun deleteReaction(
        @Path("channelType") channelType: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
        @Path("reactionType") reactionType: String,
    ): RetrofitCall<MessageResponse>

    @GET("/messages/{id}/reactions")
    fun getReactions(
        @Path("id") messageId: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): RetrofitCall<ReactionsResponse>

    @POST("/messages/{messageId}/translate")
    fun translate(
        @Path("messageId") messageId: String,
        @Body request: TranslateMessageRequest,
    ): RetrofitCall<MessageResponse>

    @GET("/messages/{parent_id}/replies")
    fun getReplies(
        @Path("parent_id") messageId: String,
        @Query("limit") limit: Int,
    ): RetrofitCall<MessagesResponse>

    @GET("/messages/{parent_id}/replies")
    fun getRepliesMore(
        @Path("parent_id") messageId: String,
        @Query("limit") limit: Int,
        @Query("id_lt") firstId: String,
    ): RetrofitCall<MessagesResponse>
}
