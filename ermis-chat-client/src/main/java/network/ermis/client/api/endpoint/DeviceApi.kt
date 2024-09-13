package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.QueryParams
import network.ermis.client.api.model.response.CompletableResponse
import network.ermis.client.api.model.response.DevicesResponse
import network.ermis.client.call.RetrofitCall
import network.ermis.client.api.model.requests.AddDeviceRequest
import network.ermis.client.api.model.requests.DeleteDeviceRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@AuthenticatedApi
internal interface DeviceApi {

    @GET("/devices")
    fun getDevices(): RetrofitCall<DevicesResponse>

    @POST("/devices/add")
    fun addDevices(
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body request: AddDeviceRequest
    ): RetrofitCall<CompletableResponse>

    @POST("/devices/delete")
    fun deleteDevice(
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body deviceId: DeleteDeviceRequest
    ): RetrofitCall<CompletableResponse>
}
