
package network.ermis.ui.widgets.avatar

/**
 * Determines the shape of the avatar image in [UserAvatarView] and [ChannelAvatarView].
 */
public enum class AvatarShape(public val value: Int) {
    /**
     * Circle cropped image.
     */
    CIRCLE(0),

    /**
     * Round rect cropped image.
     */
    ROUND_RECT(1),
}
