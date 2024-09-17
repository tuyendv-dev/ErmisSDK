package network.ermis.offline.plugin.listener

import io.getstream.result.Result
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.utils.extensions.internal.users
import network.ermis.core.models.Message

/**
 * ThreadQueryListenerFull handles database read and updates. It updates the database once the requests for backend
 * is complete.
 *
 * @param messageRepository [MessageRepository] Optional to handle database updates related to messages
 * @param userRepository [UserRepository]  Optional to handle database updates related to user
 */
internal class ThreadQueryListenerDatabase(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
) : network.ermis.client.plugin.listeners.ThreadQueryListener {

    override suspend fun onGetRepliesRequest(messageId: String, limit: Int) {
        // Nothing to do.
    }

    override suspend fun onGetRepliesResult(result: Result<List<Message>>, messageId: String, limit: Int) {
        onResult(result)
    }

    override suspend fun onGetRepliesMoreRequest(messageId: String, firstId: String, limit: Int) {
        // Nothing to do.
    }

    override suspend fun onGetRepliesMoreResult(
        result: Result<List<Message>>,
        messageId: String,
        firstId: String,
        limit: Int,
    ) {
        onResult(result)
    }

    private suspend fun onResult(result: Result<List<Message>>) {
        if (result is Result.Success) {
            val messages = result.value

            userRepository.insertUsers(messages.flatMap(Message::users))
            messageRepository.insertMessages(messages)
        }
    }
}
