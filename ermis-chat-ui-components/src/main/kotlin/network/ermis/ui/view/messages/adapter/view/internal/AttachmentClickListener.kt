
package network.ermis.ui.view.messages.adapter.view.internal

import network.ermis.core.models.Attachment

internal fun interface AttachmentClickListener {
    fun onAttachmentClick(attachment: Attachment)
}

internal fun interface AttachmentLongClickListener {
    fun onAttachmentLongClick()
}

internal fun interface AttachmentDownloadClickListener {
    fun onAttachmentDownloadClick(attachment: Attachment)
}
