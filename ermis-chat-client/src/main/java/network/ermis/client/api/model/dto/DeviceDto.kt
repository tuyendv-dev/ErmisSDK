package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DeviceDto(
    val id: String,
    val push_provider: String,
    val provider_name: String?,
)
