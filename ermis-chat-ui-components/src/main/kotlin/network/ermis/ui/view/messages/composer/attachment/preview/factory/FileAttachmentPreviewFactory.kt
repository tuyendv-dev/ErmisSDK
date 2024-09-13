
package network.ermis.ui.view.messages.composer.attachment.preview.factory

import android.view.ViewGroup
import network.ermis.core.models.Attachment
import network.ermis.ui.common.utils.MediaStringUtil
import network.ermis.ui.components.databinding.FileAttachmentPreviewBinding
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.view.messages.composer.attachment.preview.AttachmentPreviewViewHolder
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.loadAttachmentThumb
import network.ermis.ui.utils.extension.isAnyFileType
import io.getstream.log.taggedLogger

/**
 * The default [AttachmentPreviewFactory] for file attachments.
 */
public class FileAttachmentPreviewFactory : AttachmentPreviewFactory {

    private val logger by taggedLogger("AttachFilePreviewFactory")

    /**
     * Checks if the factory can create a preview ViewHolder for this attachment.
     *
     * @param attachment The attachment we want to show a preview for.
     * @return True if the factory is able to provide a preview for the given [Attachment].
     */
    public override fun canHandle(attachment: Attachment): Boolean {
        logger.i { "[canHandle] isAnyFileType: ${attachment.isAnyFileType()}; $attachment" }
        return attachment.isAnyFileType()
    }

    /**
     * Creates and instantiates a new instance of [FileAttachmentPreviewFactory].
     *
     * @param parentView The parent container.
     * @param attachmentRemovalListener Click listener for the remove attachment button.
     * @param style Used to style the factory. If null, the factory will retain
     * the default appearance.
     *
     * @return An instance of attachment preview ViewHolder.
     */
    override fun onCreateViewHolder(
        parentView: ViewGroup,
        attachmentRemovalListener: (Attachment) -> Unit,
        style: MessageComposerViewStyle?,
    ): AttachmentPreviewViewHolder {
        return FileAttachmentPreviewBinding
            .inflate(parentView.context.streamThemeInflater, parentView, false)
            .let { FileAttachmentPreviewHandler(it, attachmentRemovalListener) }
    }

    /**
     * A ViewHolder for file attachment preview.
     *
     * @param binding Binding generated for the layout.
     * @param attachmentRemovalListener Click listener for the remove attachment button.
     */
    private class FileAttachmentPreviewHandler(
        private val binding: FileAttachmentPreviewBinding,
        attachmentRemovalListener: (Attachment) -> Unit,
    ) : AttachmentPreviewViewHolder(binding.root) {

        private val logger by taggedLogger("AttachFilePreviewHolder")

        private lateinit var attachment: Attachment

        init {
            binding.removeButton.setOnClickListener { attachmentRemovalListener(attachment) }
        }

        override fun bind(attachment: Attachment) {
            logger.v { "[bind] isAnyFileType: ${attachment.isAnyFileType()}; $attachment" }
            this.attachment = attachment

            binding.fileNameTextView.text = attachment.title
            binding.fileThumbImageView.loadAttachmentThumb(attachment)
            binding.fileSizeTextView.text = MediaStringUtil.convertFileSizeByteCount(attachment.fileSize.toLong())
        }
    }
}
