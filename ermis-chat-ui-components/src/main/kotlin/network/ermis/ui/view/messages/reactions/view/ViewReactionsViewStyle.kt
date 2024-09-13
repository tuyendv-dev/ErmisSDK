
package network.ermis.ui.view.messages.reactions.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import network.ermis.ui.components.R
import network.ermis.ui.common.state.messages.list.MessageOptionsUserReactionAlignment
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDimensionOrNull
import network.ermis.ui.utils.extensions.use

/**
 * Style for [ViewReactionsView].
 * Use this class together with [TransformStyle.viewReactionsStyleTransformer] to change [ViewReactionsView]
 * styles programmatically.
 *
 * @param bubbleBorderColorMine Reaction bubble border color for the current user.
 * @param bubbleBorderColorTheirs Reaction bubble border color for other users.
 * @param bubbleColorMine Reaction bubble color for the current user.
 * @param bubbleColorTheirs Reaction bubble color for other users.
 * @param bubbleBorderWidthMine Reaction bubble border width for the current user.
 * @param bubbleBorderWidthTheirs Reaction bubble border width for other users.
 * @param totalHeight The total height of the reaction bubble.
 * @param horizontalPadding The horizontal padding to be applied to the start and end of the bubble.
 * @param itemSize The size of the reaction item.
 * @param bubbleHeight Height of the reactions part of the bubble.
 * @param bubbleRadius The radius of the reactions part of the bubble.
 * @param largeTailBubbleCy The y axis position of the large tail bubble center point.
 * @param largeTailBubbleRadius The radius of the large tail bubble.
 * @param largeTailBubbleOffset The x axis offset of the large tail bubble center point.
 * @param smallTailBubbleCy The y axis position of the small tail bubble center point.
 * @param smallTailBubbleOffset The x axis offset of the small tail bubble center point.
 * @param verticalPadding The vertical padding to be applied to top and bottom of the view.
 * @param messageOptionsUserReactionOrientation The orientation of the bubble.
 * By default is [MessageOptionsUserReactionOrientation.BY_USER]
 */
public data class ViewReactionsViewStyle(
    @ColorInt public val bubbleBorderColorMine: Int,
    @ColorInt public val bubbleBorderColorTheirs: Int?,
    @ColorInt public val bubbleColorMine: Int,
    @ColorInt public val bubbleColorTheirs: Int,
    @Px public val bubbleBorderWidthMine: Float,
    @Px public val bubbleBorderWidthTheirs: Float?,
    @Px public val totalHeight: Int,
    @Px public val horizontalPadding: Int,
    @Px public val itemSize: Int,
    @Px public val bubbleHeight: Int,
    @Px public val bubbleRadius: Int,
    @Px public val largeTailBubbleCy: Int,
    @Px public val largeTailBubbleRadius: Int,
    @Px public val largeTailBubbleOffset: Int,
    @Px public val smallTailBubbleCy: Int,
    @Px public val smallTailBubbleRadius: Int,
    @Px public val smallTailBubbleOffset: Int,
    @Px public val verticalPadding: Int,
    public val messageOptionsUserReactionOrientation: Int,
) : ViewStyle {

    internal companion object {
        private val DEFAULT_BUBBLE_BORDER_COLOR_MINE = R.color.ui_grey_whisper
        private val DEFAULT_BUBBLE_COLOR_MINE = R.color.ui_grey_whisper
        private val DEFAULT_BUBBLE_COLOR_THEIRS = R.color.ui_grey_gainsboro
        private val DEFAULT_BUBBLE_BORDER_WIDTH_MINE = 1.dpToPx() * 1.5f

        operator fun invoke(context: Context, attrs: AttributeSet?): ViewReactionsViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ViewReactionsView,
                R.attr.ermisUiMessageListViewReactionsStyle,
                0,
            ).use { a ->
                return Builder(a, context)
                    .bubbleBorderColorMine(R.styleable.ViewReactionsView_ermisUiReactionsBubbleBorderColorMine)
                    .bubbleBorderColorTheirs(R.styleable.ViewReactionsView_ermisUiReactionsBubbleBorderColorTheirs)
                    .bubbleBorderWidthMine(R.styleable.ViewReactionsView_ermisUiReactionsBubbleBorderWidthMine)
                    .bubbleBorderWidthTheirs(R.styleable.ViewReactionsView_ermisUiReactionsBubbleBorderWidthTheirs)
                    .bubbleColorMine(R.styleable.ViewReactionsView_ermisUiReactionsBubbleColorMine)
                    .bubbleColorTheirs(R.styleable.ViewReactionsView_ermisUiReactionsBubbleColorTheirs)
                    .messageOptionsUserReactionBubbleOrientation(
                        R.styleable.ViewReactionsView_ermisUiMessageOptionsUserReactionOrientation,
                    )
                    .build()
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
            private var messageOptionsUserReactionOrientation: Int =
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
                this.messageOptionsUserReactionOrientation =
                    array.getInt(bubbleOrientation, MessageOptionsUserReactionAlignment.BY_USER.value)
            }

            fun build(): ViewReactionsViewStyle {
                val totalHeight =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_total_height)
                val horizontalPadding =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_horizontal_padding)
                val itemSize =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_item_size)
                val bubbleHeight =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_bubble_height)
                val bubbleRadius =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_bubble_radius)
                val largeTailBubbleCy =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_large_tail_bubble_cy)
                val largeTailBubbleRadius =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_large_tail_bubble_radius)
                val largeTailBubbleOffset =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_large_tail_bubble_offset)
                val smallTailBubbleCy =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_small_tail_bubble_cy)
                val smallTailBubbleRadius =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_small_tail_bubble_radius)
                val smallTailBubbleOffset =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_small_tail_bubble_offset)
                val verticalPadding =
                    context.getDimension(R.dimen.ermis_ui_view_reactions_vertical_padding)

                return ViewReactionsViewStyle(
                    bubbleBorderColorMine = bubbleBorderColorMine,
                    bubbleBorderColorTheirs = bubbleBorderColorTheirs,
                    bubbleBorderWidthMine = bubbleBorderWidthMine,
                    bubbleBorderWidthTheirs = bubbleBorderWidthTheirs,
                    bubbleColorMine = bubbleColorMine,
                    bubbleColorTheirs = bubbleColorTheirs,
                    totalHeight = totalHeight,
                    horizontalPadding = horizontalPadding,
                    itemSize = itemSize,
                    bubbleHeight = bubbleHeight,
                    bubbleRadius = bubbleRadius,
                    largeTailBubbleCy = largeTailBubbleCy,
                    largeTailBubbleRadius = largeTailBubbleRadius,
                    largeTailBubbleOffset = largeTailBubbleOffset,
                    smallTailBubbleCy = smallTailBubbleCy,
                    smallTailBubbleRadius = smallTailBubbleRadius,
                    smallTailBubbleOffset = smallTailBubbleOffset,
                    verticalPadding = verticalPadding,
                    messageOptionsUserReactionOrientation = messageOptionsUserReactionOrientation,
                ).let(TransformStyle.viewReactionsStyleTransformer::transform)
            }
        }
    }
}
