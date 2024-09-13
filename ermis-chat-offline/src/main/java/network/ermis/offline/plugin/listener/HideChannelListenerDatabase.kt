package network.ermis.offline.plugin.listener

import network.ermis.client.utils.extensions.internal.toCid
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.plugin.listeners.HideChannelListener
import network.ermis.client.utils.internal.validateCidWithResult
import io.getstream.result.Result
import java.util.Date

/**
 * Implementation of [HideChannelListener] that deals with database read and write.
 *
 * @param channelRepository [ChannelRepository]
 * @param messageRepository [MessageRepository]
 */
internal class HideChannelListenerDatabase(
    private val channelRepository: ChannelRepository,
    private val messageRepository: MessageRepository,
) : network.ermis.client.plugin.listeners.HideChannelListener {

    /**
     * Run precondition for the request. If it returns [Result.Success] then the request is run otherwise it returns
     * [Result.Failure] and no request is made.
     *
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     *
     * @return [Result.Success] if precondition passes otherwise [Result.Failure]
     */
    override suspend fun onHideChannelPrecondition(
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ): Result<Unit> = validateCidWithResult(Pair(channelType, channelId).toCid()).toUnitResult()

    /**
     * Runs side effect before the request is launched.
     *
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     */
    override suspend fun onHideChannelRequest(channelType: String, channelId: String, clearHistory: Boolean) {
        // Nothing to do.
    }

    /**
     * Runs this function on the result of the request.
     *
     * @param result Result of this request.
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     */
    override suspend fun onHideChannelResult(
        result: Result<Unit>,
        channelType: String,
        channelId: String,
        clearHistory: Boolean,
    ) {
        if (result is Result.Success) {
            val cid = Pair(channelType, channelId).toCid()

            if (clearHistory) {
                val now = Date()
                channelRepository.setHiddenForChannel(cid, true, now)
                messageRepository.deleteChannelMessagesBefore(cid, now)
            } else {
                channelRepository.setHiddenForChannel(cid, true)
            }
        }
    }
}
