
package network.ermis.ui.view.messages.reactions.user

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import network.ermis.ui.components.R
import network.ermis.ui.common.state.messages.list.MessageOptionsUserReactionAlignment
import network.ermis.ui.view.messages.reactions.view.ViewReactionsViewStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDimensionOrNull
import network.ermis.ui.utils.extensions.toSingleReactionViewStyle
import network.ermis.ui.utils.extensions.use

/**
 * Style for [SingleReactionView].
 * Use this class together with [TransformStyle.singleReactionViewStyleTransformer] to change [SingleReactionView]
 * styles programmatically.
 *
 * @param bubbleBorderColorMine Reaction bubble border color for the current user.
 * @param bubbleBorderColorTheirs Reaction bubble border color for other users.
 * @param bubbleColorMine Reaction bubble color for the current user.
 * @param bubbleColorTheirs Reaction bubble color for other users.
 * @param bubbleBorderWidthMine Reaction bubble border width for the current user.
 * @param bubbleBorderWidthTheirs Reaction bubble border width for other users.
 * @param totalHeight The total height of the reaction bubble.
 * @param bubbleHeight Height of the reactions part of the bubble.
 * @param bubbleRadius The radius of the reactions part of the bubble.
 * @param largeTailBubbleCy The y axis position of the large tail bubble center point.
 * @param largeTailBubbleRadius The radius of the large tail bubble.
 * @param largeTailBubbleOffset The x axis offset of the large tail bubble center point.
 * @param smallTailBubbleCy The y axis position of the large tail bubble center point.
 * @param smallTailBubbleOffset The x axis offset of the small tail bubble center point
 * @param reactionOrientation The orientation of the bubble. By default is [MessageOptionsUserReactionAlignment.BY_USER]
 */
