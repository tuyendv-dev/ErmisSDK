package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.core.models.Message
import io.getstream.result.Result

/**
 * Listener for [ErmisClient.sendMessage] requests.
 */
public interface SendMessageListener {

    /**
     * Side effect to be invoked when the original request is completed with a response.
     *
     * @param result [Result] response from the original request.
     * @param channelType The type of the channel in which message is sent.
     * @param channelId The id of the the channel in which message is sent.
     * @param message Message to be sent.
     */
    public suspend fun onMessageSendResult(
        result: Result<Message>,
        channelType: String,
        channelId: String,
        message: Message,
    )
}
