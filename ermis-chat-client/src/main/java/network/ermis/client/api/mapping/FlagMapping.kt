package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamFlagDto
import network.ermis.core.models.Flag

internal fun DownstreamFlagDto.toDomain(): Flag {
    return Flag(
        user = user.toDomain(),
        targetUser = target_user?.toDomain(),
        targetMessageId = target_message_id,
        reviewedBy = created_at,
        createdByAutomod = created_by_automod,
        createdAt = approved_at,
        updatedAt = updated_at,
        reviewedAt = reviewed_at,
        approvedAt = approved_at,
        rejectedAt = rejected_at,
    )
}
