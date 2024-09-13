package network.ermis.client.api.model.response

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.DeviceDto

@JsonClass(generateAdapter = true)
internal data class DevicesResponse(
    val devices: List<DeviceDto>,
)
