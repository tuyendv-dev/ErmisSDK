package network.ermis.client.utils.extensions.internal

import network.ermis.core.models.User

/** Updates a collection of users by more fresh value of [users]. */
public fun Collection<User>.updateUsers(users: Map<String, User>): List<User> = map { user -> users[user.id] ?: user }
