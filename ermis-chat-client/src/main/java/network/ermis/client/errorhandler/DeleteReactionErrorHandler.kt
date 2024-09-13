package network.ermis.client.errorhandler

import network.ermis.core.models.Message
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall

public interface DeleteReactionErrorHandler {

    /**
     * Returns a [Result] from this side effect when original request is failed.
     *
     * @param originalCall The original call.
     * @param cid The full channel id, i.e. "messaging:123".
     * @param messageId The id of the message to which reaction belongs.
     *
     * @return result The replacement for the original result.
     */
    public fun onDeleteReactionError(
        originalCall: Call<Message>,
        cid: String?,
        messageId: String,
    ): ReturnOnErrorCall<Message>
}

internal fun Call<Message>.onMessageError(
    errorHandlers: List<DeleteReactionErrorHandler>,
    cid: String?,
    messageId: String,
): Call<Message> {
    return errorHandlers.fold(this) { messageCall, errorHandler ->
        errorHandler.onDeleteReactionError(messageCall, cid, messageId)
    }
}
