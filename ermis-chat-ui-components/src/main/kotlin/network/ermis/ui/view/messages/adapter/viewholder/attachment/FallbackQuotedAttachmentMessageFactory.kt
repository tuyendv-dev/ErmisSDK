
package network.ermis.ui.view.messages.adapter.viewholder.attachment

import android.view.View
import android.view.ViewGroup
import network.ermis.core.models.Message
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.adapter.view.internal.DefaultQuotedAttachmentView

/**
 * Factory for attachments that the SDK falls back when all the other [QuotedAttachmentFactory] don't support the
 * attachment type. It simply shows a file icon with the attachment title.
 */
public class FallbackQuotedAttachmentMessageFactory : QuotedAttachmentFactory {

    /**
     * @param message The quoted message with the attachments we wish to render.
     *
     * @return If the factory can handle the given quoted message attachment or not.
     */
    override fun canHandle(message: Message): Boolean = true

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
            setImageResource(R.drawable.ic_file)
        }
    }
}
