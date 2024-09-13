package network.ermis.client.attachment

import network.ermis.core.models.Attachment
import network.ermis.core.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal object AttachmentsUploadStates {

    private var messagesStates: Map<String, MutableStateFlow<List<Attachment>>> = emptyMap()

    fun updateMessageAttachments(message: Message) {
        synchronized(this) {
            val attachmentsStateFlow = messagesStates.getOrElse(message.id) { MutableStateFlow(message.attachments) }
            attachmentsStateFlow.value = message.attachments
            messagesStates = messagesStates + (message.id to attachmentsStateFlow)
        }
    }

    fun observeAttachments(messageId: String): Flow<List<Attachment>> {
        synchronized(this) {
            return messagesStates.getOrElse(messageId) { MutableStateFlow(emptyList()) }
        }
    }

    fun removeMessageAttachmentsState(messageId: String) {
        synchronized(this) {
            messagesStates = messagesStates - messageId
        }
    }

    fun clearStates() {
        synchronized(this) {
            messagesStates = emptyMap()
        }
    }
}
