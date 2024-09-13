
package network.ermis.ui.view.messages.adapter.viewholder.attachment

import android.view.ViewGroup
import network.ermis.core.models.Message
import network.ermis.ui.ChatUI

/**
 * A manager for registered quoted attachment factories.
 */
public class QuotedAttachmentFactoryManager(
    private val quotedAttachmentFactories: List<QuotedAttachmentFactory> = listOf(),
) {
    /**
     * Checks if any [QuotedAttachmentFactory] can consume attachments from the given message. If there are no
     * quoted message factories that can handle the attachment will default to the [AttachmentFactory]es that can.
     *
     * @param message The quoted message containing attachments that we are going to render.
     * @return True if there is a factory that can handle the attachments from this quoted message.
     */
    public fun canHandle(message: Message): Boolean {
        return quotedAttachmentFactories.any { it.canHandle(message) } ||
            ChatUI.attachmentFactoryManager.canHandle(message)
    }

    /**
     * Create and add a view for the quoted attachments.
     *
     * @param message The message containing attachments that we are going to render.
     * @param parent The parent View where the attachment content view is supposed to be placed.
     */
    public fun createAndAddQuotedView(
        message: Message,
        parent: ViewGroup,
    ) {
        val quotedAttachmentFactory =
            quotedAttachmentFactories.firstOrNull { it.canHandle(message) } ?: FallbackQuotedAttachmentMessageFactory()
        val view = quotedAttachmentFactory.generateQuotedAttachmentView(message, parent)

        parent.removeAllViews()
        parent.addView(view)
    }
}
