
package network.ermis.ui.view.messages.adapter.viewholder.attachment

import android.content.res.ColorStateList
import android.view.ViewGroup
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType
import network.ermis.core.models.Message
import network.ermis.ui.components.databinding.UnsupportedAttachmentViewBinding
import network.ermis.ui.view.messages.UnsupportedAttachmentViewStyle
import network.ermis.ui.view.messages.adapter.MessageListListenerContainer
import network.ermis.ui.view.messages.adapter.MessageListListeners
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.extension.hasLink

/**
 * Fallback factory for unsupported attachment types.
 */
public class UnsupportedAttachmentFactory :
    network.ermis.ui.view.messages.adapter.viewholder.attachment.AttachmentFactory {

    /**
     * Checks if the message contains unsupported attachments.
     *
     * @param message The message containing custom attachments that we are going to render.
     * @return True it the message contains unsupported attachment.
     */
    override fun canHandle(message: Message): Boolean {
        return message.attachments.isNotEmpty() &&
            message.attachments.all { !it.hasLink() && !it.isSupported() }
    }

    /**
     * Creates fallback UI that represents unsupported attachments.
     *
     * @param message The message containing custom attachments that we are going to render.
     * @param listeners [MessageListListenerContainer] with listeners for the message list.
     * @param parent The parent View where the attachment content view is supposed to be placed.
     * @return An inner ViewHolder with the fallback attachment content view.
     */
    override fun createViewHolder(
        message: Message,
        listeners: MessageListListeners?,
        parent: ViewGroup,
    ): InnerAttachmentViewHolder {
        val binding = UnsupportedAttachmentViewBinding
            .inflate(parent.context.streamThemeInflater, parent, false)

        val style = UnsupportedAttachmentViewStyle(parent.context, null)

        val shapeAppearanceModel = ShapeAppearanceModel.Builder()
            .setAllCorners(CornerFamily.ROUNDED, style.cornerRadius.toFloat())
            .build()
        binding.attachmentContainer.background = MaterialShapeDrawable(shapeAppearanceModel).apply {
            fillColor = ColorStateList.valueOf(style.backgroundColor)
            strokeColor = ColorStateList.valueOf(style.strokeColor)
            strokeWidth = style.strokeWidth.toFloat()
        }
        binding.titleImageView.setTextStyle(style.titleTextStyle)

        return object : InnerAttachmentViewHolder(binding.root) {}
    }

    /**
     * Checks if the attachment type is supported.
     *
     * @return True if the attachment type is supported.
     */
    private fun Attachment.isSupported(): Boolean {
        return SUPPORTED_ATTACHMENT_TYPES.contains(type)
    }

    private companion object {
        /**
         * The list of supported attachment types.
         */
        private val SUPPORTED_ATTACHMENT_TYPES: Set<String> = setOf(
            AttachmentType.IMAGE,
            AttachmentType.GIPHY,
            AttachmentType.VIDEO,
            AttachmentType.AUDIO,
            AttachmentType.FILE,
            AttachmentType.LINK,
            AttachmentType.AUDIO_RECORDING,
        )
    }
}
