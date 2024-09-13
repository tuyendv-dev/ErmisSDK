package network.ermis.client.utils.extensions.internal

import network.ermis.client.ErmisClient
import network.ermis.client.utils.extensions.cidIsChannelDirect
import network.ermis.client.utils.message.isSystem
import network.ermis.core.models.Attachment
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.User
import java.util.Date

/** Updates collection of messages with more recent data of [users]. */
public fun Collection<Message>.updateUsers(users: Map<String, User>, channelName: String? = null): List<Message> = map { it.updateUsers(users, channelName) }

/**
 * Updates a message with more recent data of [users]. It updates author user, latestReactions, replyTo message,
 * mentionedUsers, threadParticipants and pinnedBy user of this instance.
 */
public fun Message.updateUsers(users: Map<String, User>, channelName: String? = null): Message =
    if (users().map(User::id).any(users::containsKey)) {
        copy(
            user = if (users.containsKey(user.id)) {
                users[user.id] ?: user
            } else {
                user
            },
            text = getSystemText(users, channelName),
            latestReactions = latestReactions.updateByUsers(users).toMutableList(),
            replyTo = replyTo?.updateUsers(users),
            mentionedUsers = mentionedUsers.updateUsers(users).toMutableList(),
            threadParticipants = threadParticipants.updateUsers(users).toMutableList(),
            pinnedBy = users[pinnedBy?.id ?: ""] ?: pinnedBy,
        )
    } else {
        this
    }

public fun Message.getSystemText(users: Map<String, User>, channelName: String? = null): String {
    if (this.isSystem()) {
        val textSplits = text.split(" ")
        if (textSplits.size < 2) return text
        val syntax = textSplits[0]
        val userId = textSplits[1]
        val isMine = ErmisClient.instance().getCurrentUser()?.id == userId
        val user = if (users.containsKey(userId)) users[userId]!! else null
        val isDriect = cid.cidIsChannelDirect()
        val textResult = when (syntax) {
            "1" -> {
                val lates = text.substring("$syntax $userId".length, text.length)
                "${user?.name ?: userId} changed the channel name to$lates"
            }
            "2" -> "${user?.name ?: userId} has changed the channel avatar"
            "3" -> "${user?.name ?: userId} changed the channel description"
            "4" -> "${user?.name ?: userId} has been removed from this channel"
            "5" -> {
                if (isMine) {
                    "You have been banned from interacting in this channel by Channel Admin"
                } else {
                    "${user?.name ?: userId} has been banned from interacting in this channel by Channel Admin"
                }
            }
            "6" -> {
                if (isMine) {
                    "You have been unbanned and now can interact in this channel"
                } else {
                    "${user?.name ?: userId} has been unbanned and now can interact in this channel"
                }
            }
            "7" -> {
                if (isMine) {
                    "You have been assigned as the moderator for $channelName"
                } else {
                    "${user?.name ?: userId} has been assigned as the moderator for this channel"
                }
            }
            "8" -> {
                if (isMine) {
                    "You have been removed as the moderator from $channelName"
                } else {
                    "${user?.name ?: userId} has been removed as the moderator from this channel"
                }
            }
            "9" -> ""
            "10" -> {
                if (isDriect) {
                    if (isMine) {
                        "You have joined this conversation"
                    } else {
                        "${user?.name ?: userId} have joined this conversation"
                    }
                } else {
                    if (isMine) {
                        "You have joined this channel"
                    } else {
                        "${user?.name ?: userId} have joined this channel"
                    }
                }
            }
            "11" -> "${user?.name ?: userId} has declined to join this channel"
            "12" -> {
                if (isMine) {
                    "You have left this channel"
                } else {
                    "${user?.name ?: userId} has left this channel"
                }
            }
            else -> ""
        }
        return textResult
    } else {
        return text
    }
}

/**
 * Fills [Message.mentionedUsersIds] based on [Message.text] and [Channel.members].
 *
 * It combines the users found in the input with pre-set [Message.mentionedUsersIds], in case someone
 * is manually added as a mention. Currently only searches through the channel members for possible mentions.
 *
 * @param channel The channel whose members we can check for the mention.
 */
public fun Message.populateMentions(channel: Channel): Message {
    if ('@' !in text) {
        return this
    }
    val text = text.lowercase()
    val mentions = mentionedUsersIds.toMutableSet() + channel.members.mapNotNullTo(mutableListOf()) { member ->
        if (text.contains("@${member.user.name.lowercase()}")) {
            member.user.id
        } else {
            null
        }
    }
    return copy(mentionedUsersIds = mentions.toList())
}

public val NEVER: Date = Date(0)

public fun Message.wasCreatedAfterOrAt(date: Date?): Boolean {
    return createdAt ?: createdLocallyAt ?: NEVER >= date
}

public fun Message.wasCreatedAfter(date: Date?): Boolean {
    return createdAt ?: createdLocallyAt ?: NEVER > date
}

public fun Message.wasCreatedBefore(date: Date?): Boolean {
    return createdAt ?: createdLocallyAt ?: NEVER < date
}

public fun Message.wasCreatedBeforeOrAt(date: Date?): Boolean {
    return createdAt ?: createdLocallyAt ?: NEVER <= date
}

public fun Message.users(): List<User> {
    return latestReactions.mapNotNull(Reaction::user) +
        user +
        (replyTo?.users().orEmpty()) +
        mentionedUsers +
        ownReactions.mapNotNull(Reaction::user) +
        threadParticipants +
        (pinnedBy?.let { listOf(it) } ?: emptyList())
}

/**
 * Function that parses if the unread count should be increased or not.
 *
 * @param currentUserId The id of the user that the unread count should be evaluated.
 * @param lastMessageAtDate The Date of the last message the SDK is aware of. This is normally the ChannelUserRead.lastMessageSeenDate.
 * @param isChannelMuted If the channel is muted for the current user or not.
 */
public fun Message.shouldIncrementUnreadCount(
    currentUserId: String,
    lastMessageAtDate: Date?,
    isChannelMuted: Boolean,
): Boolean {
    if (isChannelMuted) return false

    val isMoreRecent = if (createdAt != null && lastMessageAtDate != null) {
        createdAt!! > lastMessageAtDate
    } else {
        true
    }

    return user.id != currentUserId && !silent && !shadowed && isMoreRecent
}

public fun Message.hasPendingAttachments(): Boolean =
    attachments.any {
        it.uploadState is Attachment.UploadState.InProgress ||
            it.uploadState is Attachment.UploadState.Idle
    }

/**
 * Checks if the message mentions the [user].
 */
internal fun Message.containsUserMention(user: User): Boolean {
    return mentionedUsersIds.contains(user.id) || mentionedUsers.any { mentionedUser -> mentionedUser.id == user.id }
}
