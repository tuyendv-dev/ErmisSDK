package network.ermis.client.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class GetTokenLoginResponse(

    @Json(name = "token")
    val token: String = "",

    @Json(name = "refresh_token")
    val refresh_token: String = "",

    @Json(name = "user_id")
    val user_id: String = "",

    @Json(name = "project_id")
    val project_id: String = "",
)
