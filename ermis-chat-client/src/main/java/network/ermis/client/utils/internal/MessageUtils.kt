package network.ermis.client.utils.internal

import network.ermis.client.utils.extensions.internal.hasPendingAttachments
import network.ermis.core.models.Message
import java.util.regex.Pattern

private val COMMAND_PATTERN = Pattern.compile("^/[a-z]*$")

// TODO: type should be a sealed/class or enum at the client level
public fun getMessageType(message: Message): String {
    val hasAttachments = message.attachments.isNotEmpty()
    val hasAttachmentsToUpload = message.hasPendingAttachments()

    return if (COMMAND_PATTERN.matcher(message.text).find() || (hasAttachments && hasAttachmentsToUpload)) {
        Message.TYPE_EPHEMERAL
    } else {
        Message.TYPE_REGULAR
    }
}
