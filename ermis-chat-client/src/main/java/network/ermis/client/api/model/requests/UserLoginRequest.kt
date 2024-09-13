package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UserLoginRequest(
    val user_id: String,
    val password: String,
)