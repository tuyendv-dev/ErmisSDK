package network.ermis.client.api

import network.ermis.client.api.models.CompletableResponse
import network.ermis.client.api.models.UploadFileResponse
import network.ermis.client.call.RetrofitCall
import network.ermis.client.utils.ProgressCallback
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Tag

@AuthenticatedApi
internal interface RetrofitCdnApi {
    @Multipart
    @POST("/channels/{type}/{id}/image")
    fun sendImage(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Part file: MultipartBody.Part,
        @Tag progressCallback: ProgressCallback?,
    ): RetrofitCall<UploadFileResponse>

    @Multipart
    @POST("/channels/{type}/{id}/file")
    fun sendFile(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Part file: MultipartBody.Part,
        @Tag progressCallback: ProgressCallback?,
    ): RetrofitCall<UploadFileResponse>

    @DELETE("/channels/{type}/{id}/file")
    fun deleteFile(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Query("url") url: String,
    ): RetrofitCall<CompletableResponse>

    @DELETE("/channels/{type}/{id}/image")
    fun deleteImage(
        @Path("type") channelType: String,
        @Path("id") channelId: String,
        @Query("url") url: String,
    ): RetrofitCall<CompletableResponse>
}
