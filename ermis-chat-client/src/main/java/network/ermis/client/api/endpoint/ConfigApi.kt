package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.model.response.AppSettingsResponse
import network.ermis.client.call.RetrofitCall
import retrofit2.http.GET

/**
 * API for configurations, settings in the dashboard with read and write possibilities (not mandatorily).
 */
@AuthenticatedApi
internal interface ConfigApi {

    @GET("/app")
    fun getAppSettings(): RetrofitCall<AppSettingsResponse>
}
