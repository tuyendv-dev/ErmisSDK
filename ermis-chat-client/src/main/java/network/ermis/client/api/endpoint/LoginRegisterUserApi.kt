package network.ermis.client.api.endpoint

import network.ermis.client.api.AnonymousApi
import network.ermis.client.api.model.requests.UserLoginRequest
import network.ermis.client.api.model.requests.UserRegisterRequest
import network.ermis.client.api.model.response.UserLoginResponse
import network.ermis.client.api.model.response.UserRegisterResponse
import network.ermis.client.call.RetrofitCall
import retrofit2.http.Body
import retrofit2.http.POST

@AnonymousApi // call api no check token header
internal interface LoginRegisterUserApi {
    @POST("/register")
    fun userRegister(
        @Body body: UserRegisterRequest,
    ): RetrofitCall<UserRegisterResponse>

    @POST("/login")
    fun userLogin(
        @Body body: UserLoginRequest,
    ): RetrofitCall<UserLoginResponse>

}
