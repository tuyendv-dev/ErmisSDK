package network.ermis.offline.plugin.listener

import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.persistance.MessageRepository
import network.ermis.client.persistance.ReactionRepository
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.extensions.internal.removeMyReaction
import network.ermis.client.utils.extensions.internal.updateSyncStatus
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.SyncStatus
import network.ermis.core.models.User
import java.util.Date

internal class DeleteReactionListenerDatabase(
    private val clientState: ClientState,
    private val reactionsRepository: ReactionRepository,
    private val messageRepository: MessageRepository,
) : network.ermis.client.plugin.listeners.DeleteReactionListener {

    /**
     * A method called before making an API call to delete the reaction.
     * Creates the reaction based on [messageId] and [reactionType], updates reactions' database
     * and runs optimistic update if [cid] is specified.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param messageId The id of the message to which reaction belongs.
     * @param reactionType The type of reaction.
     * @param currentUser The currently logged in user.
     */
    override suspend fun onDeleteReactionRequest(
        cid: String?,
        messageId: String,
        reactionType: String,
        currentUser: User,
    ) {
        val reaction = Reaction(
            messageId = messageId,
            type = reactionType,
            user = currentUser,
            userId = currentUser.id,
            syncStatus = if (clientState.isNetworkAvailable) SyncStatus.IN_PROGRESS else SyncStatus.SYNC_NEEDED,
            deletedAt = Date(),
        )

        reactionsRepository.insertReaction(reaction)

        messageRepository.selectMessage(messageId = messageId)?.copy()?.let { cachedMessage ->
            messageRepository.insertMessage(cachedMessage.removeMyReaction(reaction))
        }
    }

    /**
     * A method called after receiving the response from the delete reaction call.
     * Updates reaction's sync status stored in the database based on API result.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param messageId The id of the message to which reaction belongs.
     * @param reactionType The type of reaction.
     * @param currentUser The currently logged in user.
     * @param result The API call result.
     */
    override suspend fun onDeleteReactionResult(
        cid: String?,
        messageId: String,
        reactionType: String,
        currentUser: User,
        result: Result<Message>,
    ) {
        reactionsRepository.selectUserReactionToMessage(
            reactionType = reactionType,
            messageId = messageId,
            userId = currentUser.id,
        )?.let { cachedReaction ->
            reactionsRepository.insertReaction(cachedReaction.updateSyncStatus(result))
        }
    }

    /**
     * Checks if current user is set.
     *
     * @param currentUser The currently logged in user.
     */
    override fun onDeleteReactionPrecondition(currentUser: User?): Result<Unit> {
        return if (currentUser != null) {
            Result.Success(Unit)
        } else {
            Result.Failure(Error.GenericError(message = "Current user is null!"))
        }
    }
}
