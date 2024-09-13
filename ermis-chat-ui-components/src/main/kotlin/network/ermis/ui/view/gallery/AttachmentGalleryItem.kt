
package network.ermis.ui.view.gallery

import network.ermis.core.models.Attachment
import network.ermis.core.models.User
import java.util.Date

public data class AttachmentGalleryItem(
    val attachment: Attachment,
    val user: User?,
    val createdAt: Date,
    val messageId: String,
    val cid: String,
    val isMine: Boolean,
)
