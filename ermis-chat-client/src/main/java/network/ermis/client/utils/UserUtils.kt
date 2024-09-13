package network.ermis.client.utils

import network.ermis.core.models.User

/**
 * Partially merges [that] user data into [this] user data.
 */
public fun User.mergePartially(that: User): User = this.copy(
    role = that.role,
    createdAt = that.createdAt,
    updatedAt = that.updatedAt,
    lastActive = that.lastActive,
    banned = that.banned,
    name = that.name,
    image = that.image,
    extraData = that.extraData,
)
