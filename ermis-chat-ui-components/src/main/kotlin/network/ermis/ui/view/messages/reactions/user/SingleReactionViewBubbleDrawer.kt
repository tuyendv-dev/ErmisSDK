
package network.ermis.ui.view.messages.reactions.user

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import network.ermis.ui.utils.extensions.isRtlLayout

/**
 * Draw the bubble of single reaction view reactions.
 */
internal class SingleReactionViewBubbleDrawer(
    private val viewReactionsViewStyle: SingleReactionViewStyle,
) {

    private val bubblePaintTheirs = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = viewReactionsViewStyle.bubbleColorTheirs
        style = Paint.Style.FILL
    }
    private val bubblePaintMine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = viewReactionsViewStyle.bubbleColorMine
        style = Paint.Style.FILL
    }
    private val bubbleStrokePaintMine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = viewReactionsViewStyle.bubbleBorderColorMine
        strokeWidth = viewReactionsViewStyle.bubbleBorderWidthMine
        style = Paint.Style.STROKE
    }

    private val bubbleStrokePaintTheirs by lazy {
        check(shouldDrawTheirsBorder()) {
            "You need to specify either bubbleBorderColorTheirs and bubbleBorderWidthTheirs to draw a border for " +
                "another user reaction bubble"
        }
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = viewReactionsViewStyle.bubbleBorderColorTheirs!!
            strokeWidth = viewReactionsViewStyle.bubbleBorderWidthTheirs!!
            style = Paint.Style.STROKE
        }
    }

    private var bubbleWidth: Int = 0
    private var isMyMessage: Boolean = false
    private var isOrientedTowardsStart: Boolean = false

    /**
     * Draws the bubble of reactions choosing the correct direction.
     *
     * @param context [Context].
     * @param canvas [Canvas].
     * @param bubbleWidth The width of the bubble. This should be at least bigger than all the columns of reactions.
     * @param isMyMessage Whether this is a message sent by the current user or not.
     * @param isOrientedTowardsStart Whether the bubble is oriented towards start or not.
     */
    fun drawReactionsBubble(
        context: Context,
        canvas: Canvas,
        bubbleWidth: Int,
        isMyMessage: Boolean,
        isOrientedTowardsStart: Boolean,
    ) {
        this.isMyMessage = isMyMessage
        this.bubbleWidth = bubbleWidth
        this.isOrientedTowardsStart = isOrientedTowardsStart

        val isRtl = context.isRtlLayout

        val path = Path().apply {
            op(createBubbleRoundRectPath(), Path.Op.UNION)
            op(createLargeTailBubblePath(isRtl), Path.Op.UNION)
            op(createSmallTailBubblePath(isRtl), Path.Op.UNION)
        }

        if (isMyMessage) {
            canvas.drawPath(path, bubblePaintMine)
            canvas.drawPath(path, bubbleStrokePaintMine)
        } else {
            canvas.drawPath(path, bubblePaintTheirs)
            if (shouldDrawTheirsBorder()) {
                canvas.drawPath(path, bubbleStrokePaintTheirs)
            }
        }
    }

    private fun shouldDrawTheirsBorder(): Boolean =
        viewReactionsViewStyle.bubbleBorderColorTheirs != null && viewReactionsViewStyle.bubbleBorderWidthTheirs != null

    private fun createBubbleRoundRectPath(): Path {
        val strokeOffset = getStrokeOffset()
        return Path().apply {
            addRoundRect(
                strokeOffset,
                strokeOffset,
                bubbleWidth.toFloat() - strokeOffset,
                viewReactionsViewStyle.bubbleHeight.toFloat(),
                viewReactionsViewStyle.bubbleRadius.toFloat(),
                viewReactionsViewStyle.bubbleRadius.toFloat(),
                Path.Direction.CW,
            )
        }
    }

    private fun getStrokeOffset(): Float {
        return when {
            isMyMessage -> {
                viewReactionsViewStyle.bubbleBorderWidthMine / 2
            }
            shouldDrawTheirsBorder() -> {
                viewReactionsViewStyle.bubbleBorderWidthTheirs!! / 2
            }
            else -> {
                0f
            }
        }
    }

    /**
     * Draws the path of large tail bubble.
     *
     * @param isRtl If the bubble should be drawn with inverted direction.
     */
    private fun createLargeTailBubblePath(isRtl: Boolean): Path {
        return Path().apply {
            addCircle(
                positionBubble(isRtl, viewReactionsViewStyle.largeTailBubbleOffset.toFloat()),
                viewReactionsViewStyle.largeTailBubbleCy.toFloat(),
                viewReactionsViewStyle.largeTailBubbleRadius.toFloat(),
                Path.Direction.CW,
            )
        }
    }

    /**
     * Draws the path of small tail bubble.
     *
     * @param isRtl If the bubble should be drawn with inverted direction.
     */
    private fun createSmallTailBubblePath(isRtl: Boolean): Path {
        return Path().apply {
            addCircle(
                positionBubble(isRtl, viewReactionsViewStyle.smallTailBubbleOffset.toFloat()),
                viewReactionsViewStyle.smallTailBubbleCy.toFloat(),
                viewReactionsViewStyle.smallTailBubbleRadius.toFloat() - getStrokeOffset(),
                Path.Direction.CW,
            )
        }
    }

    private fun calculateBubbleCenterX(bubbleOffset: Float): Float {
        return if (!isOrientedTowardsStart) {
            bubbleWidth / 2 + bubbleOffset
        } else {
            bubbleWidth / 2 - bubbleOffset
        }
    }

    /**
     * Applies an offset in the bubble, respecting RTL support.
     *
     * @param isRtl If the bubble should be drawn with inverted direction.
     * @param bubbleOffset The offset to apply.
     */
    private fun positionBubble(isRtl: Boolean, bubbleOffset: Float): Float {
        return calculateBubbleCenterX(bubbleOffset).let { offset ->
            if (isRtl) {
                bubbleWidth.toFloat() - offset
            } else {
                offset
            }
        }
    }
}
