package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamChannelMuteDto
import network.ermis.core.models.ChannelMute

internal fun DownstreamChannelMuteDto.toDomain(): ChannelMute =
    ChannelMute(
        user = user.toDomain(),
        channel = channel.toDomain(),
        createdAt = created_at,
        updatedAt = updated_at,
        expires = expires,
    )
