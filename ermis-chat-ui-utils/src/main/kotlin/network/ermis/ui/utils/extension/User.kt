package network.ermis.ui.utils.extension

import android.content.Context
import android.text.format.DateUtils
import androidx.annotation.StringRes
import network.ermis.core.models.User

/**
 * Returns a string describing the elapsed time since the user was online (was watching the channel).
 *
 * Depending on the elapsed time, the string can have one of the following formats:
 * - Online
 * - Last seen just now
 * - Last seen 13 hours ago
 *
 * @param context The context to load string resources.
 * @param userOnlineResId Resource id for the online text.
 * @param userLastSeenJustNowResId Resource id for the just now text.
 * @param userLastSeenResId Resource id for the last seen text.
 * @return A string that represents the elapsed time since the user was online.
 */
public fun User.getLastSeenText(
    context: Context,
    @StringRes userOnlineResId: Int,
    @StringRes userLastSeenJustNowResId: Int,
    @StringRes userLastSeenResId: Int,
): String {
    if (online) {
        return context.getString(userOnlineResId)
    }

    return (lastActive ?: updatedAt ?: createdAt)?.let {
        if (it.isInLastMinute()) {
            context.getString(userLastSeenJustNowResId)
        } else {
            context.getString(
                userLastSeenResId,
                DateUtils.getRelativeTimeSpanString(it.time).toString(),
            )
        }
    } ?: ""
}
