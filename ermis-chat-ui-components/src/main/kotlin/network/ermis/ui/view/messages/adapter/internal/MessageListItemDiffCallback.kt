
package network.ermis.ui.view.messages.adapter.internal

import androidx.recyclerview.widget.DiffUtil
import network.ermis.core.models.User
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff

internal object MessageListItemDiffCallback : DiffUtil.ItemCallback<MessageListItem>() {
    override fun areItemsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
        return oldItem.getStableId() == newItem.getStableId()
    }

    override fun areContentsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
        return when (oldItem) {
            is MessageListItem.MessageItem -> {
                newItem as MessageListItem.MessageItem
                val oldMessage = oldItem.message
                val newMessage = newItem.message

                oldMessage.text == newMessage.text &&
                    oldMessage.replyTo?.text == newMessage.replyTo?.text &&
                    oldMessage.reactionScores == newMessage.reactionScores &&
                    oldMessage.reactionCounts == newMessage.reactionCounts &&
                    oldMessage.attachments == newMessage.attachments &&
                    oldMessage.replyCount == newMessage.replyCount &&
                    oldMessage.syncStatus == newMessage.syncStatus &&
                    oldMessage.deletedAt == newMessage.deletedAt &&
                    oldItem.positions == newItem.positions &&
                    oldItem.isMessageRead == newItem.isMessageRead &&
                    oldItem.isThreadMode == newItem.isThreadMode &&
                    oldMessage.extraData == newMessage.extraData &&
                    oldMessage.pinned == newMessage.pinned &&
                    oldMessage.user == newMessage.user &&
                    oldMessage.mentionedUsers == newMessage.mentionedUsers &&
                    oldItem.showMessageFooter == newItem.showMessageFooter
            }
            is MessageListItem.DateSeparatorItem -> oldItem.date == (newItem as? MessageListItem.DateSeparatorItem)?.date
            is MessageListItem.ThreadSeparatorItem -> oldItem == (newItem as? MessageListItem.ThreadSeparatorItem)
            is MessageListItem.LoadingMoreIndicatorItem -> true
            is MessageListItem.TypingItem -> oldItem.users.map(User::id) == ((newItem) as? MessageListItem.TypingItem)?.users?.map(
                User::id,
            )
            is MessageListItem.ThreadPlaceholderItem -> true
            is MessageListItem.UnreadSeparatorItem ->
                oldItem.unreadCount == (newItem as? MessageListItem.UnreadSeparatorItem)?.unreadCount

            is MessageListItem.StartOfTheChannelItem ->
                oldItem.channel == (newItem as? MessageListItem.StartOfTheChannelItem)?.channel
        }
    }

    override fun getChangePayload(oldItem: MessageListItem, newItem: MessageListItem): Any? {
        return if (oldItem is MessageListItem.MessageItem) {
            newItem as MessageListItem.MessageItem
            val oldMessage = oldItem.message
            val newMessage = newItem.message

            MessageListItemPayloadDiff(
                text = oldMessage.text != newMessage.text,
                replyText = oldMessage.replyTo?.text != newMessage.replyTo?.text,
                reactions = (oldMessage.reactionCounts != newMessage.reactionCounts) || (oldMessage.reactionScores != newMessage.reactionScores),
                attachments = oldMessage.attachments != newMessage.attachments,
                replies = oldMessage.replyCount != newMessage.replyCount,
                syncStatus = oldMessage.syncStatus != newMessage.syncStatus,
                deleted = oldMessage.deletedAt != newMessage.deletedAt,
                positions = oldItem.positions != newItem.positions,
                pinned = oldMessage.pinned != newMessage.pinned,
                user = oldMessage.user != newMessage.user,
                mentions = oldMessage.mentionedUsers != newMessage.mentionedUsers,
                footer = oldItem.showMessageFooter != newItem.showMessageFooter,
            )
        } else {
            null
        }
    }
}
