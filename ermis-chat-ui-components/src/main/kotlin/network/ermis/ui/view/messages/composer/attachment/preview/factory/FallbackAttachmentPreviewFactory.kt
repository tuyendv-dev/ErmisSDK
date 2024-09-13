
package network.ermis.ui.view.messages.composer.attachment.preview.factory

import android.view.ViewGroup
import network.ermis.client.utils.attachment.isAudioRecording
import network.ermis.core.models.Attachment
import network.ermis.ui.components.databinding.UnsupportedAttachmentPreviewBinding
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.view.messages.composer.attachment.preview.AttachmentPreviewViewHolder
import network.ermis.ui.utils.extensions.streamThemeInflater
import io.getstream.log.taggedLogger

/**
 * A fallback [AttachmentPreviewFactory] for attachments unhandled by other factories.
 */
public class FallbackAttachmentPreviewFactory : AttachmentPreviewFactory {

    private val logger by taggedLogger("AttachFallbackPreviewFactory")

    /**
     * Checks if the factory can create a preview ViewHolder for this attachment.
     *
     * @param attachment The attachment we want to show a preview for.
     * @return True if the factory is able to provide a preview for the given [Attachment].
     */
    override fun canHandle(attachment: Attachment): Boolean {
        logger.i { "[canHandle] isAudioRecording: ${attachment.isAudioRecording()}; $attachment" }
        return true
    }

    /**
     * Creates and instantiates a new instance of [FallbackAttachmentPreviewViewHolder].
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
        return UnsupportedAttachmentPreviewBinding
            .inflate(parentView.context.streamThemeInflater, parentView, false)
            .let { binding ->
                FallbackAttachmentPreviewViewHolder(binding, attachmentRemovalListener)
            }
    }

    /**
     * An empty ViewHolder as we don't display unsupported attachment types.
     *
     * @param binding [UnsupportedAttachmentPreviewBinding] generated for the layout.
     * @param attachmentRemovalListener Click listener for the remove attachment button.
     */
    private class FallbackAttachmentPreviewViewHolder(
        private val binding: UnsupportedAttachmentPreviewBinding,
        private val attachmentRemovalListener: (Attachment) -> Unit,
    ) : AttachmentPreviewViewHolder(binding.root) {
        override fun bind(attachment: Attachment) {
            binding.titleImageView.text = attachment.title
            binding.removeButton.setOnClickListener { attachmentRemovalListener(attachment) }
        }
    }
}
