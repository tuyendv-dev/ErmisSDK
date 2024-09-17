package network.ermis.state.errorhandler.internal

import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall
import io.getstream.result.call.onErrorReturn
import kotlinx.coroutines.CoroutineScope
import network.ermis.client.errorhandler.SendReactionErrorHandler
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.extensions.internal.enrichWithDataBeforeSending
import network.ermis.core.models.Reaction
import network.ermis.core.models.User

/**
 * Returns a [Reaction] instance enriched with user [Reaction.syncStatus] if reaction was send offline and can be synced.
 *
 * @param scope [CoroutineScope]
 */
internal class SendReactionErrorHandlerImpl(
    private val scope: CoroutineScope,
    private val clientState: ClientState,
) : SendReactionErrorHandler {

    /**
     * Replaces the original response error if the user is offline.
     * This means that the reaction was added locally but the API request failed due to lack of connection.
     * The request will be synced once user's connection is recovered.
     *
     * @param originalCall The original call.
     * @param reaction The [Reaction] to send.
     * @param enforceUnique Flag to determine whether the reaction should replace other ones added by the current user.
     * @param currentUser The currently logged in user.
     *
     * @return result The original or offline related result.
     */
    override fun onSendReactionError(
        originalCall: Call<Reaction>,
        reaction: Reaction,
        enforceUnique: Boolean,
        currentUser: User,
    ): ReturnOnErrorCall<Reaction> {
        return originalCall.onErrorReturn(scope) { originalError ->
            if (clientState.isOnline) {
                Result.Failure(originalError)
            } else {
                Result.Success(
                    reaction.enrichWithDataBeforeSending(
                        currentUser = currentUser,
                        isOnline = clientState.isOnline,
                        enforceUnique = enforceUnique,
                    ),
                )
            }
        }
    }
}
