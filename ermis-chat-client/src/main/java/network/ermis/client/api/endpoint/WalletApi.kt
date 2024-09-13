package network.ermis.client.api.endpoint

import network.ermis.client.api.AnonymousApi
import network.ermis.client.api.model.requests.WalletConnectRequest
import network.ermis.client.api.model.requests.WalletSigninRequest
import network.ermis.client.api.model.response.GetTokenLoginResponse
import network.ermis.client.api.model.response.WalletConnectResponse
import network.ermis.client.call.RetrofitCall
import retrofit2.http.Body
import retrofit2.http.POST

@AnonymousApi
internal interface WalletApi {
    @POST("/uss/v1/wallets/auth/start")
    fun walletConnect(@Body params: WalletConnectRequest): RetrofitCall<WalletConnectResponse>

    @POST("/uss/v1/wallets/auth")
    fun walletSignin(@Body params: WalletSigninRequest): RetrofitCall<GetTokenLoginResponse>

}
