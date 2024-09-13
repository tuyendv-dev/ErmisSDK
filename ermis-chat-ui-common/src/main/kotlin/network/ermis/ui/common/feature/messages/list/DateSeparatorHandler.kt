package network.ermis.ui.common.feature.messages.list

import network.ermis.client.utils.extensions.getCreatedAtOrDefault
import network.ermis.client.utils.extensions.getCreatedAtOrNull
import network.ermis.client.utils.extensions.internal.NEVER
import network.ermis.core.models.Message

/**
 * A SAM designed to evaluate if a date separator should be added between messages.
 */
public fun interface DateSeparatorHandler {

    /**
     * Determines whether we should add the date separator or not.
     *
     * @param previousMessage The [Message] before the one we are currently evaluating.
     * @param message The [Message] before which we want to add a date separator or not.
     *
     * @return Whether to add the date separator or not.
     */
    public fun shouldAddDateSeparator(previousMessage: Message?, message: Message): Boolean

    public companion object {

        /**
         * @param separatorTimeMillis Time difference between two message after which we add the date separator.
         *
         * @return The default normal list date separator handler.
         */
        public fun getDefaultDateSeparatorHandler(
            separatorTimeMillis: Long = DateSeparatorDefaultHourThreshold,
        ): DateSeparatorHandler = DateSeparatorHandler { previousMessage, message ->
            if (previousMessage == null) {
                true
            } else {
                shouldAddDateSeparator(previousMessage, message, separatorTimeMillis)
            }
        }

        /**
         * @param separatorTimeMillis Time difference between two message after which we add the date separator.
         *
         * @return The default thread date separator handler.
         */
        public fun getDefaultThreadDateSeparatorHandler(
            separatorTimeMillis: Long = DateSeparatorDefaultHourThreshold,
        ): DateSeparatorHandler = DateSeparatorHandler { previousMessage, message ->
            if (previousMessage == null) {
                false
            } else {
                shouldAddDateSeparator(previousMessage, message, separatorTimeMillis)
            }
        }

        /**
         * @param previousMessage The [Message] before the one we are currently evaluating.
         * @param message The [Message] before which we want to add a date separator or not.
         *
         * @return Whether to add the date separator or not depending on the time difference.
         */
        private fun shouldAddDateSeparator(
            previousMessage: Message?,
            message: Message,
            separatorTimeMillis: Long,
        ): Boolean {
            return (
                message.getCreatedAtOrDefault(NEVER).time - (
                    previousMessage?.getCreatedAtOrNull()?.time
                        ?: NEVER.time
                    )
                ) >
                separatorTimeMillis
        }

        /**
         * The default threshold for showing date separators. If the message difference in millis is equal to this
         * number, then we show a separator, if it's enabled in the list.
         */
        private const val DateSeparatorDefaultHourThreshold: Long = 4 * 60 * 60 * 1000
    }
}