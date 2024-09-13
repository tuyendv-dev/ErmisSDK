package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AddDeviceRequest(
    val id: String,
    val push_provider: String,
    val push_provider_name: String?,
)
