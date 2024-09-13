package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.core.models.Message
import io.getstream.result.Result

/**
 * Listener for requests of message deletion and for message deletion results.
 */
public interface DeleteMessageListener {

    /**
     * Runs precondition check for [ErmisClient.deleteMessage].
     * The request will be run if the method returns [Result.Success] and won't be made if it returns [Result.Failure].
     *
     * @param messageId The message id to be deleted.
     *
     * @return [Result.Success] if the precondition is fulfilled, [Result.Failure] otherwise.
     */
    public suspend fun onMessageDeletePrecondition(messageId: String): Result<Unit>

    /**
     * Method called when a request to delete a message in the API happens
     *
     * @param messageId
     */
    public suspend fun onMessageDeleteRequest(messageId: String)

    /**
     * Method called when a request for message deletion return. Use it to update database, update messages or to
     * present an error to the user.
     *
     * @param result the result of the API call.
     */
    public suspend fun onMessageDeleteResult(originalMessageId: String, result: Result<Message>)
}
