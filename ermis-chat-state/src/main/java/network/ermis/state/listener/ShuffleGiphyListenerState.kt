package network.ermis.state.listener

import network.ermis.client.plugin.listeners.ShuffleGiphyListener
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import network.ermis.state.plugin.logic.LogicRegistry
import io.getstream.result.Result

/**
 * [ShuffleGiphyListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
 * Handles updating the state.
 *
 * @param logic [LogicRegistry]
 */
internal class ShuffleGiphyListenerState(private val logic: LogicRegistry) :
    network.ermis.client.plugin.listeners.ShuffleGiphyListener {

    /**
     * Added a new message to the state if request was successful.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param result The API call result.
     */
    override suspend fun onShuffleGiphyResult(cid: String, result: Result<Message>) {
        if (result is Result.Success) {
            val processedMessage = result.value.copy(syncStatus = SyncStatus.COMPLETED)
            logic.channelFromMessage(processedMessage)?.upsertMessage(processedMessage)
            logic.threadFromMessage(processedMessage)?.upsertMessage(processedMessage)
        }
    }
}
