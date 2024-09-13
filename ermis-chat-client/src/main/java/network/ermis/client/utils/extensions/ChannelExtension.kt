package network.ermis.client.utils.extensions

import network.ermis.client.ErmisClient
import network.ermis.client.utils.extensions.internal.containsUserMention
import network.ermis.client.utils.extensions.internal.wasCreatedAfter
import network.ermis.core.models.Channel
import network.ermis.core.models.Member
import network.ermis.core.models.User

public fun Channel.isAnonymousChannel(): Boolean = type == "messaging"

/**
 * Checks if [Channel] is muted for [user].
 *
 * @return True if the channel is muted for [user].
 */
public fun Channel.isMutedFor(user: User): Boolean = user.channelMutes.any { mute -> mute.channel.cid == cid }

/**
 * Returns a list of users that are members of the channel excluding the currently
 * logged in user.
 *
 * @param currentUser The currently logged in user.
 * @return The list of users in the channel without the current user.
 */
public fun Channel.getUsersExcludingCurrent(
    currentUser: User? = ErmisClient.instance().getCurrentUser(),
): List<User> = getMembersExcludingCurrent(currentUser).map { it.user }

/**
 * Returns a list of members of the channel excluding the currently logged in user.
 *
 * @param currentUser The currently logged in user.
 * @return The list of members in the channel without the current user.
 */
public fun Channel.getMembersExcludingCurrent(
    currentUser: User? = ErmisClient.instance().getCurrentUser(),
): List<Member> =
    members.filter { it.user.id != currentUser?.id }

/**
 * Counts messages in which [user] is mentioned.
 * The method relies on the [Channel.messages] list and doesn't do any API call.
 * Therefore, the count might be not reliable as it relies on the local data.
 *
 * @param user The User object for which unread mentions are counted.
 *
 * @return Number of messages containing unread user mention.
 */
public fun Channel.countUnreadMentionsForUser(user: User): Int {
    val lastMessageSeenDate = read.firstOrNull { read -> read.user.id == user.id }?.lastRead

    val messagesToCheck = if (lastMessageSeenDate == null) {
        messages
    } else {
        messages.filter { message -> message.wasCreatedAfter(lastMessageSeenDate) }
    }

    return messagesToCheck.count { message -> message.containsUserMention(user) }
}

/**
 * Returns the number of unread messages in the channel for the current user.
 *
 * @return The number of unread messages in the channel for the current user.
 */
public val Channel.currentUserUnreadCount: Int
    get() = ErmisClient.instance().getCurrentUser()?.let { currentUser ->
        read.firstOrNull { it.user.id == currentUser.id }?.unreadMessages
    } ?: 0

public val Channel.hasUnread: Boolean
    get() = currentUserUnreadCount > 0

public fun Channel.syncUnreadCountWithReads(): Channel =
    copy(unreadCount = currentUserUnreadCount)
