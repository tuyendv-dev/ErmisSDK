package network.ermis.ui.helper

import android.content.Context
import android.graphics.drawable.Drawable
import network.ermis.ui.ChatUI
import network.ermis.ui.helper.SupportedReactions.DefaultReactionTypes.HAHA
import network.ermis.ui.helper.SupportedReactions.DefaultReactionTypes.LOVE
import network.ermis.ui.helper.SupportedReactions.DefaultReactionTypes.SAD
import network.ermis.ui.helper.SupportedReactions.DefaultReactionTypes.LIKE
import network.ermis.ui.helper.SupportedReactions.DefaultReactionTypes.FIRE
import network.ermis.ui.helper.SupportedReactions.ReactionDrawable

/**
 * Class allowing to define set of supported reactions. You can customize reactions by providing your own
 * implementation of this class to [ChatUI.supportedReactions].
 *
 * @param context The context to load drawable resources.
 * @param reactions Map<String, ReactionDrawable> instance. Map with keys corresponding to reaction type, and
 * value corresponding to [ReactionDrawable] object. By default it's initialized with standard reactions.
 */
public class SupportedReactions(
    context: Context,
    public val reactions: Map<String, String> = mapOf(
        HAHA to "😂",
        LIKE to "👍",
        LOVE to "❤️",
        SAD to "😔",
        FIRE to "🔥",
    ),
) {
    public val types: List<String> = reactions.keys.toList()

    internal fun isReactionTypeSupported(type: String): Boolean {
        return reactions.keys.contains(type)
    }

    public fun getReactionDrawable(type: String): String? {
        return reactions[type]
    }

    /**
     * Contains drawables for normal and selected reaction states.
     *
     * @param inactiveDrawable The drawable for the normal icon.
     * @param activeDrawable The drawable for the selected state icon.
     */
    public class ReactionDrawable(
        private val inactiveDrawable: Drawable,
        private val activeDrawable: Drawable,
    ) {
        public fun getDrawable(isActive: Boolean): Drawable = if (isActive) {
            activeDrawable
        } else {
            inactiveDrawable
        }
    }

    /**
     * The reaction types we support by default.
     */
    public object DefaultReactionTypes {
        public const val HAHA: String = "haha"
        public const val LIKE: String = "like"
        public const val LOVE: String = "love"
        public const val SAD: String = "sad"
        public const val FIRE: String = "fire"
    }

}
