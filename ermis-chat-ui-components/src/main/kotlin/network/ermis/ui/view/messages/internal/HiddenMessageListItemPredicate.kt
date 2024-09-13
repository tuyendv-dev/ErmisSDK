
package network.ermis.ui.view.messages.internal

import network.ermis.client.utils.message.isGiphyEphemeral
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.adapter.MessageListItem

internal object HiddenMessageListItemPredicate : MessageListView.MessageListItemPredicate {

    private val theirGiphyEphemeralMessagePredicate: (MessageListItem) -> Boolean = { item ->
        item is MessageListItem.MessageItem && item.message.isGiphyEphemeral() && item.isTheirs
    }

    override fun predicate(item: MessageListItem): Boolean {
        return theirGiphyEphemeralMessagePredicate(item).not()
    }
}
