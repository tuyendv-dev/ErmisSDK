package network.ermis.client.api.endpoint

import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.QueryParams
import network.ermis.client.api.model.dto.DownstreamUserDto
import network.ermis.client.api.model.requests.PartialUpdateUsersRequest
import network.ermis.client.api.model.requests.UpdateUserProfileRequest
import network.ermis.client.api.model.requests.UpdateUsersRequest
import network.ermis.client.api.model.requests.UsersByIdsRequest
import network.ermis.client.api.model.response.UpdateUsersResponse
import network.ermis.client.api.model.response.UploadUserAvatarResponse
import network.ermis.client.api.model.response.UsersResponse
import network.ermis.client.call.RetrofitCall
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

@AuthenticatedApi
internal interface UserApi {
    @POST("/users")
    fun updateUsers(
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body body: UpdateUsersRequest,
    ): RetrofitCall<UpdateUsersResponse>

    @PATCH("/users")
    @JvmSuppressWildcards // See issue: https://github.com/square/retrofit/issues/3275
    fun partialUpdateUsers(
        @Query(QueryParams.CONNECTION_ID) connectionId: String,
        @Body body: PartialUpdateUsersRequest,
    ): RetrofitCall<UpdateUsersResponse>

    @POST("/uss/v1/users/search")
    fun queryUsers(
        @Query("project_id") projectId: String?,
        @Query("name") name: String,
        @Query("page_size") limit: Int,
        @Query("page") page: Int,
    ): RetrofitCall<UsersResponse>

    @POST("/uss/v1/users/batch")
    fun getUsersByIds(
        @Body body: UsersByIdsRequest,
    ): RetrofitCall<UsersResponse>

    @GET("/uss/v1/users/{id}")
    fun getUserInfo(
        @Path("id") id: String,
        @Query("project_id") projectId: String?,
    ): RetrofitCall<DownstreamUserDto>

    @PATCH("/uss/v1/users/update")
    fun updateUserProfile(
        @Body body: UpdateUserProfileRequest,
    ): RetrofitCall<DownstreamUserDto>

    @Multipart
    @POST("/uss/v1/users/upload")
    fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): RetrofitCall<UploadUserAvatarResponse>
}
