
package network.ermis.ui.view.messages.adapter.view.internal

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.google.android.material.shape.AbsoluteCornerSize
import com.google.android.material.shape.CornerSize
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import network.ermis.client.utils.attachment.isImage
import network.ermis.client.utils.attachment.isVideo
import network.ermis.core.models.Attachment
import network.ermis.ui.components.R
import io.getstream.chat.android.ui.common.extensions.internal.getOrDefault
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.viewholder.decorator.internal.BackgroundDecorator
import network.ermis.ui.view.messages.background.ShapeAppearanceModelFactory
import network.ermis.ui.utils.extensions.constrainViewToParentBySide
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.displayMetrics
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.horizontalChainInParent
import network.ermis.ui.utils.extensions.isBottomPosition
import network.ermis.ui.utils.extensions.verticalChainInParent
import network.ermis.ui.utils.extension.hasLink
import io.getstream.log.taggedLogger

internal class MediaAttachmentsGroupView : ConstraintLayout {

    private val logger by taggedLogger("MediaAttachGroupView")

    var attachmentClickListener: AttachmentClickListener? = null
    var attachmentLongClickListener: AttachmentLongClickListener? = null
    private val maxMediaAttachmentHeight: Int by lazy {
        (displayMetrics().heightPixels * MAX_HEIGHT_PERCENTAGE).toInt()
    }

    private var state: State = State.Empty

