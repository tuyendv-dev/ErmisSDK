package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class UserRegisterResponse(
    val token: String
)