
package network.ermis.ui.view.messages.adapter.view.internal

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType
import network.ermis.ui.ChatUI
import network.ermis.ui.common.images.internal.ErmisImageLoader
import network.ermis.ui.common.images.resizing.applyStreamCdnImageResizingIfEnabled
import network.ermis.ui.common.utils.extensions.imagePreviewUrl
import network.ermis.ui.view.messages.DefaultQuotedAttachmentViewStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.load
import network.ermis.ui.utils.loadAttachmentThumb

/**
 * View tasked to show the attachments supported by default.
 */
internal class DefaultQuotedAttachmentView : AppCompatImageView {

    constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init(context)
    }

    private lateinit var style: DefaultQuotedAttachmentViewStyle
    private var attachment: Attachment? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        layoutParams = layoutParams.apply {
            when (attachment?.type) {
                AttachmentType.FILE, AttachmentType.VIDEO -> {
                    width = style.fileAttachmentWidth
                    height = style.fileAttachmentHeight
                }

                else -> {
                    width = style.imageAttachmentWidth
                    height = style.imageAttachmentHeight
                }
            }
        }
    }

    /**
     * Show the attachment sent inside the message.
     *
     * @param attachment The attachment we wish to show.
     */
    fun showAttachment(attachment: Attachment) {
        this.attachment = attachment
        when (attachment.type) {
            AttachmentType.FILE, AttachmentType.VIDEO, AttachmentType.AUDIO_RECORDING -> loadAttachmentThumb(attachment)
            AttachmentType.IMAGE -> showAttachmentThumb(
                attachment.imagePreviewUrl?.applyStreamCdnImageResizingIfEnabled(ChatUI.streamCdnImageResizing),
            )
            AttachmentType.GIPHY -> showAttachmentThumb(attachment.thumbUrl)
            else -> showAttachmentThumb(attachment.image)
        }
    }

    /**
     * Sets the attachment thumbnail in case of images and giphy.
     *
     * @param url The url to the image resource.
     */
    private fun showAttachmentThumb(url: String?) {
        load(
            data = url,
            transformation = ErmisImageLoader.ImageTransformation.RoundedCorners(style.quotedImageRadius.toFloat()),
        )
    }

    private fun init(context: Context) {
        this.style = DefaultQuotedAttachmentViewStyle(context)
    }
}
