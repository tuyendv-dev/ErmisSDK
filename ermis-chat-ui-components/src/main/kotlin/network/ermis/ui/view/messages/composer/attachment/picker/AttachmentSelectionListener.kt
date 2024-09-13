
package network.ermis.ui.view.messages.composer.attachment.picker

import network.ermis.ui.common.state.messages.composer.AttachmentMetaData

@Suppress("MaxLineLength")
@Deprecated(
    message = "Use the new [AttachmentsPickerDialogFragment.AttachmentsSelectionListener] interface instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(
        "AttachmentsPickerDialogFragment.AttachmentsSelectionListener",
        "network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogFragment.AttachmentsSelectionListener",
    ),
)
public fun interface AttachmentSelectionListener {
    /**
     * Called when attachment picking has been completed.
     *
     * @param attachments The list of selected attachments.
     */
    public fun onAttachmentsSelected(attachments: List<AttachmentMetaData>)
}
