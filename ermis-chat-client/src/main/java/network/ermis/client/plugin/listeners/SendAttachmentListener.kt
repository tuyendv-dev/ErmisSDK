package network.ermis.client.plugin.listeners

import network.ermis.core.models.Message

/**
 * Listener that updates the SDK accordingly with the request to send attachments to the backend.
 */
public interface SendAttachmentListener {

    /**
     * Updates the SDK before the attachments are sent to backend. It can be used to update the
     * database with the message whose attachments are going to be sent or to change the state
     * of the messages that are presented to the end user.
     *
     * @param channelType String
     * @param channelId String
     * @param message [Message]
     */
    public suspend fun onAttachmentSendRequest(
        channelType: String,
        channelId: String,
        message: Message,
    )
}
