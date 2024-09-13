package network.ermis.client.api.mapping

import network.ermis.client.api.model.response.BannedUserResponse
import network.ermis.core.models.BannedUser

internal fun BannedUserResponse.toDomain(): BannedUser {
    return BannedUser(
        user = user.toDomain(),
        bannedBy = banned_by?.toDomain(),
        channel = channel?.toDomain(),
        createdAt = created_at,
        expires = expires,
        shadow = shadow,
        reason = reason,
    )
}
