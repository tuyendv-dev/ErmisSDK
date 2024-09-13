
package network.ermis.ui.view.messages.composer.attachment.preview.factory

import android.view.ViewGroup
import network.ermis.core.models.Attachment
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.view.messages.composer.attachment.preview.AttachmentPreviewViewHolder

/**
 * A factory responsible for creating attachment preview ViewHolders.
 */
public interface AttachmentPreviewFactory {
    /**
     * Checks if the factory can create a preview ViewHolder for this attachment.
     *
     * @param attachment The attachment we want to show a preview for.
     * @return True if the factory is able to provide a preview for the given [Attachment].
     */
    public fun canHandle(attachment: Attachment): Boolean

    /**
     * Creates and instantiates a new instance of [AttachmentPreviewViewHolder].
     *
     * @param parentView The parent container.
     * @param attachmentRemovalListener Click listener for the remove attachment button.
     * @param style Used to style the factory. If null, the factory will retain
     * the default appearance.
     *
     * @return An instance of attachment preview ViewHolder.
     */
    public fun onCreateViewHolder(
        parentView: ViewGroup,
        attachmentRemovalListener: (Attachment) -> Unit,
        style: MessageComposerViewStyle? = null,
    ): AttachmentPreviewViewHolder
}
