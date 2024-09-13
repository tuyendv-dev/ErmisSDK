package network.ermis.state.listener

import network.ermis.client.utils.extensions.internal.toCid
import network.ermis.client.plugin.listeners.HideChannelListener
import network.ermis.client.utils.internal.validateCidWithResult
import network.ermis.state.plugin.logic.LogicRegistry
import io.getstream.result.Result
import java.util.Date

/**
 * Implementation of [HideChannelListener] that handles read and write of the state of the SDK.
 *
 * @param logic [LogicRegistry]
 */
internal class HideChannelListenerState(private val logic: LogicRegistry) :
    network.ermis.client.plugin.listeners.HideChannelListener {

    /**
     * Run precondition for the request. If it returns [Result.isSuccess] then the request is run otherwise it returns
     * [Result.error] and no request is made.
     *
     * @param channelType Type of the requested channel.
     * @param channelId Id of the requested channel.
     * @param clearHistory Boolean, if you want to clear the history of this channel or not.
     *
     * @return [Result.success] if precondition passes otherwise [Result.error]
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
        logic.channel(channelType, channelId).stateLogic().toggleHidden(true)
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
        val channelStateLogic = logic.channel(channelType, channelId).stateLogic()
        when (result) {
            is Result.Success -> {
                if (clearHistory) {
                    val now = Date()
                    channelStateLogic.run {
                        hideMessagesBefore(now)
                        removeMessagesBefore(now)
                    }
                }
            }
            is Result.Failure -> channelStateLogic.toggleHidden(false)
        }
    }
}
