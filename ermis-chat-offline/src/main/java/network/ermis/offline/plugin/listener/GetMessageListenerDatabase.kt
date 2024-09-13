package network.ermis.offline.plugin.listener

import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.plugin.listeners.GetMessageListener
import network.ermis.core.models.Message
import io.getstream.log.StreamLog
import io.getstream.result.Error
import io.getstream.result.Result

/**
 * An implementation of [GetMessageListener] used to perform database operations when making an API call
 * that fetches a single message from the backend.
 *
 * @param repositoryFacade A class that holds a collection of repositories used by the SDK and exposes
 * various repository operations as methods.
 */
internal class GetMessageListenerDatabase(
    private val repositoryFacade: RepositoryFacade,
) : network.ermis.client.plugin.listeners.GetMessageListener {

    /**
     * Stream Logger using the class name as tag in order to make tracking operations easier.
     */
    private val logger = StreamLog.getLogger("Chat: GetMessageListenerDatabase")

    /**
     * Inserts the message into the database if the API call had been successful, otherwise logs the error.
     *
     * @param messageId The ID of the message we are fetching.
     * @param result The result of the API call. Will contain an instance of [Message] wrapped inside [Result] if
     * the request was successful, or an instance of [Error] if the request had failed.
     */
    override suspend fun onGetMessageResult(
        messageId: String,
        result: Result<Message>,
    ) {
        when (result) {
            is Result.Success -> {
                repositoryFacade.insertMessage(
                    message = result.value,
                )
            }
            is Result.Failure -> {
                val error = result.value
                logger.e {
                    "[onGetMessageResult] Could not insert the message into the database. The API call " +
                        "had failed with: ${error.message}"
                }
            }
        }
    }
}
