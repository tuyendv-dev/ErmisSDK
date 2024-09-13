package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BanUserRequest(
    val target_user_id: String,
    val timeout: Int?,
    val reason: String?,
    val type: String,
    val id: String,
    val shadow: Boolean,
)
