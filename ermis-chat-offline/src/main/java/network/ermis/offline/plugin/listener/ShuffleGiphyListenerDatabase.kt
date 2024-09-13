package network.ermis.offline.plugin.listener

import network.ermis.client.utils.extensions.internal.users
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.plugin.listeners.ShuffleGiphyListener
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import io.getstream.result.Result

/**
* [ShuffleGiphyListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
* Handles updating the database.
*
* @param userRepository [UserRepository]
* @param messageRepository [MessageRepository]
*/
internal class ShuffleGiphyListenerDatabase(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
) : network.ermis.client.plugin.listeners.ShuffleGiphyListener {

    /**
     * Added a new message to the DB if request was successful.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param result The API call result.
     */
    override suspend fun onShuffleGiphyResult(cid: String, result: Result<Message>) {
        if (result is Result.Success) {
            val processedMessage = result.value.copy(syncStatus = SyncStatus.COMPLETED)
            userRepository.insertUsers(processedMessage.users())
            messageRepository.insertMessage(processedMessage)
        }
    }
}
