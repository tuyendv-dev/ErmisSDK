@file:JvmName("MessageUtils")

package network.ermis.client.utils.message

import network.ermis.core.utils.after
import network.ermis.core.models.AttachmentType
import network.ermis.core.models.Message
import network.ermis.core.models.MessageType
import network.ermis.core.models.SyncStatus

private const val ITEM_COUNT_OF_TWO: Int = 2

/**
 * Peeks the latest message from the sorted [List] of messages.
 */
public fun List<Message>.latestOrNull(): Message? = when (size >= ITEM_COUNT_OF_TWO) {
    true -> {
        val first = first()
        val last = last()
        when (last.createdAfter(first)) {
            true -> last
            else -> first
        }
    }

    else -> lastOrNull()
}

/**
 * Tests if [this] message was created after [that] message.
 */
public fun Message.createdAfter(that: Message): Boolean {
    val thisDate = this.createdAt ?: this.createdLocallyAt
    val thatDate = that.createdAt ?: that.createdLocallyAt
    return thisDate after thatDate
}

/**
 * @return If the current message failed to send.
 */
public fun Message.isFailed(): Boolean = this.syncStatus == SyncStatus.FAILED_PERMANENTLY

/**
 * @return If the message type is error or failed to send.
 */
public fun Message.isErrorOrFailed(): Boolean = isError() || isFailed()

/**
 * @return If the message is deleted.
 */
public fun Message.isDeleted(): Boolean = deletedAt != null

/**
 * @return If the message type is regular.
 */
public fun Message.isRegular(): Boolean = type == MessageType.REGULAR

/**
 * @return If the message type is ephemeral.
 */
public fun Message.isEphemeral(): Boolean = type == MessageType.EPHEMERAL

/**
 * @return If the message type is system.
 */
public fun Message.isSystem(): Boolean = type == MessageType.SYSTEM

/**
 * @return If the message type is error.
 */
public fun Message.isError(): Boolean = type == MessageType.ERROR

/**
 * @return If the message is related to a Giphy slash command.
 */
public fun Message.isGiphy(): Boolean = command == AttachmentType.GIPHY

/**
 * @return If the message is a temporary message to select a gif.
 */
public fun Message.isGiphyEphemeral(): Boolean = isGiphy() && isEphemeral()

/**
 * @return If the message is a start of a thread.
 */
public fun Message.isThreadStart(): Boolean = threadParticipants.isNotEmpty()

/**
 * @return If the message is a thread reply.
 */
public fun Message.isThreadReply(): Boolean = !parentId.isNullOrEmpty()

/**
 * @return If the message contains quoted message.
 */
public fun Message.isReply(): Boolean = replyTo != null

/**
 * @return If the message belongs to the current user.
 */
public fun Message.isMine(currentUserId: String?): Boolean = currentUserId == user.id

/**
 * @return If the message has moderation bounce action.
 */
public fun Message.isModerationBounce(): Boolean = moderationDetails?.action == network.ermis.core.models.MessageModerationAction.bounce

/**
 * @return If the message has moderation block action.
 */
public fun Message.isModerationBlock(): Boolean = moderationDetails?.action == network.ermis.core.models.MessageModerationAction.block

/**
 * @return If the message has moderation flag action.
 */
public fun Message.isModerationFlag(): Boolean = moderationDetails?.action == network.ermis.core.models.MessageModerationAction.flag

/**
 * @return if the message failed at moderation or not.
 */
public fun Message.isModerationError(currentUserId: String?): Boolean = isMine(currentUserId) &&
    (isError() && isModerationBounce())

public fun Message.isEdited(): Boolean = (updatedAt != null && updatedAt != createdAt)