public data class SingleReactionViewStyle(
    @ColorInt public val bubbleBorderColorMine: Int,
    @ColorInt public val bubbleBorderColorTheirs: Int?,
    @ColorInt public val bubbleColorMine: Int,
    @ColorInt public val bubbleColorTheirs: Int,
    @Px public val bubbleBorderWidthMine: Float,
    @Px public val bubbleBorderWidthTheirs: Float?,
    @Px public val totalHeight: Int,
    @Px public val bubbleHeight: Int,
    @Px public val bubbleRadius: Int,
    @Px public val largeTailBubbleCy: Int,
    @Px public val largeTailBubbleRadius: Int,
    @Px public val largeTailBubbleOffset: Int,
    @Px public val smallTailBubbleCy: Int,
    @Px public val smallTailBubbleRadius: Int,
    @Px public val smallTailBubbleOffset: Int,
    public val reactionOrientation: Int,
) : ViewStyle {

    internal companion object {
        private val DEFAULT_BUBBLE_BORDER_COLOR_MINE = R.color.ui_grey_whisper
        private val DEFAULT_BUBBLE_COLOR_MINE = R.color.ui_grey_whisper
        private val DEFAULT_BUBBLE_COLOR_THEIRS = R.color.ui_grey_gainsboro
        private val DEFAULT_BUBBLE_BORDER_WIDTH_MINE = 1.dpToPx() * 1.5f

        operator fun invoke(context: Context, attrs: AttributeSet?): SingleReactionViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.SingleReactionView,
                R.attr.ermisUiSingleReactionViewStyle,
                0,
            ).use { a ->
                return if (a.indexCount != 0) {
                    Builder(a, context)
                        .bubbleBorderColorMine(
                            R.styleable.SingleReactionView_ermisUiSingleReactionBubbleBorderColorMine,
                        )
                        .bubbleBorderColorTheirs(
                            R.styleable.SingleReactionView_ermisUiSingleReactionBubbleBorderColorTheirs,
                        )
                        .bubbleBorderWidthMine(
                            R.styleable.SingleReactionView_ermisUiSingleReactionBubbleBorderWidthMine,
                        )
                        .bubbleBorderWidthTheirs(
                            R.styleable.SingleReactionView_ermisUiSingleReactionBubbleBorderWidthTheirs,
                        )
                        .bubbleColorMine(
                            R.styleable.SingleReactionView_ermisUiSingleReactionBubbleColorMine,
                        )
                        .bubbleColorTheirs(
                            R.styleable.SingleReactionView_ermisUiSingleReactionBubbleColorTheirs,
                        )
                        .messageOptionsUserReactionBubbleOrientation(
                            R.styleable.SingleReactionView_ermisUiSingleReactionBubbleOrientation,
                        )
                        .build()
                } else {
                    ViewReactionsViewStyle(context, attrs).toSingleReactionViewStyle()
                }
            }
        }

        class Builder(private val array: TypedArray, private val context: Context) {
            @ColorInt
            private var bubbleColorTheirs: Int = context.getColorCompat(DEFAULT_BUBBLE_COLOR_THEIRS)

            @ColorInt
            private var bubbleColorMine: Int = context.getColorCompat(DEFAULT_BUBBLE_COLOR_MINE)

            @ColorInt
            private var bubbleBorderColorMine: Int = context.getColorCompat(DEFAULT_BUBBLE_BORDER_COLOR_MINE)

            @ColorInt
            private var bubbleBorderColorTheirs: Int? = null

            @Px
            private var bubbleBorderWidthMine: Float = DEFAULT_BUBBLE_BORDER_WIDTH_MINE

            @Px
            private var bubbleBorderWidthTheirs: Float? = null
            private var reactionOrientation: Int =
                MessageOptionsUserReactionAlignment.BY_USER.value

            fun bubbleColorTheirs(@StyleableRes theirsBubbleColorAttribute: Int) = apply {
                bubbleColorTheirs =
                    array.getColor(theirsBubbleColorAttribute, context.getColorCompat(DEFAULT_BUBBLE_COLOR_THEIRS))
            }

            fun bubbleColorMine(@StyleableRes mineBubbleColorAttribute: Int) = apply {
                bubbleColorMine =
                    array.getColor(mineBubbleColorAttribute, context.getColorCompat(DEFAULT_BUBBLE_COLOR_MINE))
            }

            fun bubbleBorderColorMine(@StyleableRes bubbleBorderColorAttribute: Int) = apply {
                bubbleBorderColorMine =
                    array.getColor(bubbleBorderColorAttribute, context.getColorCompat(DEFAULT_BUBBLE_BORDER_COLOR_MINE))
            }

            fun bubbleBorderColorTheirs(@StyleableRes bubbleBorderColorAttribute: Int) = apply {
                bubbleBorderColorTheirs = array.getColorOrNull(bubbleBorderColorAttribute)
            }

            fun bubbleBorderWidthMine(@StyleableRes bubbleBorderWidthAttribute: Int) = apply {
                bubbleBorderWidthMine = array.getDimension(bubbleBorderWidthAttribute, DEFAULT_BUBBLE_BORDER_WIDTH_MINE)
            }

            fun bubbleBorderWidthTheirs(@StyleableRes bubbleBorderWidthAttribute: Int) = apply {
                bubbleBorderWidthTheirs = array.getDimensionOrNull(bubbleBorderWidthAttribute)
            }

            fun messageOptionsUserReactionBubbleOrientation(@StyleableRes bubbleOrientation: Int) = apply {
                this.reactionOrientation =
                    array.getInt(bubbleOrientation, MessageOptionsUserReactionAlignment.BY_USER.value)
            }

            fun build(): SingleReactionViewStyle {
                val totalHeight =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_total_height)
                val bubbleHeight =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_bubble_height)
                val bubbleRadius =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_bubble_radius)
                val largeTailBubbleCy =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_large_tail_bubble_cy)
                val largeTailBubbleRadius =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_large_tail_bubble_radius)
                val largeTailBubbleOffset =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_large_tail_bubble_offset)
                val smallTailBubbleCy =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_small_tail_bubble_cy)
                val smallTailBubbleRadius =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_small_tail_bubble_radius)
                val smallTailBubbleOffset =
                    context.getDimension(R.dimen.ermis_ui_single_reaction_view_small_tail_bubble_offset)

                return SingleReactionViewStyle(
                    bubbleBorderColorMine = bubbleBorderColorMine,
                    bubbleBorderColorTheirs = bubbleBorderColorTheirs,
                    bubbleBorderWidthMine = bubbleBorderWidthMine,
                    bubbleBorderWidthTheirs = bubbleBorderWidthTheirs,
                    bubbleColorMine = bubbleColorMine,
                    bubbleColorTheirs = bubbleColorTheirs,
                    totalHeight = totalHeight,
                    bubbleHeight = bubbleHeight,
                    bubbleRadius = bubbleRadius,
                    largeTailBubbleCy = largeTailBubbleCy,
                    largeTailBubbleRadius = largeTailBubbleRadius,
                    largeTailBubbleOffset = largeTailBubbleOffset,
                    smallTailBubbleCy = smallTailBubbleCy,
                    smallTailBubbleRadius = smallTailBubbleRadius,
                    smallTailBubbleOffset = smallTailBubbleOffset,
                    reactionOrientation = reactionOrientation,
                ).let(TransformStyle.singleReactionViewStyleTransformer::transform)
            }
        }
    }
}
