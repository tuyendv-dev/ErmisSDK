package network.ermis.ui.utils.extension

import network.ermis.core.models.FilterObject
import network.ermis.core.models.Filters
import network.ermis.core.models.User

/**
 * Create the default channel list filter for the given user.
 *
 * @param user The currently logged in user.
 * @return The default filter for the channel list view.
 */
public fun Filters.defaultChannelListFilter(user: User?): FilterObject? {
    return if (user == null) {
        null
    } else {
        and(
            eq("type", "team"),//"messaging"),
            `in`("members", listOf(user.id)),
        )
    }
}
