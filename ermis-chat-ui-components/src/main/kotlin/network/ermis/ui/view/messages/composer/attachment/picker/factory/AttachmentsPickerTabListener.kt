
package network.ermis.ui.view.messages.composer.attachment.picker.factory

import network.ermis.ui.common.state.messages.composer.AttachmentMetaData

/**
 * Listener invoked when attachments are selected in the attachment tab.
 */
public interface AttachmentsPickerTabListener {

    /**
     * Called when the list of currently selected attachments changes.
     *
     * @param attachments The list of currently selected attachments.
     */
    public fun onSelectedAttachmentsChanged(attachments: List<AttachmentMetaData>)

    /**
     * Called when selected attachments need to be submitted to the message input.
     */
    public fun onSelectedAttachmentsSubmitted()
}
