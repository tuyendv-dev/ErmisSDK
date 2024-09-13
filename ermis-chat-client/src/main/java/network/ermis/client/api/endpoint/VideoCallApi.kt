package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.QueryParams
import network.ermis.client.api.model.requests.VideoCallSignalRequest
import network.ermis.client.api.model.response.SignalCallResponse
import network.ermis.client.call.RetrofitCall
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

@AuthenticatedApi
internal interface VideoCallApi {

    @POST("/signal?")
    fun sendCallSignal(
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body request: VideoCallSignalRequest,
    ): RetrofitCall<SignalCallResponse>
}
