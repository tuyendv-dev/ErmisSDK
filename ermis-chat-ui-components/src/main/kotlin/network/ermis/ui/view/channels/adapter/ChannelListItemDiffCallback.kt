
package network.ermis.ui.view.channels.adapter

import androidx.recyclerview.widget.DiffUtil
import network.ermis.client.utils.extensions.currentUserUnreadCount
import network.ermis.client.utils.extensions.getMembersExcludingCurrent
import io.getstream.chat.android.ui.common.extensions.internal.cast
import io.getstream.chat.android.ui.common.extensions.internal.safeCast
import network.ermis.ui.utils.extensions.getLastMessage

internal object ChannelListItemDiffCallback : DiffUtil.ItemCallback<ChannelListItem>() {
    override fun areItemsTheSame(oldItem: ChannelListItem, newItem: ChannelListItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }

        return when (oldItem) {
            is ChannelListItem.ChannelItem -> {
                oldItem.channel.cid == newItem.safeCast<ChannelListItem.ChannelItem>()?.channel?.cid
            }

            else -> true
        }
    }

    override fun areContentsTheSame(oldItem: ChannelListItem, newItem: ChannelListItem): Boolean {
        // this is only called if areItemsTheSame returns true, so they must be the same class
        return when (oldItem) {
            is ChannelListItem.ChannelItem -> {
                oldItem
                    .diff(newItem.cast())
                    .hasDifference()
                    .not()
            }

            else -> true
        }
    }

    override fun getChangePayload(oldItem: ChannelListItem, newItem: ChannelListItem): Any {
        // only called if their contents aren't the same, so they must be channel items and not loading items
        return oldItem
            .cast<ChannelListItem.ChannelItem>()
            .diff(newItem.cast())
    }
    private fun ChannelListItem.ChannelItem.diff(other: ChannelListItem.ChannelItem): ChannelListPayloadDiff {
        val usersChanged = channel.getMembersExcludingCurrent() != other.channel.getMembersExcludingCurrent()
        return ChannelListPayloadDiff(
            nameChanged = channel.name != other.channel.name,
            avatarViewChanged = usersChanged,
            usersChanged = usersChanged,
            readStateChanged = channel.read != other.channel.read,
            lastMessageChanged = channel.getLastMessage() != other.channel.getLastMessage(),
            unreadCountChanged = channel.currentUserUnreadCount != other.channel.currentUserUnreadCount,
            extraDataChanged = channel.extraData != other.channel.extraData,
            typingUsersChanged = typingUsers != other.typingUsers,
        )
    }
}
