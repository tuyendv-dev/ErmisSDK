
package network.ermis.ui.view.messages.reactions.view

/**
 * Determines the orientation of the reaction bubble inside message options user reactions.
 *
 * @param value The int value of the enum used for xml attributes.
 */
public enum class MessageOptionsUserReactionOrientation(public val value: Int) {
    /**
     * Orients the reaction to the start of the user avatar.
     */
    START(0),

    /**
     * Orients the reaction to the end of user avatar.
     */
    END(1),

    /**
     * Orients the reaction to the end of user avatar if the reaction is from the current user otherwise orients it
     * to the start of the avatar.
     */
    BY_USER(2),

    /**
     * Orients the reaction to the start of user avatar if the reaction is from the current user otherwise orients it
     * to the end of the avatar.
     */
    BY_USER_INVERTED(3),
}

/**
 * @return The corresponding [MessageOptionsUserReactionOrientation] for the passed attribute in xml.
 */
public fun Int.getUserReactionOrientation(): MessageOptionsUserReactionOrientation {
    return MessageOptionsUserReactionOrientation.values().firstOrNull { it.value == this }
        ?: error("No such alignment")
}

/**
 * Determines if the user reaction should be oriented to start or end.
 *
 * @param isMine Is the reaction the current users reaction.
 *
 * @return If the reaction is oriented towards start or not.
 */
public fun MessageOptionsUserReactionOrientation.isOrientedTowardsStart(isMine: Boolean): Boolean {
    return this == MessageOptionsUserReactionOrientation.START ||
        (isMine && this == MessageOptionsUserReactionOrientation.BY_USER) ||
        (!isMine && this == MessageOptionsUserReactionOrientation.BY_USER_INVERTED)
}
