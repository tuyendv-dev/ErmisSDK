package network.ermis.ui.utils.extension

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import network.ermis.client.ErmisClient
import network.ermis.client.utils.extensions.getUsersExcludingCurrent
import network.ermis.client.utils.message.isRegular
import network.ermis.client.utils.message.isReply
import network.ermis.client.utils.message.isSystem
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.ui.utils.R

/**
 * Returns channel's last regular or system message if exists.
 * Deleted and silent messages, as well as messages from shadow-banned users, are not taken into account.
 *
 * @return Last message from the channel or null if it doesn't exist.
 */
public fun Channel.getPreviewMessage(currentUser: User?): Message? =
    if (isInsideSearch) {
        cachedLatestMessages
    } else {
        messages
    }.asSequence()
        .filter { it.createdAt != null || it.createdLocallyAt != null }
        .filter { it.deletedAt == null }
        .filter { !it.silent }
        .filter { it.user.id == currentUser?.id || !it.shadowed }
        .filter { it.isRegular() || it.isSystem() || it.isReply()}
        .maxByOrNull { requireNotNull(it.createdAt ?: it.createdLocallyAt) }

/**
 * Returns the channel name if exists, or the list of member names if the channel is distinct.
 *
 * @param context The context to load string resources.
 * @param currentUser The currently logged-in user.
 * @param fallback The resource identifier of a fallback string if the [Channel] object lacks
 * information to construct a valid display name string.
 * @param maxMembers The maximum number of members used to generate a name for a distinct channel.
 *
 * @return The display name of the channel.
 */
public fun Channel.getDisplayName(
    context: Context,
    currentUser: User? = ErmisClient.instance().clientState.user.value,
    @StringRes fallback: Int,
    maxMembers: Int = 2,
): String {
    return name.takeIf { it.isNotEmpty() }
        ?: nameFromMembers(context, currentUser, maxMembers)
        ?: context.getString(fallback)
}

private fun Channel.nameFromMembers(
    context: Context,
    currentUser: User?,
    maxMembers: Int,
): String? {
    val users = getUsersExcludingCurrent(currentUser)
    return when {
        users.isNotEmpty() -> {
            val usersCount = users.size
            val userNames = users
                .sortedBy(User::name)
                .take(maxMembers)
                .joinToString { it.name }
            when (usersCount <= maxMembers) {
                true -> userNames
                else -> {
                    context.getString(
                        R.string.ermis_ui_channel_list_untitled_channel_plus_more,
                        userNames,
                        usersCount - maxMembers,
                    )
                }
            }
        }

        // This channel has only the current user or only one user
        members.size == 1 -> members.first().user.name

        else -> null
    }
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
    currentUser: User?,
    @StringRes userOnlineResId: Int,
    @StringRes userLastSeenJustNowResId: Int,
    @StringRes userLastSeenResId: Int,
    @PluralsRes memberCountResId: Int,
    @StringRes memberCountWithOnlineResId: Int,
): String {
    return when {
        isOneToOne(currentUser) -> members.first { it.user.id != currentUser?.id }
            .user
            .getLastSeenText(
                context = context,
                userOnlineResId = userOnlineResId,
                userLastSeenJustNowResId = userLastSeenJustNowResId,
                userLastSeenResId = userLastSeenResId,
            )
        else -> {
            val memberCountString = context.resources.getQuantityString(
                memberCountResId,
                memberCount,
                memberCount,
            )

            return if (watcherCount > 0) {
                context.getString(
                    memberCountWithOnlineResId,
                    memberCountString,
                    watcherCount,
                )
            } else {
                memberCountString
            }
        }
    }
}

/**
 * Checks if the channel is a direct conversation between the current user and some
 * other user.
 *
 * A one-to-one chat is basically a corner case of a distinct channel with only 2 members.
 *
 * @param currentUser The currently logged in user.
 * @return True if the channel is a one-to-one conversation.
 */
private fun Channel.isOneToOne(currentUser: User?): Boolean {
    return cid.contains("messaging") &&
        members.size == 2 &&
        members.any { it.user.id == currentUser?.id }
}
