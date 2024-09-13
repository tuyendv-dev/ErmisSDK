
package network.ermis.ui.utils.extensions

import android.content.Context
import network.ermis.core.models.User
import network.ermis.ui.ChatUI
import network.ermis.ui.components.R
import network.ermis.ui.utils.extension.getLastSeenText

/**
 * Returns a string describing the elapsed time since the user was online (was watching the channel).
 *
 * Depending on the elapsed time, the string can have one of the following formats:
 * - Online
 * - Last seen just now
 * - Last seen 13 hours ago
 *
 * @param context The context to load string resources.
 * @return A string that represents the elapsed time since the user was online.
 */
public fun User.getLastSeenText(context: Context): String {
    return getLastSeenText(
        context = context,
        userOnlineResId = R.string.ermis_ui_user_status_online,
        userLastSeenJustNowResId = R.string.ermis_ui_user_status_last_seen_just_now,
        userLastSeenResId = R.string.ermis_ui_user_status_last_seen,
    )
}

internal fun User.isCurrentUser(): Boolean {
    return id == ChatUI.currentUserProvider.getCurrentUser()?.id
}

internal fun User.asMention(context: Context): String =
    context.getString(R.string.ermis_ui_mention, name)
