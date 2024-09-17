package network.ermis.state.listener

import network.ermis.core.models.Message
import network.ermis.state.plugin.logic.LogicRegistry

/**
 * Updates the state of the SDK accordingly with request to send attachments to backend.
 */
internal class SendAttachmentListenerState(private val logic: LogicRegistry) :
    network.ermis.client.plugin.listeners.SendAttachmentListener {

    /**
     * Update the state of the SDK before the attachments are sent to the backend.
     *
     * @param channelType String
     * @param channelId String
     * @param message [Message]
     */
    override suspend fun onAttachmentSendRequest(channelType: String, channelId: String, message: Message) {
        val channel = logic.channel(channelType, channelId)

        channel.upsertMessage(message)
        logic.threadFromMessage(message)?.upsertMessage(message)

        // Update flow for currently running queries
        logic.getActiveQueryChannelsLogic().forEach { query -> query.refreshChannelState(channel.cid) }
    }
}
