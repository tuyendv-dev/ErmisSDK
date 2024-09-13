package network.ermis.state.errorhandler.internal

import network.ermis.client.errorhandler.DeleteReactionErrorHandler
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Message
import network.ermis.state.plugin.logic.LogicRegistry
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall
import io.getstream.result.call.onErrorReturn
import kotlinx.coroutines.CoroutineScope

/**
 * Checks if the change was done offline and can be synced.
 *
 * @param scope [CoroutineScope]
 * @param logic [LogicRegistry]
 * @param clientState [ClientState]
 */
internal class DeleteReactionErrorHandlerImpl(
    private val scope: CoroutineScope,
    private val logic: LogicRegistry,
    private val clientState: ClientState,
) : DeleteReactionErrorHandler {

    /**
     * Replaces the original response error if the user is offline, [cid] is specified and the message exists in the cache.
     * This means that the message was updated locally but the API request failed due to lack of connection.
     * The request will be synced once user's connection is recovered.
     *
     * @param originalCall The original call.
     * @param cid The full channel id, i.e. "messaging:123".
     * @param messageId The id of the message to which reaction belongs.
     *
     * @return result The original or offline related result.
     */
    override fun onDeleteReactionError(
        originalCall: Call<Message>,
        cid: String?,
        messageId: String,
    ): ReturnOnErrorCall<Message> {
        return originalCall.onErrorReturn(scope) { originalError ->
            if (cid == null || clientState.isOnline) {
                Result.Failure(originalError)
            } else {
                val (channelType, channelId) = cid.cidToTypeAndId()
                val cachedMessage =
                    logic.channel(channelType = channelType, channelId = channelId).getMessage(messageId)

                if (cachedMessage != null) {
                    Result.Success(cachedMessage)
                } else {
                    Result.Failure(Error.GenericError(message = "Local message was not found."))
                }
            }
        }
    }
}
