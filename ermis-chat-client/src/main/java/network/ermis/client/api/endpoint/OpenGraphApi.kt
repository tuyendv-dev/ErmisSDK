package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.QueryParams
import network.ermis.client.api.model.dto.AttachmentDto
import network.ermis.client.call.RetrofitCall
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API for open graph data.
 */
@AuthenticatedApi
internal interface OpenGraphApi {

    @GET("/og")
    fun get(@Query(QueryParams.URL) url: String): RetrofitCall<AttachmentDto>
}
