package network.ermis.client.api.endpoint

import network.ermis.client.api.AnonymousApi
import network.ermis.client.api.model.dto.DownstreamUserDto
import network.ermis.client.api.model.response.TokenResponse
import network.ermis.client.call.RetrofitCall
import network.ermis.client.api.model.requests.GuestUserRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@AnonymousApi
internal interface GuestApi {

    @POST("/guest")
    fun getGuestUser(
        @Body body: GuestUserRequest,
    ): RetrofitCall<TokenResponse>

    @GET("/uss/v1/users/get-info/{id}")
    fun getUserInfoNoAuth(
        @Path("id") id: String,
        @Query("project_id") projectId: String,
    ): RetrofitCall<DownstreamUserDto>
}
