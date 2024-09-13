
package network.ermis.ui.utils.extensions

import android.content.Context
import network.ermis.client.ErmisClient
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.ui.ChatUI
import network.ermis.ui.components.R
import network.ermis.ui.utils.extension.getMembersStatusText
import network.ermis.ui.utils.extension.getPreviewMessage

internal fun Channel.isCurrentUserBanned(): Boolean {
    val currentUserId = ErmisClient.instance().clientState.user.value?.id ?: return false
    return members.any { it.user.id == currentUserId && it.banned }
}

/**
 * Returns a string describing the member status of the channel: either a member count for a group channel
 * or the last seen text for a direct one-to-one conversation with the current user.
 *
 * @param context The context to load string resources.
 * @param currentUser The currently logged in user.
 * @return The text that represent the member status of the channel.
 */
public fun Channel.getMembersStatusText(
    context: Context,
    currentUser: User? = ChatUI.currentUserProvider.getCurrentUser(),
): String {
    return getMembersStatusText(
        context = context,
        currentUser = currentUser,
        userOnlineResId = R.string.ermis_ui_user_status_online,
        userLastSeenJustNowResId = R.string.ermis_ui_user_status_last_seen_just_now,
        userLastSeenResId = R.string.ermis_ui_user_status_last_seen,
        memberCountResId = R.plurals.ermis_ui_message_list_header_member_count,
        memberCountWithOnlineResId = R.string.ermis_ui_message_list_header_member_count_online,
    )
}

/**
 * Returns channel's last regular or system message if exists.
 * Deleted and silent messages, as well as messages from shadow-banned users, are not taken into account.
 *
 * @return Last message from the channel or null if it doesn't exist.
 */
public fun Channel.getLastMessage(): Message? = getPreviewMessage(ChatUI.currentUserProvider.getCurrentUser())

internal fun Channel.readCount(message: Message): Int {
    val currentUser = ErmisClient.instance().clientState.user.value
    return read.filter { it.user.id != currentUser?.id }
        .mapNotNull { it.lastRead }
        .count { it.time >= message.getCreatedAtOrThrow().time }
}

internal const val EXTRA_DATA_MUTED: String = "mutedChannel"

internal val Channel.isMuted: Boolean
    get() = extraData[network.ermis.ui.utils.extensions.EXTRA_DATA_MUTED] as Boolean? ?: false
