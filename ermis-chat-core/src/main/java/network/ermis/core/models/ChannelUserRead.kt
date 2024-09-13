package network.ermis.core.models

import androidx.compose.runtime.Immutable
import java.util.Date

/**
 * Information about how many messages are unread in the channel by a given user.
 *
 * @property user The user which has read some of the messages and may have some unread messages.
 * @property lastReceivedEventDate The time of the event that updated this [ChannelUserRead] object.
 * @property lastRead The time of the last read message.
 * @property unreadMessages How many messages are unread.
 */
@Immutable
public data class ChannelUserRead(
    override val user: User,
    val lastReceivedEventDate: Date,
    val unreadMessages: Int,
    val lastRead: Date,
    val lastReadMessageId: String?,
) : UserEntity
