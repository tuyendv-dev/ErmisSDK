package network.ermis.offline.plugin.listener

import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.MessageRepository
import network.ermis.core.models.Message

/**
 * Updates the database of the SDK accordingly with the request to send the attachments to the backend.
 */
internal class SendAttachmentsListenerDatabase(
    private val messageRepository: MessageRepository,
    private val channelRepository: ChannelRepository,
) : network.ermis.client.plugin.listeners.SendAttachmentListener {

    /**
     * Update the database of the SDK before the attachments are sent to the backend.
     *
     * @param channelType String
     * @param channelId String
     * @param message [Message]
     */
    override suspend fun onAttachmentSendRequest(channelType: String, channelId: String, message: Message) {
        // we insert early to ensure we don't lose messages
        messageRepository.insertMessage(message)
        channelRepository.updateLastMessageForChannel(message.cid, message)
    }
}
