package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.model.response.ErmisChainResponse
import network.ermis.client.api.model.response.ErmisClientModel
import network.ermis.client.api.model.response.ErmisProject
import network.ermis.client.call.RetrofitCall
import network.ermis.client.api.model.requests.GetClientRequest
import network.ermis.client.api.model.requests.GetProjectRequest
import network.ermis.client.api.model.requests.JoinProjectRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@AuthenticatedApi
internal interface ErmisClientApi {

    @POST("/uss/v1/users/clients")
    fun getClientsByChainId(@Body params: GetClientRequest): RetrofitCall<List<ErmisClientModel>>

    @POST("/uss/v1/users/projects")
    fun getProjects(@Body params: GetProjectRequest): RetrofitCall<List<ErmisProject>>

    @GET("/uss/v1/users/chains")
    fun getChains(): RetrofitCall<ErmisChainResponse>

    @POST("/uss/v1/users/join")
    fun joinProject(@Body params: JoinProjectRequest): RetrofitCall<ErmisChainResponse>
}
