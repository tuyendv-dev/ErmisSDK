
package network.ermis.ui.common.state.messages.list

import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelUserRead
import network.ermis.core.models.Message
import network.ermis.core.models.User
import java.util.Date

/**
 * Represents a list item inside a message list.
 */
public sealed class MessageListItemState

/**
 * Represents either regular or system message item inside a message list.
 */
public sealed class HasMessageListItemState : MessageListItemState() {

    /**
     * The [Message] to show in the list.
     */
    public abstract val message: Message
}

/**
 * Represents a message item inside the messages list.
 *
 * @param message The [Message] to show in the list.
 * @param parentMessageId The id of the parent [Message] if the message is inside a thread.
 * @param isMine Whether the message is sent by the current user or not.
 * @param isInThread Whether the message is inside a thread or not.
 * @param showMessageFooter Whether we need to show the message footer or not.
 * @param currentUser The currently logged in user.
 * @param groupPosition The [MessagePosition] of the item inside a group.
 * @param isMessageRead Whether the message has been read or not.
 * @param deletedMessageVisibility The [DeletedMessageVisibility] which determines the visibility of deleted messages in
 * the UI.
 * @param focusState The current [MessageFocusState] of the message, used to focus the message in the ui.
 * @param messageReadBy The list of [ChannelUserRead] for the message.
 */
public data class MessageItemState(
    public override val message: Message = Message(),
    public val parentMessageId: String? = null,
    public val isMine: Boolean = false,
    public val isInThread: Boolean = false,
    public val showMessageFooter: Boolean = false,
    public val currentUser: User? = null,
    public val groupPosition: List<MessagePosition> = listOf(MessagePosition.NONE),
    public val isMessageRead: Boolean = false,
    public val deletedMessageVisibility: DeletedMessageVisibility = DeletedMessageVisibility.ALWAYS_HIDDEN,
    public val focusState: MessageFocusState? = null,
    public val messageReadBy: List<ChannelUserRead> = emptyList(),
) : HasMessageListItemState()

/**
 * Represents a date separator inside the message list.
 *
 * @param date The date to show on the separator.
 */
public data class DateSeparatorItemState(
    val date: Date,
) : MessageListItemState()

/**
 * Represents a date separator inside thread messages list.
 *
 * @param date The date show on the separator.
 * @param replyCount Number of messages inside the thread.
 */
public data class ThreadDateSeparatorItemState(
    public val date: Date,
    public val replyCount: Int,
) : MessageListItemState()

/**
 * Represents a system message inside the message list.
 *
 * @param message The [Message] to show as the system message inside the list.
 */
public data class SystemMessageItemState(
    public override val message: Message,
) : HasMessageListItemState()

/**
 * Represents a typing indicator item inside a message list.
 *
 * @param typingUsers The list of the [User]s currently typing a message.
 */
public data class TypingItemState(
    public val typingUsers: List<User>,
) : MessageListItemState()

/**
 * Represents an empty thread placeholder item inside thread messages list.
 */
public data object EmptyThreadPlaceholderItemState : MessageListItemState()

/**
 * Represents an unread separator item inside a message list.
 *
 * @param unreadCount The number of unread messages.
 */
public data class UnreadSeparatorItemState(
    val unreadCount: Int,
) : MessageListItemState()

/**
 * Represents the start of the channel inside a message list.
 *
 * @param channel The [Channel] this message list belongs to.
 */
public data class StartOfTheChannelItemState(
    val channel: Channel,
) : MessageListItemState()
