package network.ermis.client.utils.extensions.internal

import network.ermis.client.utils.extensions.syncUnreadCountWithReads
import network.ermis.client.query.pagination.AnyChannelPaginationRequest
import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelUserRead
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.User
import io.getstream.log.StreamLog
import io.getstream.log.taggedLogger
import java.util.Date

private const val TAG = "Chat:ChannelTools"

/**
 * Returns all users including watchers of a channel that are associated with it.
 */
public fun Channel.users(): List<User> {
    return members.map(Member::user) +
        read.map(ChannelUserRead::user) +
        createdBy +
        messages.flatMap { it.users() } +
        watchers
}

public val Channel.lastMessage: Message?
    get() = messages.maxByOrNull { it.createdAt ?: it.createdLocallyAt ?: Date(0) }

public fun Channel.updateLastMessage(
    receivedEventDate: Date,
    message: Message,
    currentUserId: String,
): Channel {
    val createdAt = message.createdAt ?: message.createdLocallyAt
    checkNotNull(createdAt) { "created at cant be null, be sure to set message.createdAt" }

    val newMessages = (
        messages
            .associateBy { it.id } + (message.id to message)
        )
        .values
        .sortedBy { it.createdAt ?: it.createdLocallyAt }

    val newReads = read.map { read ->
        read.takeUnless { it.user.id == currentUserId }
            ?: read.copy(
                lastReceivedEventDate = receivedEventDate,
                unreadMessages = read.let {
                    val hasNewUnreadMessage = receivedEventDate.after(it.lastReceivedEventDate) &&
                        newMessages.size > messages.size &&
                        newMessages.last().id == message.id
                    if (hasNewUnreadMessage) it.unreadMessages.inc() else it.unreadMessages
                },
            )
    }
    return this.copy(
        lastMessageAt = newMessages.last().let { it.createdAt ?: it.createdLocallyAt },
        messages = newMessages,
        read = newReads,
    ).syncUnreadCountWithReads()
}

/**
 * Removes member from the [Channel.members] and aligns [Channel.memberCount].
 *
 * @param currentUserId User id of the currently logged in user.
 * @param memberUserId User id of the removed member.
 */
public fun Channel.removeMember(memberUserId: String?): Channel = copy(
    members = members.filterNot { it.user.id == memberUserId },
    memberCount = memberCount - (1.takeIf { members.any { it.user.id == memberUserId } } ?: 0),
)

/**
 * Adds member to the [Channel.members] and aligns [Channel.memberCount].
 *
 * @param member Added member.
 */
public fun Channel.addMember(member: Member): Channel {
    val memberExists = members.any { it.getUserId() == member.getUserId() }
    return copy(
        members = members + listOfNotNull(member.takeUnless { memberExists }),
        memberCount = memberCount + (1.takeUnless { memberExists } ?: 0),

    )
}

/**
 * Updates [Channel] member.
 *
 * @param member Updated member.
 */
public fun Channel.updateMember(member: Member): Channel = copy(
    members = members.map { iterableMember ->
        iterableMember.takeUnless { it.getUserId() == member.getUserId() } ?: member
    },
)

/**
 * Updates [Member.banned] property inside the [Channel.members].
 *
 * @param memberUserId Updated member user id.
 * @param banned Shows whether a user is banned or not in this channel.
 * @param shadow Shows whether a user is shadow banned or not in this channel.
 */
public fun Channel.updateMemberBanned(
    memberUserId: String,
    banned: Boolean,
    shadow: Boolean,
): Channel = copy(
    members = members.map { member ->
        member.takeUnless { it.user.id == memberUserId }
            ?: member.copy(banned = banned, shadowBanned = shadow)
    },
)

/**
 * Sets [Channel.membership] to [member] if [currentUserId] equals to [member.getUserId()].
 *
 * @param currentUserId User id of the currently logged in user.
 * @param member Added member.
 */
public fun Channel.addMembership(currentUserId: String, member: Member): Channel = copy(
    membership = member.takeIf { it.getUserId() == currentUserId } ?: membership,
)

/**
 * Sets [Channel.membership] to [member] if [member.user.id] equals to [Channel.membership.user.id].
 *
 * @param member Updated member.
 */
public fun Channel.updateMembership(member: Member): Channel = copy(
    membership = member
        .takeIf { it.getUserId() == membership?.getUserId() }
        ?: membership.also {
            StreamLog.w(TAG) {
                "[updateMembership] rejected; memberUserId(${member.getUserId()}) != " +
                    "membershipUserId(${membership?.getUserId()})"
            }
        },
)

/**
 * Sets [Channel.membership.banned] to [banned] if [memberUserId] equals to [membership.user.id].
 *
 * @param memberUserId Member user id.
 * @param banned Shows whether a user is banned or not in this channel.
 */
public fun Channel.updateMembershipBanned(memberUserId: String, banned: Boolean): Channel = copy(
    membership = membership
        ?.takeIf { it.getUserId() == memberUserId }
        ?.copy(banned = banned)
        ?: membership,
)

/**
 * Sets [Channel.membership] to null if [currentUserId] equals to [membership.user.id].
 *
 * @param currentUserId User id of the currently logged in user.
 */
public fun Channel.removeMembership(currentUserId: String?): Channel =
    copy(membership = membership.takeUnless { it?.user?.id == currentUserId })

public fun Channel.updateReads(newRead: ChannelUserRead): Channel {
    val oldRead = read.firstOrNull { it.user.id == newRead.user.id }
    return copy(
        read = if (oldRead != null) {
            read - oldRead + newRead
        } else {
            read + newRead
        },
    ).syncUnreadCountWithReads()
}

public fun Collection<Channel>.applyPagination(pagination: AnyChannelPaginationRequest): List<Channel> {
    val logger by taggedLogger("Chat:ChannelSort")

    return asSequence()
        .also { channelSequence ->
            logger.d {
                val ids = channelSequence.joinToString { channel -> channel.id }
                "Sorting channels: $ids"
            }
        }
        .sortedWith(pagination.sort.comparator)
        .also { channelSequence ->
            logger.d {
                val ids = channelSequence.joinToString { channel -> channel.id }
                "Sort for channels result: $ids"
            }
        }
        .drop(pagination.channelOffset)
        .take(pagination.channelLimit)
        .toList()
}

/** Updates collection of channels with more recent data of [users]. */
public fun Collection<Channel>.updateUsers(users: Map<String, User>): List<Channel> = map { it.updateUsers(users) }

/**
 * Updates a channel with more recent data of [users]. It updates messages, members, watchers, createdBy and
 * pinnedMessages of channel instance.
 */
internal fun Channel.updateUsers(users: Map<String, User>): Channel {
    return if (users().map(User::id).any(users::containsKey)) {
        copy(
            messages = messages.updateUsers(users),
            members = members.updateUsers(users).toList(),
            watchers = watchers.updateUsers(users),
            createdBy = users[createdBy.id] ?: createdBy,
            pinnedMessages = pinnedMessages.updateUsers(users),
        )
    } else {
        this
    }
}
