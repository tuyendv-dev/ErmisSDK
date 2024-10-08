package network.ermis.state.event.handler.model

import network.ermis.core.models.User

/**
 * Represents currently logged in user.
 */
internal sealed class SelfUser {
    abstract val me: User
}

/**
 * Contains a full [User] information.
 */
internal data class SelfUserFull(override val me: User) : SelfUser()

/**
 * Contains a limited [User] information, like [User.id], [User.role], [User.name], [User.online], [User.banned],
 * [User.image], [User.createdAt], [User.updatedAt], [User.lastActive]
 */
internal data class SelfUserPart(override val me: User) : SelfUser()
