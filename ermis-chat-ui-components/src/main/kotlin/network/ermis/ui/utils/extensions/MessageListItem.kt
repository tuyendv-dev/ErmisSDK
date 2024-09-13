
package network.ermis.ui.utils.extensions

import network.ermis.ui.common.state.messages.list.DateSeparatorItemState
import network.ermis.ui.common.state.messages.list.EmptyThreadPlaceholderItemState
import network.ermis.ui.common.state.messages.list.MessageItemState
import network.ermis.ui.common.state.messages.list.StartOfTheChannelItemState
import network.ermis.ui.common.state.messages.list.SystemMessageItemState
import network.ermis.ui.common.state.messages.list.ThreadDateSeparatorItemState
import network.ermis.ui.common.state.messages.list.TypingItemState
import network.ermis.ui.common.state.messages.list.UnreadSeparatorItemState
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.common.state.messages.list.MessageListItemState as MessageListItemCommon

/**
 * Converts [MessageListItemCommon] to [MessageListItem] to be shown inside [MessageListView].
 *
 * @return [MessageListItem] derived from [MessageListItemCommon].
 */
public fun MessageListItemCommon.toUiMessageListItem(): MessageListItem {
    return when (this) {
        is DateSeparatorItemState -> MessageListItem.DateSeparatorItem(date = date)
        is SystemMessageItemState -> MessageListItem.MessageItem(message = message)
        is ThreadDateSeparatorItemState -> MessageListItem.ThreadSeparatorItem(date = date, messageCount = replyCount)
        is TypingItemState -> MessageListItem.TypingItem(users = typingUsers)
        is MessageItemState -> MessageListItem.MessageItem(
            message = message,
            positions = groupPosition,
            isMine = isMine,
            messageReadBy = messageReadBy,
            isThreadMode = isInThread,
            isMessageRead = isMessageRead,
            showMessageFooter = showMessageFooter,
        )
        is EmptyThreadPlaceholderItemState -> MessageListItem.ThreadPlaceholderItem
        is UnreadSeparatorItemState -> MessageListItem.UnreadSeparatorItem(unreadCount = unreadCount)
        is StartOfTheChannelItemState -> MessageListItem.StartOfTheChannelItem(channel = channel)
    }
}
