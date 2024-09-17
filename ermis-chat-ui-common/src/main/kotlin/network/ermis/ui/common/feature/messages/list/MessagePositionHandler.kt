package network.ermis.ui.common.feature.messages.list

import network.ermis.client.utils.message.isError
import network.ermis.client.utils.message.isSystem
import network.ermis.core.models.Message
import network.ermis.ui.common.state.messages.list.MessagePosition

/**
 * A handler to determine the position of a message inside a group.
 */
public fun interface MessagePositionHandler {
    /**
     * Determines the position of a message inside a group.
     *
     * @param previousMessage The previous [Message] in the list.
     * @param message The current [Message] in the list.
     * @param nextMessage The next [Message] in the list.
     * @param isAfterDateSeparator If a date separator was added before the current [Message].
     * @param isInThread If the current [Message] is in a thread.
     *
     * @return The position of the current message inside the group.
     */
    public fun handleMessagePosition(
        previousMessage: Message?,
        message: Message,
        nextMessage: Message?,
        isAfterDateSeparator: Boolean,
        isInThread: Boolean,
    ): List<MessagePosition>

    public companion object {
        /**
         * The default implementation of the [MessagePositionHandler] interface which can be taken
         * as a reference when implementing a custom one.
         *
         * @return The default implementation of [MessagePositionHandler].
         */
        @Suppress("ComplexCondition")
        public fun defaultHandler(): MessagePositionHandler {
            return MessagePositionHandler {
                    previousMessage: Message?,
                    message: Message,
                    nextMessage: Message?,
                    isAfterDateSeparator: Boolean,
                    _: Boolean,
                ->
                val previousUser = previousMessage?.user
                val user = message.user
                val nextUser = nextMessage?.user

                mutableListOf<MessagePosition>().apply {
                    if (previousMessage == null || previousUser != user || previousMessage.isSystem() ||
                        isAfterDateSeparator
                    ) {
                        add(MessagePosition.TOP)
                    }
                    if (previousMessage != null && nextMessage != null && previousUser == user && nextUser == user) {
                        add(MessagePosition.MIDDLE)
                    }
                    if (nextMessage == null || nextUser != user || nextMessage.isSystem() || nextMessage.isError()) {
                        add(MessagePosition.BOTTOM)
                    }
                    if (isEmpty()) add(MessagePosition.NONE)
                }
            }
        }
    }
}
