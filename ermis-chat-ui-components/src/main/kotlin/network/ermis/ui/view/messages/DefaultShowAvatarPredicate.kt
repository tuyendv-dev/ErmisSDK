
package network.ermis.ui.view.messages

import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.utils.extensions.isBottomPosition

internal class DefaultShowAvatarPredicate : MessageListView.ShowAvatarPredicate {
    override fun shouldShow(messageItem: MessageListItem.MessageItem): Boolean {
        return messageItem.isTheirs && (messageItem.showMessageFooter || messageItem.isBottomPosition())
    }
}
