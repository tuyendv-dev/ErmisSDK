package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UpdateUserProfileRequest (
    val name: String? = null,
    val phone: String? = null,
    val about_me: String? = null,
)