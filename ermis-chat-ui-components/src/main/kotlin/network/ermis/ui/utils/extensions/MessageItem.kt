
package network.ermis.ui.utils.extensions

import network.ermis.client.utils.message.isErrorOrFailed
import network.ermis.ui.common.state.messages.list.MessagePosition
import network.ermis.ui.view.messages.adapter.MessageListItem

public fun MessageListItem.MessageItem.isBottomPosition(): Boolean {
    return MessagePosition.BOTTOM in positions
}

public fun MessageListItem.MessageItem.isNotBottomPosition(): Boolean {
    return !isBottomPosition()
}

/**
 * @return If the mine message is the type of error or failed to send.
 */
internal fun MessageListItem.MessageItem.isErrorOrFailed(): Boolean = isMine && message.isErrorOrFailed()
