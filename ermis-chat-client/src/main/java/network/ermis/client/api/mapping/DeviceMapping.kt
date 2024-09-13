package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DeviceDto
import network.ermis.core.models.Device
import network.ermis.core.models.PushProvider

internal fun Device.toDto(): DeviceDto =
    DeviceDto(
        id = token,
        push_provider = pushProvider.key,
        provider_name = providerName,
    )

internal fun DeviceDto.toDomain(): Device =
    Device(
        token = id,
        pushProvider = PushProvider.fromKey(push_provider),
        providerName = provider_name,
    )
