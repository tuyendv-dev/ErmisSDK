package network.ermis.client.plugin.listeners

import network.ermis.core.models.Message
import io.getstream.result.Error
import io.getstream.result.Result

/**
 * Listener used when fetching a single new message from the backend.
 */
public interface GetMessageListener {

    /**
     * Method called when the API call requesting a single new message has completed.
     *
     * Use it to update the database accordingly.
     *
     * @param messageId The ID of the message we are fetching.
     * @param result The result of the API call. Will contain an instance of [Message] wrapped inside [Result] if
     * the request was successful, or an instance of [Error] if the request had failed.
     */
    public suspend fun onGetMessageResult(
        messageId: String,
        result: Result<Message>,
    )
}
