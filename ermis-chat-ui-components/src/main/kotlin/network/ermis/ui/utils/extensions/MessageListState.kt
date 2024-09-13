
package network.ermis.ui.utils.extensions

import network.ermis.ui.common.state.messages.list.MessageListState
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.model.MessageListItemWrapper

/**
 * Converts the common [MessageListState] to ui-components [MessageListItemWrapper].
 *
 * @param isInThread Whether the message list is currently in thread mode or not.
 *
 * @return [MessageListItemWrapper] derived from [MessageListState].
 */
public fun MessageListState.toMessageListItemWrapper(isInThread: Boolean): MessageListItemWrapper {
    var messagesList: List<MessageListItem> = messageItems.map { it.toUiMessageListItem() }

    if (isLoadingOlderMessages) messagesList = messagesList + listOf(MessageListItem.LoadingMoreIndicatorItem)
    if (isLoadingNewerMessages) messagesList = listOf(MessageListItem.LoadingMoreIndicatorItem) + messagesList

    return MessageListItemWrapper(
        items = messagesList,
        hasNewMessages = newMessageState != null,
        isTyping = messagesList.firstOrNull { it is MessageListItem.TypingItem } != null,
        areNewestMessagesLoaded = endOfNewMessagesReached,
        isThread = isInThread,
    )
}
