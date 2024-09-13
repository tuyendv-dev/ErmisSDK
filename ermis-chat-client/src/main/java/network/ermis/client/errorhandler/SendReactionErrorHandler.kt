package network.ermis.client.errorhandler

import network.ermis.core.models.Reaction
import network.ermis.core.models.User
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall

public interface SendReactionErrorHandler {

    /**
     * Returns a [Result] from this side effect when original request is failed.
     *
     * @param originalCall The original call.
     * @param reaction The [Reaction] to send.
     * @param enforceUnique Flag to determine whether the reaction should replace other ones added by the current user.
     * @param currentUser The currently logged in user.
     *
     * @return result The replacement for the original result.
     */
    public fun onSendReactionError(
        originalCall: Call<Reaction>,
        reaction: Reaction,
        enforceUnique: Boolean,
        currentUser: User,
    ): ReturnOnErrorCall<Reaction>
}

internal fun Call<Reaction>.onReactionError(
    errorHandlers: List<SendReactionErrorHandler>,
    reaction: Reaction,
    enforceUnique: Boolean,
    currentUser: User,
): Call<Reaction> {
    return errorHandlers.fold(this) { originalCall, errorHandler ->
        errorHandler.onSendReactionError(originalCall, reaction, enforceUnique, currentUser)
    }
}
