package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamReactionDto
import network.ermis.client.api.model.dto.UpstreamReactionDto
import network.ermis.core.models.Reaction

internal fun Reaction.toDto(): UpstreamReactionDto =
    UpstreamReactionDto(
        created_at = createdAt,
        message_id = messageId,
        score = score ?: 0,
        type = type,
        updated_at = updatedAt,
        user = user?.toDto(),
        user_id = userId,
        extraData = extraData,
    )

internal fun DownstreamReactionDto.toDomain(): Reaction =
    Reaction(
        createdAt = created_at,
        messageId = message_id,
        score = score ?: 0,
        type = type,
        updatedAt = updated_at,
        user = user?.toDomain(),
        userId = user_id,
        extraData = extraData.toMutableMap(),
    )
