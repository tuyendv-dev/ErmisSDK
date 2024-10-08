
package network.ermis.ui.view.messages.reactions.edit

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import network.ermis.ui.components.R
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.use

/**
 * Style for [EditReactionsView].
 * Use this class together with [TransformStyle.editReactionsStyleTransformer] to change [EditReactionsView]
 * styles programmatically.
 *
 * @param bubbleColorMine Reaction bubble color for the current user.
 * @param bubbleColorTheirs Reaction bubble color for other users.
 * @param horizontalPadding The horizontal padding to be applied to the start and end of the bubble.
 * @param itemSize The size of the reaction item.
 * @param bubbleHeight Height of the reactions part of the bubble.
 * @param bubbleRadius The radius of the reactions part of the bubble.
 * @param largeTailBubbleCyOffset The y axis offset of the large tail bubble center point
 * @param largeTailBubbleRadius The radius of the large tail bubble.
 * @param largeTailBubbleOffset The x axis offset of the large tail bubble center point.
 * @param smallTailBubbleCyOffset The y axis offset of the small tail bubble center point.
 * @param smallTailBubbleRadius The radius of the bubble small tail.
 * @param smallTailBubbleOffset The x axis offset of the small tail bubble center point.
 * @param reactionsColumn The number of reaction columns.
 * @param verticalPadding The vertical padding to be applied to top and bottom of the view.
 */
public data class EditReactionsViewStyle(
    @ColorInt public val bubbleColorMine: Int,
    @ColorInt public val bubbleColorTheirs: Int,
    @Px public val horizontalPadding: Int,
    @Px public val itemSize: Int,
    @Px public val bubbleHeight: Int,
    @Px public val bubbleRadius: Int,
    @Px public val largeTailBubbleCyOffset: Int,
    @Px public val largeTailBubbleRadius: Int,
    @Px public val largeTailBubbleOffset: Int,
    @Px public val smallTailBubbleCyOffset: Int,
    @Px public val smallTailBubbleRadius: Int,
    @Px public val smallTailBubbleOffset: Int,
    public val reactionsColumn: Int,
    @Px public val verticalPadding: Int,
) : ViewStyle {

    internal data class Builder(private val array: TypedArray, private val context: Context) {
        @ColorInt
        private var bubbleColorMine: Int = context.getColorCompat(R.color.ui_white)

        @ColorInt
        private var bubbleColorTheirs: Int = context.getColorCompat(R.color.ui_white)

        private var reactionsColumn = 5

        fun bubbleColorMine(@StyleableRes bubbleColorMineResId: Int) = apply {
            bubbleColorMine = array.getColor(bubbleColorMineResId, context.getColorCompat(R.color.ui_white))
        }

        fun bubbleColorTheirs(@StyleableRes bubbleColorTheirsResId: Int) = apply {
            bubbleColorTheirs = array.getColor(bubbleColorTheirsResId, context.getColorCompat(R.color.ui_white))
        }

        fun reactionsColumns(columnsResId: Int) = apply {
            reactionsColumn = array.getInt(columnsResId, 5)
        }

        fun build(): EditReactionsViewStyle {
            val horizontalPadding =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_horizontal_padding)
            val itemSize =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_item_size)
            val bubbleHeight =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_bubble_height)
            val bubbleRadius =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_bubble_radius)
            val largeTailBubbleCyOffset =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_large_tail_bubble_cy_offset)
            val largeTailBubbleRadius =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_large_tail_bubble_radius)
            val largeTailBubbleOffset =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_large_tail_bubble_offset)
            val smallTailBubbleCyOffset =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_small_tail_bubble_cy_offset)
            val smallTailBubbleRadius =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_small_tail_bubble_radius)
            val smallTailBubbleOffset =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_small_tail_bubble_offset)
            val verticalPadding =
                context.getDimension(R.dimen.ermis_ui_edit_reactions_vertical_padding)

            return EditReactionsViewStyle(
                bubbleColorMine = bubbleColorMine,
                bubbleColorTheirs = bubbleColorTheirs,
                horizontalPadding = horizontalPadding,
                itemSize = itemSize,
                bubbleHeight = bubbleHeight,
                bubbleRadius = bubbleRadius,
                largeTailBubbleCyOffset = largeTailBubbleCyOffset,
                largeTailBubbleRadius = largeTailBubbleRadius,
                largeTailBubbleOffset = largeTailBubbleOffset,
                smallTailBubbleCyOffset = smallTailBubbleCyOffset,
                smallTailBubbleRadius = smallTailBubbleRadius,
                smallTailBubbleOffset = smallTailBubbleOffset,
                reactionsColumn = reactionsColumn,
                verticalPadding = verticalPadding,
            ).let(TransformStyle.editReactionsStyleTransformer::transform)
        }
    }

    public companion object {

        internal operator fun invoke(context: Context, attrs: AttributeSet?): EditReactionsViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.EditReactionsView,
                0,
                0,
            ).use { a ->
                return Builder(a, context)
                    .bubbleColorMine(R.styleable.EditReactionsView_ermisUiReactionsBubbleColorMine)
                    .bubbleColorTheirs(R.styleable.EditReactionsView_ermisUiReactionsBubbleColorTheirs)
                    .build()
            }
        }
    }
}
