package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.QueryParams
import network.ermis.client.api.UrlQueryPayload
import network.ermis.client.api.model.response.QueryMembersResponse
import network.ermis.client.api.model.response.SearchMessagesResponse
import network.ermis.client.api.model.response.SyncHistoryResponse
import network.ermis.client.call.RetrofitCall
import network.ermis.client.api.model.requests.QueryMembersRequest
import network.ermis.client.api.model.requests.SearchMessagesRequest
import network.ermis.client.api.model.requests.SyncHistoryRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.OPTIONS
import retrofit2.http.POST
import retrofit2.http.Query

@AuthenticatedApi
internal interface GeneralApi {
    @OPTIONS("/connect")
    fun warmUp(): RetrofitCall<ResponseBody>

    @POST("/sync")
    fun getSyncHistory(
        @Body body: SyncHistoryRequest,
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
    ): RetrofitCall<SyncHistoryResponse>

    @GET("/search")
    fun searchMessages(
        @UrlQueryPayload @Query("payload") payload: SearchMessagesRequest,
    ): RetrofitCall<SearchMessagesResponse>

    @GET("/members")
    fun queryMembers(
        @UrlQueryPayload @Query("payload") payload: QueryMembersRequest,
    ): RetrofitCall<QueryMembersResponse>
}
