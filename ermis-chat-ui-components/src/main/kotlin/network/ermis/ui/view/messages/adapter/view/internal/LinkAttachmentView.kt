
package network.ermis.ui.view.messages.adapter.view.internal

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import network.ermis.core.models.Attachment
import network.ermis.ui.components.R
import network.ermis.ui.common.images.internal.ErmisImageLoader.ImageTransformation.RoundedCorners
import network.ermis.ui.common.utils.extensions.imagePreviewUrl
import network.ermis.ui.components.databinding.LinkAttachmentsViewBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.font.TextStyle
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.load

internal class LinkAttachmentView : FrameLayout {
    private val binding = LinkAttachmentsViewBinding.inflate(streamThemeInflater, this, true)
    private var previewUrl: String? = null

    constructor(context: Context) : super(context.createStreamThemeWrapper())
    constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    )

    /**
     * Displays the given attachment.
     *
     * @param attachment The attachment to be displayed.
     * @param style The style used for applying various things such as text styles.
     */
    fun showLinkAttachment(attachment: Attachment, style: MessageListItemStyle) {
        previewUrl = attachment.titleLink ?: attachment.ogUrl
        showTitle(attachment, style)
        showDescription(attachment, style)
        showLabel(attachment, style)
        showAttachmentImage(attachment)
    }

    /**
     * Sets up the style for the link title text and displays it
     * if it exists.
     *
     * @param attachment The attachment used to obtain the title.
     * @param style The style which contains the title text style that
     * will be applied.
     */
    private fun showTitle(attachment: Attachment, style: MessageListItemStyle) {
        val title = attachment.title
        if (title != null) {
            binding.titleTextView.isVisible = true
            binding.titleTextView.text = title
        } else {
            binding.titleTextView.isVisible = false
        }
        binding.titleTextView.setTextStyle(style.textStyleLinkTitle)
    }

    /**
     * Sets up the style for the link description text and displays it
     * if it exists.
     *
     * @param attachment The attachment used to obtain the description.
     * @param style The style which contains the description text style that
     * will be applied.
     */
    private fun showDescription(attachment: Attachment, style: MessageListItemStyle) {
        val description = attachment.text
        if (description != null) {
            binding.descriptionTextView.isVisible = true
            binding.descriptionTextView.text = description
        } else {
            binding.descriptionTextView.isVisible = false
        }
        binding.descriptionTextView.setTextStyle(style.textStyleLinkDescription)
    }

    /**
     * Sets up the style for the link label text and displays it
     * if it exists.
     *
     * @param attachment The attachment used to obtain the label.
     * @param style The style which contains the label text style that
     * will be applied.
     */
    private fun showLabel(attachment: Attachment, style: MessageListItemStyle) {
        val label = attachment.authorName
        if (label != null) {
            binding.labelContainer.isVisible = true
            binding.labelTextView.text = label.replaceFirstChar(Char::uppercase)
        } else {
            binding.labelContainer.isVisible = false
        }
        binding.labelTextView.setTextStyle(style.textStyleLinkLabel)
    }

    /**
     * Shows the attachment preview image if it is not null.
     */
    private fun showAttachmentImage(attachment: Attachment) {
        if (attachment.imagePreviewUrl != null) {
            binding.linkPreviewContainer.isVisible = true

            binding.linkPreviewImageView.load(
                data = attachment.imagePreviewUrl,
                placeholderResId = R.drawable.picture_placeholder,
                onStart = { binding.progressBar.isVisible = true },
                onComplete = { binding.progressBar.isVisible = false },
                transformation = RoundedCorners(LINK_PREVIEW_CORNER_RADIUS),
            )
        } else {
            binding.linkPreviewContainer.isVisible = false
            binding.progressBar.isVisible = false
        }
    }

    internal fun setTitleTextStyle(textStyle: TextStyle) {
        binding.titleTextView.setTextStyle(textStyle)
    }

    internal fun setDescriptionTextStyle(textStyle: TextStyle) {
        binding.descriptionTextView.setTextStyle(textStyle)
    }

    internal fun setLabelTextStyle(textStyle: TextStyle) {
        binding.labelTextView.setTextStyle(textStyle)
    }

    internal fun setLinkDescriptionMaxLines(maxLines: Int) {
        binding.descriptionTextView.maxLines = maxLines
    }

    fun setLinkPreviewClickListener(linkPreviewClickListener: LinkPreviewClickListener) {
        setOnClickListener {
            previewUrl?.let { url ->
                linkPreviewClickListener.onLinkPreviewClick(url)
            }
        }
    }

    fun setLongClickTarget(longClickTarget: View) {
        setOnLongClickListener {
            longClickTarget.performLongClick()
            true
        }
    }

    fun interface LinkPreviewClickListener {
        fun onLinkPreviewClick(url: String)
    }

    companion object {
        private val LINK_PREVIEW_CORNER_RADIUS = 8.dpToPxPrecise()
    }
}
