
package network.ermis.ui.view.gallery.options

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import network.ermis.ui.components.databinding.AttachmentGalleryOptionsViewBinding
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.setStartDrawable
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * Represents the media options menu, used to perform different actions for
 * the currently selected media.
 */
internal class AttachmentGalleryOptionsView : FrameLayout {

    private val binding = AttachmentGalleryOptionsViewBinding
        .inflate(streamThemeInflater, this, true)

    /**
     * The style of the option items.
     */
    private lateinit var style: AttachmentGalleryOptionsViewStyle

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init(attrs)
    }

    /**
     * Registers a callback to be invoked when the "reply" option is clicked.
     *
     * @param listener The callback that will run.
     */
    fun setReplyClickListener(listener: OnClickListener) {
        binding.reply.setOnClickListener(listener)
    }

    /**
     * Registers a callback to be invoked when the "delete" option is clicked.
     *
     * @param listener The callback that will run.
     */
    fun setDeleteClickListener(listener: OnClickListener) {
        binding.delete.setOnClickListener(listener)
    }

    /**
     * Registers a callback to be invoked when the "show in chat" option is clicked.
     *
     * @param listener The callback that will run.
     */
    fun setShowInChatClickListener(listener: OnClickListener) {
        binding.showInChat.setOnClickListener(listener)
    }

    /**
     * Registers a callback to be invoked when the "save media" option is clicked.
     *
     * @param listener The callback that will run.
     */
    fun setSaveMediaClickListener(listener: OnClickListener) {
        binding.saveMedia.setOnClickListener(listener)
    }

    /**
     * Refreshes the option list based on the message ownership.
     *
     * @param isMine If the message belongs to the current user.
     */
    fun setIsMine(isMine: Boolean) {
        binding.delete.isVisible = isMine && style.deleteOptionEnabled
    }

    /**
     * Initializes the style and configuration of the View based on the [style].
     */
    private fun init(attrs: AttributeSet?) {
        style = AttachmentGalleryOptionsViewStyle(context, attrs)

        binding.optionsContainer.setCardBackgroundColor(style.backgroundColor)

        if (style.replyOptionEnabled) {
            binding.reply.setStartDrawable(style.replyOptionDrawable)
            binding.reply.setTextStyle(style.optionTextStyle)
        } else {
            binding.reply.isVisible = false
        }

        if (style.showInChatOptionEnabled) {
            binding.showInChat.setStartDrawable(style.showInChatOptionDrawable)
            binding.showInChat.setTextStyle(style.optionTextStyle)
        } else {
            binding.showInChat.isVisible = false
        }

        if (style.saveMediaOptionEnabled) {
            binding.saveMedia.setStartDrawable(style.saveMediaOptionDrawable)
            binding.saveMedia.setTextStyle(style.optionTextStyle)
        } else {
            binding.saveMedia.isVisible = false
        }

        if (style.deleteOptionEnabled) {
            binding.delete.setStartDrawable(style.deleteOptionDrawable)
            binding.delete.setTextStyle(style.optionTextStyle)
            binding.delete.setTextColor(style.deleteOptionTextColor)
        } else {
            binding.delete.isVisible = false
        }
    }
}
