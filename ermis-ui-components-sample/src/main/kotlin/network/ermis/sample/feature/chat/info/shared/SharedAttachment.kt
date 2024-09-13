
package network.ermis.sample.feature.chat.info.shared

import network.ermis.client.api.model.response.AttachmentGet
import network.ermis.core.models.Attachment
import network.ermis.core.models.Message
import java.util.Date

sealed class SharedAttachment {

    val id: String
        get() = when (this) {
            is AttachmentItem -> "${message.id}-${attachment.name}"
            is AttachmentGetItem -> "${attachmentGet.message_id}-${attachmentGet.id}"
            is DateDivider -> date.toString()
        }

    data class AttachmentItem(val message: Message, val createdAt: Date, val attachment: Attachment) : SharedAttachment()
    data class DateDivider(val date: Date) : SharedAttachment()

    data class AttachmentGetItem(val attachmentGet: AttachmentGet) : SharedAttachment()
}