    constructor(context: Context) : super(context.createStreamThemeWrapper())
    constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
        defStyleRes,
    )

    @Suppress("MagicNumber")
    fun showAttachments(attachments: List<Attachment>) {
        val media = attachments.filter { attachment ->
            !attachment.hasLink() &&
                (attachment.isImage() || attachment.isVideo())
        }
        logger.d { "[showAttachments] attachments.size: ${media.size}" }
        when (media.size) {
            0 -> Unit
            1 -> showOne(media.first())
            2 -> showTwo(media.first(), media[1])
            3 -> showThree(media.first(), media[1], media[2])
            else -> showFour(
                media.first(),
                media[1],
                media[2],
                media[3],
                media.size - MAX_PREVIEW_COUNT,
            )
        }
        (background as? MaterialShapeDrawable)?.shapeAppearanceModel?.let(::applyToMediaPreviews)
    }

    private fun showOne(first: Attachment) {
        removeAllViews()
        val mediaAttachmentView = createMediaAttachmentView()
        addView(mediaAttachmentView)
        state = State.OneView(mediaAttachmentView)
        ConstraintSet().apply {
            constrainMaxHeight(mediaAttachmentView.id, maxMediaAttachmentHeight)
            constrainWidth(mediaAttachmentView.id, ViewGroup.LayoutParams.MATCH_PARENT)
            constrainViewToParentBySide(mediaAttachmentView, ConstraintSet.LEFT)
            constrainViewToParentBySide(mediaAttachmentView, ConstraintSet.RIGHT)
            constrainViewToParentBySide(mediaAttachmentView, ConstraintSet.TOP)
            constrainViewToParentBySide(mediaAttachmentView, ConstraintSet.BOTTOM)

            val mediaWidth = first.originalWidth?.toFloat()
            val mediaHeight = first.originalHeight?.toFloat()

            // Used to set a dimension ratio before we load the media
            // so that message positions don't jump after we load it.
            if (mediaWidth != null && mediaHeight != null) {
                val ratio = (mediaWidth / mediaHeight).toString()
                this.setDimensionRatio(mediaAttachmentView.id, ratio)
            } else {
                this.setDimensionRatio(mediaAttachmentView.id, "1")
            }

            applyTo(this@MediaAttachmentsGroupView)
        }

        mediaAttachmentView.showAttachment(first)
    }

    private fun showTwo(first: Attachment, second: Attachment) {
        removeAllViews()
        val viewOne = createMediaAttachmentView().also { addView(it) }
        val viewTwo = createMediaAttachmentView().also { addView(it) }
        state = State.TwoViews(viewOne, viewTwo)
        ConstraintSet().apply {
            setupMinHeight(viewOne, false)
            setupMinHeight(viewTwo, false)
            constrainViewToParentBySide(viewOne, ConstraintSet.TOP)
            constrainViewToParentBySide(viewTwo, ConstraintSet.TOP)
            constrainViewToParentBySide(viewOne, ConstraintSet.BOTTOM)
            constrainViewToParentBySide(viewTwo, ConstraintSet.BOTTOM)
            horizontalChainInParent(viewOne, viewTwo)
            applyTo(this@MediaAttachmentsGroupView)
        }
        viewOne.showAttachment(first)
        viewTwo.showAttachment(second)
    }

    private fun showThree(first: Attachment, second: Attachment, third: Attachment) {
        removeAllViews()
        val viewOne = createMediaAttachmentView().also { addView(it) }
        val viewTwo = createMediaAttachmentView().also { addView(it) }
        val viewThree = createMediaAttachmentView().also { addView(it) }
        state = State.ThreeViews(viewOne, viewTwo, viewThree)
        ConstraintSet().apply {
            setupMinHeight(viewTwo, true)
            setupMinHeight(viewThree, true)
            horizontalChainInParent(viewOne, viewTwo)
            horizontalChainInParent(viewOne, viewThree)
            verticalChainInParent(viewTwo, viewThree)
            connect(viewOne.id, ConstraintSet.TOP, viewTwo.id, ConstraintSet.TOP)
            connect(viewOne.id, ConstraintSet.BOTTOM, viewThree.id, ConstraintSet.BOTTOM)
            applyTo(this@MediaAttachmentsGroupView)
        }
        viewOne.showAttachment(first)
        viewTwo.showAttachment(second)
        viewThree.showAttachment(third)
    }

    private fun showFour(
        first: Attachment,
        second: Attachment,
        third: Attachment,
        fourth: Attachment,
        andMoreCount: Int = 0,
    ) {
        removeAllViews()
        val viewOne = createMediaAttachmentView().also { addView(it) }
        val viewTwo = createMediaAttachmentView().also { addView(it) }
        val viewThree = createMediaAttachmentView().also { addView(it) }
        val viewFour = createMediaAttachmentView().also { addView(it) }
        state = State.FourViews(viewOne, viewTwo, viewThree, viewFour)
        ConstraintSet().apply {
            setupMinHeight(viewOne, true)
            setupMinHeight(viewTwo, true)
            setupMinHeight(viewThree, true)
            setupMinHeight(viewFour, true)
            horizontalChainInParent(viewOne, viewTwo)
            horizontalChainInParent(viewThree, viewFour)
            verticalChainInParent(viewOne, viewThree)
            verticalChainInParent(viewTwo, viewFour)
            applyTo(this@MediaAttachmentsGroupView)
        }
        viewOne.showAttachment(first)
        viewTwo.showAttachment(second)
        viewThree.showAttachment(third)
        viewFour.showAttachment(fourth, andMoreCount)
    }

    override fun setBackground(background: Drawable) {
        super.setBackground(background)
        if (background is MaterialShapeDrawable) {
            applyToMediaPreviews(background.shapeAppearanceModel)
        }
    }

    private fun applyToMediaPreviews(shapeAppearanceModel: ShapeAppearanceModel) {
        val topLeftCorner = shapeAppearanceModel.getCornerSize(ShapeAppearanceModel::getTopLeftCornerSize)
        val topRightCorner = shapeAppearanceModel.getCornerSize(ShapeAppearanceModel::getTopRightCornerSize)
        val bottomRightCorner = shapeAppearanceModel.getCornerSize(ShapeAppearanceModel::getBottomRightCornerSize)
        val bottomLeftCorner = shapeAppearanceModel.getCornerSize(ShapeAppearanceModel::getBottomLeftCornerSize)
        when (val stateCopy = state) {
            is State.OneView -> stateCopy.view.setMediaPreviewShapeByCorners(
                topLeftCorner,
                topRightCorner,
                bottomRightCorner,
                bottomLeftCorner,
            )
            is State.TwoViews -> {
                stateCopy.viewOne.setMediaPreviewShapeByCorners(topLeftCorner, 0f, 0f, bottomLeftCorner)
                stateCopy.viewTwo.setMediaPreviewShapeByCorners(0f, topRightCorner, bottomRightCorner, 0f)
            }
            is State.ThreeViews -> {
                stateCopy.viewOne.setMediaPreviewShapeByCorners(topLeftCorner, 0f, 0f, bottomLeftCorner)
                stateCopy.viewTwo.setMediaPreviewShapeByCorners(0f, topRightCorner, 0f, 0f)
                stateCopy.viewThree.setMediaPreviewShapeByCorners(0f, 0f, bottomRightCorner, 0f)
            }
            is State.FourViews -> {
                stateCopy.viewOne.setMediaPreviewShapeByCorners(topLeftCorner, 0f, 0f, 0f)
                stateCopy.viewTwo.setMediaPreviewShapeByCorners(0f, topRightCorner, 0f, 0f)
                stateCopy.viewThree.setMediaPreviewShapeByCorners(0f, 0f, 0f, bottomLeftCorner)
                stateCopy.viewFour.setMediaPreviewShapeByCorners(0f, 0f, bottomRightCorner, 0f)
            }
            else -> Unit
        }
    }

    private fun ShapeAppearanceModel.getCornerSize(selector: (ShapeAppearanceModel) -> CornerSize): Float {
        return (((selector(this) as? AbsoluteCornerSize)?.cornerSize ?: 0f) - STROKE_WIDTH).takeIf { it >= 0f }
            .getOrDefault(0f)
    }

    private fun createMediaAttachmentView(): MediaAttachmentView {
        return MediaAttachmentView(context).also {
            it.id = generateViewId()
            it.attachmentClickListener = attachmentClickListener
            it.attachmentLongClickListener = attachmentLongClickListener
        }
    }

    /**
     * Configured the background of the View.
     *
     * @param data [MessageListItem.MessageItem].
     */
    fun setupBackground(data: MessageListItem.MessageItem) {
        background = ShapeAppearanceModelFactory.create(
            context,
            BackgroundDecorator.DEFAULT_CORNER_RADIUS,
            0F,
            data.isMine,
            data.isBottomPosition(),
        )
            .let(::MaterialShapeDrawable)
            .apply { setTint(ContextCompat.getColor(context, R.color.ui_literal_transparent)) }
    }

    private sealed class State {
        object Empty : State() {
            override fun toString(): String = "Empty"
        }
        data class OneView(val view: MediaAttachmentView) : State()
        data class TwoViews(val viewOne: MediaAttachmentView, val viewTwo: MediaAttachmentView) : State()
        data class ThreeViews(
            val viewOne: MediaAttachmentView,
            val viewTwo: MediaAttachmentView,
            val viewThree: MediaAttachmentView,
        ) : State()

        data class FourViews(
            val viewOne: MediaAttachmentView,
            val viewTwo: MediaAttachmentView,
            val viewThree: MediaAttachmentView,
            val viewFour: MediaAttachmentView,
        ) : State()
    }

    companion object {
        private const val MAX_HEIGHT_PERCENTAGE = .75
        private const val MAX_PREVIEW_COUNT = 4
        private val MIN_HEIGHT_PX = 95.dpToPx()
        private val STROKE_WIDTH = 2.dpToPxPrecise()

        private fun ConstraintSet.setupMinHeight(view: View, isQuarter: Boolean) {
            this.constrainMinHeight(view.id, if (isQuarter) MIN_HEIGHT_PX else 2 * MIN_HEIGHT_PX)
        }
    }
}
