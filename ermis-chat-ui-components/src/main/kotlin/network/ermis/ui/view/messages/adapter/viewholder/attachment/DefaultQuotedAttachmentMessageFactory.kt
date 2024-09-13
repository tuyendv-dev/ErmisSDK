
package network.ermis.ui.view.messages.adapter.viewholder.attachment

import android.view.View
import android.view.ViewGroup
import network.ermis.client.utils.attachment.isAudioRecording
import network.ermis.client.utils.attachment.isFile
import network.ermis.client.utils.attachment.isGiphy
import network.ermis.client.utils.attachment.isImage
import network.ermis.client.utils.attachment.isVideo
import network.ermis.core.models.Message
import network.ermis.ui.view.messages.adapter.view.internal.DefaultQuotedAttachmentView

/**
 * Factory for attachments we support by default.
 */
public class DefaultQuotedAttachmentMessageFactory : QuotedAttachmentFactory {

    /**
     * @param message The quoted message with the attachments we wish to render.
     *
     * @return If the factory can handle the given quoted message attachment or not.
     */
    override fun canHandle(message: Message): Boolean {
        val attachment = message.attachments.firstOrNull() ?: return false

        return attachment.isFile() ||
            attachment.isImage() ||
            attachment.isGiphy() ||
            attachment.isVideo() ||
            attachment.isAudioRecording()
    }

    /**
     * Generates a [DefaultQuotedAttachmentView] to render the attachment.
     *
     * @param message The quoted message holding the attachments.
     * @param parent The parent [ViewGroup] in which the attachment will be rendered.
     *
     * @return [DefaultQuotedAttachmentView] that will be rendered inside the quoted message.
     */
    override fun generateQuotedAttachmentView(message: Message, parent: ViewGroup): View {
        return DefaultQuotedAttachmentView(parent.context).apply {
            showAttachment(message.attachments.first())
        }
    }
}
