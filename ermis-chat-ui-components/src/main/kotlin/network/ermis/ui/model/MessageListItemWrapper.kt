
package network.ermis.ui.model

import network.ermis.ui.view.messages.adapter.MessageListItem

/**
 * MessageListItemWrapper wraps a list of [MessageListItem] with a few extra fields.
 */
public data class MessageListItemWrapper(
    val items: List<MessageListItem> = listOf(),
    val hasNewMessages: Boolean = false,
    val isTyping: Boolean = false,
    val isThread: Boolean = false,
    val areNewestMessagesLoaded: Boolean = true,
) {

    override fun toString(): String {
        return stringify()
    }

    private fun stringify(): String {
        return "MessageListItemWrapper(items=${items.size}, first: ${items.firstOrNull()?.stringify()}" +
            ", hasNewMessages=$hasNewMessages, isTyping=$isTyping, isThread=$isThread" +
            ", areNewestMessagesLoaded=$areNewestMessagesLoaded)"
    }
}
