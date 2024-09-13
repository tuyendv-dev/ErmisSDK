package network.ermis.client.utils.extensions.internal

import network.ermis.core.models.Member
import network.ermis.core.models.User

/** Updates collection of members with more recent data of [users]. */
public fun Collection<Member>.updateUsers(userMap: Map<String, User>): Collection<Member> = map { member ->
    if (userMap.containsKey(member.getUserId())) {
        member.copy(user = userMap[member.getUserId()] ?: member.user)
    } else {
        member
    }
}
