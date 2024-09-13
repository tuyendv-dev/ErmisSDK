package network.ermis.core.models

import androidx.compose.runtime.Immutable

/**
 * Describes the details of a message which was moderated.
 */
@Immutable
public data class MessageModerationDetails(
    val originalText: String,
    val action: MessageModerationAction,
    val errorMsg: String,
)

/**
 * The type of moderation performed to a message.
 */
@Immutable
public data class MessageModerationAction(
    public val rawValue: String,
) {
    public companion object {

        /**
         * A bounced message means it needs to be rephrased and sent again.
         */
        public val bounce: MessageModerationAction = MessageModerationAction(
            rawValue = "MESSAGE_RESPONSE_ACTION_BOUNCE",
        )

        /**
         * A flagged message means it was sent for review in the dashboard but the message was still published.
         */
        public val flag: MessageModerationAction = MessageModerationAction(
            rawValue = "MESSAGE_RESPONSE_ACTION_BOUNCE",
        )

        /**
         * A blocked message means it was not published and it was sent for review in the dashboard.
         */
        public val block: MessageModerationAction = MessageModerationAction(
            rawValue = "MESSAGE_RESPONSE_ACTION_BLOCK",
        )

        /**
         * A set of all the available [MessageModerationAction] values.
         */
        public val values: Set<MessageModerationAction> = setOf(bounce, flag, block)

        /**
         * Creates a [MessageModerationAction] from a raw value.
         */
        public fun fromRawValue(rawValue: String): MessageModerationAction = values.find {
            it.rawValue == rawValue
        } ?: MessageModerationAction(rawValue = rawValue)
    }
}
