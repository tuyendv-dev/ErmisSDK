package network.ermis.offline.plugin.listener

import io.getstream.result.Result
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.plugin.listeners.EditMessageListener
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.extensions.internal.users
import network.ermis.client.utils.extensions.updateFailedMessage
import network.ermis.client.utils.extensions.updateMessageOnlineState
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus

/**
 * Implementation of [EditMessageListener] that deals with database read and write.
 *
 * @param userRepository [UserRepository]
 * @param messageRepository [MessageRepository]
 * @param clientState [ClientState]
 */
internal class EditMessageListenerDatabase(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val clientState: ClientState,
) : network.ermis.client.plugin.listeners.EditMessageListener {

    /**
     * Method called when a message edit request happens. This method should be used to update the database.
     *
     * @param message [Message].
     */
    override suspend fun onMessageEditRequest(message: Message) {
        val isOnline = clientState.isNetworkAvailable
        val messagesToEdit = message.updateMessageOnlineState(isOnline)

        saveMessage(messagesToEdit)
    }

    /**
     * Method called when an edition in a message returns from the API. Updates the database accordingly.
     *
     * @param originalMessage [Message].
     * @param result the result of the API call.
     */
    override suspend fun onMessageEditResult(originalMessage: Message, result: Result<Message>) {
        val parsedMessage = when (result) {
            is Result.Success -> result.value.copy(syncStatus = SyncStatus.COMPLETED)
            is Result.Failure -> originalMessage.updateFailedMessage(result.value)
        }

        saveMessage(parsedMessage)
    }

    private suspend fun saveMessage(message: Message) {
        userRepository.insertUsers(message.users())
        messageRepository.insertMessage(message)
    }
}
