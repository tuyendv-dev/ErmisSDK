
package network.ermis.ui.view.messages.adapter.viewholder.decorator

import network.ermis.ui.view.messages.adapter.BaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.MessageListItem

/**
 * Decorator interface used to decorate the view holder.
 */
public interface Decorator {

    /**
     * The type of the decorator.
     */
    public val type: Type

    /**
     * Decorates the view holder.
     */
    public fun <T : MessageListItem> decorate(
        viewHolder: BaseMessageItemViewHolder<T>,
        data: T,
    )

    /**
     * The type of the decorator.
     */
    public interface Type {

        /**
         * The SDK built-in decorators.
         */
        public enum class BuiltIn : Type {

            /**
             * Decorates the avatar of the message.
             */
            AVATAR,

            /**
             * Decorates the background of the message.
             */
            BACKGROUND,

            /**
             * Decorates the failed indicator.
             */
            FAILED_INDICATOR,

            /**
             * Decorates the footer of the message.
             */
            FOOTNOTE,

            /**
             * Decorates the gap between messages.
             */
            GAP,

            /**
             * Defines max possible width for the message container.
             */
            MAX_POSSIBLE_WIDTH,

            /**
             * Defines the margins of the message container.
             */
            MESSAGE_CONTAINER_MARGIN,

            /**
             * Decorates the pinned messages.
             */
            PIN_INDICATOR,

            /**
             * Decorates the reactions attached to the message.
             */
            REACTIONS,

            /**
             * Decorates the replies to the message.
             */
            REPLY,

            /**
             * Decorates the the message text.
             */
            TEXT,
        }
    }
}
