
package network.ermis.ui.common.state.messages.list

/**
 * Determines the alignment of the reaction icon inside user reactions.
 *
 * @param value The int value of the enum used for xml attributes.
 */
public enum class MessageOptionsUserReactionAlignment(public val value: Int) {
    /**
     * Aligns the reaction to the start of the user avatar.
     */
    START(0),

    /**
     * Aligns the reaction to the end of user avatar.
     */
    END(1),

    /**
     * Aligns the reaction to the end of user avatar if the reaction is from the current user otherwise aligns it
     * to the start of the avatar.
     */
    BY_USER(2),

    /**
     * Aligns the reaction to the start of user avatar if the reaction is from the current user otherwise aligns it
     * to the end of the avatar.
     */
    @Suppress("MagicNumber")
    BY_USER_INVERTED(3),
}

/**
 * @return The corresponding [MessageOptionsUserReactionAlignment] for the passed attribute in xml.
 */
public fun Int.getUserReactionAlignment(): MessageOptionsUserReactionAlignment {
    return MessageOptionsUserReactionAlignment.values().firstOrNull { it.value == this } ?: error("No such alignment")
}

/**
 * Determines if the user reaction should be aligned to start or end.
 *
 * @param isMine Is the reaction the current users reaction.
 *
 * @return If the reaction is aligned to the start or not.
 */
public fun MessageOptionsUserReactionAlignment.isStartAlignment(isMine: Boolean): Boolean {
    return this == MessageOptionsUserReactionAlignment.START ||
        (!isMine && this == MessageOptionsUserReactionAlignment.BY_USER) ||
        (isMine && this == MessageOptionsUserReactionAlignment.BY_USER_INVERTED)
}
