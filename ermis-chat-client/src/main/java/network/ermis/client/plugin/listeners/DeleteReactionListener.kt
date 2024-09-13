package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.core.models.Message
import network.ermis.core.models.User
import io.getstream.result.Result

public interface DeleteReactionListener {

    /**
     * A method called before making an API call to delete the reaction.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param messageId The id of the message to which reaction belongs.
     * @param reactionType The type of reaction.
     * @param currentUser The currently logged in user.
     */
    public suspend fun onDeleteReactionRequest(
        cid: String?,
        messageId: String,
        reactionType: String,
        currentUser: User,
    )

    /**
     * A method called after receiving the response from the delete reaction call.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param messageId The id of the message to which reaction belongs.
     * @param reactionType The type of reaction.
     * @param currentUser The currently logged in user.
     * @param result The API call result.
     */
    public suspend fun onDeleteReactionResult(
        cid: String?,
        messageId: String,
        reactionType: String,
        currentUser: User,
        result: Result<Message>,
    )

    /**
     * Runs precondition check for [ErmisClient.deleteReaction].
     * The request will be run if the method returns [Result.Success] and won't be made if it returns [Result.Failure].
     *
     * @param currentUser The currently logged in user.
     *
     * @return [Result.Success] if the precondition is fulfilled, [Result.Failure] otherwise.
     */
    public fun onDeleteReactionPrecondition(currentUser: User?): Result<Unit>
}
