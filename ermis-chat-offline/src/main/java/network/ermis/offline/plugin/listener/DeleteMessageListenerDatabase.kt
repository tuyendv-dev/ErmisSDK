package network.ermis.offline.plugin.listener

import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.errors.cause.MessageModerationDeletedException
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.extensions.internal.users
import network.ermis.client.utils.message.isModerationError
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import java.util.Date

/**
 * Listener for requests of message deletion and for message deletion results responsible to
 * change database.
 */
internal class DeleteMessageListenerDatabase(
    private val clientState: ClientState,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
) : network.ermis.client.plugin.listeners.DeleteMessageListener {

    /**
     * Checks if message can be safely deleted.
     *
     * @param messageId The message id to be deleted.
     */
    override suspend fun onMessageDeletePrecondition(messageId: String): Result<Unit> {
        return messageRepository.selectMessage(messageId)?.let { message ->
            val currentUserId = clientState.user.value?.id
            val isModerationFailed = message.isModerationError(currentUserId)

            if (isModerationFailed) {
                messageRepository.deleteChannelMessage(message)
                Result.Failure(
                    Error.ThrowableError(
                        message = "Message with failed moderation has been deleted locally: $messageId",
                        cause = MessageModerationDeletedException(
                            "Message with failed moderation has been deleted locally: $messageId",
                        ),
                    ),
                )
            } else {
                Result.Success(Unit)
            }
        } ?: Result.Failure(Error.GenericError(message = "No message found with id: $messageId"))
    }

    /**
     * Method called when a request to delete a message in the API happens
     *
     * @param messageId
     */
    override suspend fun onMessageDeleteRequest(messageId: String) {
        messageRepository.selectMessage(messageId)?.let { message ->
            if (message.syncStatus == SyncStatus.FAILED_PERMANENTLY) {
                // TODO nếu tin nhắn gửi lỗi, không đồng bộ được thì xoá luôn
                messageRepository.deleteChannelMessage(message)
            } else {
                val networkAvailable = clientState.isNetworkAvailable
                val messageToBeDeleted = message.copy(
                    deletedAt = Date(),
                    syncStatus = if (!networkAvailable) SyncStatus.SYNC_NEEDED else SyncStatus.IN_PROGRESS,
                )

                userRepository.insertUsers(messageToBeDeleted.users())
                messageRepository.insertMessage(messageToBeDeleted)
            }
        }
    }

    /**
     * Method called when a request for message deletion return. Use it to update database, update messages or
     * to present an error to the user.
     *
     * @param result the result of the API call.
     */
    override suspend fun onMessageDeleteResult(originalMessageId: String, result: Result<Message>) {
        when (result) {
            is Result.Success -> {
                messageRepository.insertMessage(
                    result.value.copy(syncStatus = SyncStatus.COMPLETED),
                )
            }
            is Result.Failure -> {
                messageRepository.selectMessage(originalMessageId)?.let { originalMessage ->
                    val failureMessage = originalMessage.copy(
                        syncStatus = SyncStatus.SYNC_NEEDED,
                        updatedLocallyAt = Date(),
                    )

                    messageRepository.insertMessage(failureMessage)
                }
            }
        }
    }
}
