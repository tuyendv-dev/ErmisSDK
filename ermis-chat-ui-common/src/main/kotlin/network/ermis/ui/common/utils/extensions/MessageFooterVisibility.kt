package network.ermis.ui.common.utils.extensions

import network.ermis.client.utils.extensions.getCreatedAtOrDefault
import network.ermis.client.utils.extensions.internal.NEVER
import network.ermis.client.utils.message.isDeleted
import network.ermis.core.models.Message
import network.ermis.ui.common.state.messages.list.MessageFooterVisibility

/**
 * Decides if we need to show the message footer (timestamp) below the message.
 *
 * @param message The current message for which we are checking whether we need to show the footer for.
 * @param isLastMessageInGroup Is the message at the bottom of the group.
 * @param nextMessage The message that comes after the current message.
 * Depending on it and [MessageFooterVisibility] we will show/hide the footer.
 *
 * @return If the message footer should be visible or not.
 */
public fun MessageFooterVisibility.shouldShowMessageFooter(
    message: Message,
    isLastMessageInGroup: Boolean,
    nextMessage: Message?,
): Boolean {
    if (nextMessage == null && this != MessageFooterVisibility.Never) return true
    if (message.updatedAt != null && message.updatedAt != message.createdAt) return true
    return when (this) {
        MessageFooterVisibility.Always -> true
        MessageFooterVisibility.LastInGroup -> isLastMessageInGroup
        MessageFooterVisibility.Never -> false
        is MessageFooterVisibility.WithTimeDifference -> isFooterVisibleWithTimeDifference(
            message = message,
            nextMessage = nextMessage,
            isLastMessageInGroup = isLastMessageInGroup,
            timeDifferenceMillis = timeDifferenceMillis,
        )
    }
}

/**
 * @param message The current [Message].
 * @param nextMessage The next [Message] in the list if there is one.
 * @param isLastMessageInGroup If the message is the last in group of messages.
 * @param timeDifferenceMillis The time difference between next and current message.
 *
 * @return Whether the footer should be visible or not.
 */
private fun isFooterVisibleWithTimeDifference(
    message: Message,
    nextMessage: Message?,
    isLastMessageInGroup: Boolean,
    timeDifferenceMillis: Long,
): Boolean {
    return when {
        isLastMessageInGroup -> true
        message.isDeleted() -> false
        message.user != nextMessage?.user ||
            nextMessage.isDeleted() ||
            (nextMessage.getCreatedAtOrDefault(NEVER).time) -
            (message.getCreatedAtOrDefault(NEVER).time) >
            timeDifferenceMillis -> true
        else -> false
    }
}
