package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamMuteDto
import network.ermis.client.api.model.dto.UpstreamMuteDto
import network.ermis.core.models.Mute

internal fun Mute.toDto(): UpstreamMuteDto =
    UpstreamMuteDto(
        user = user.toDto(),
        target = target.toDto(),
        created_at = createdAt,
        updated_at = updatedAt,
        expires = expires,
    )

internal fun DownstreamMuteDto.toDomain(): Mute =
    Mute(
        user = user.toDomain(),
        target = target.toDomain(),
        createdAt = created_at,
        updatedAt = updated_at,
        expires = expires,
    )
