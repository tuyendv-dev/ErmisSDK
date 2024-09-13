package network.ermis.client.persistance

import network.ermis.core.models.Reaction
import network.ermis.core.models.SyncStatus
import java.util.Date

/**
 * Repository to read and write reactions.
 */
public interface ReactionRepository {

    /**
     * Inserts a reaction.
     *
     * @param reaction [Reaction]
     */
    public suspend fun insertReaction(reaction: Reaction)

    /**
     * Updates the Reaction.deletedAt for reactions of a message.
     *
     * @param userId String.
     * @param messageId String.
     * @param deletedAt Date.
     */
    public suspend fun updateReactionsForMessageByDeletedDate(userId: String, messageId: String, deletedAt: Date)

    /**
     * Selects reaction with specified [id].
     *
     * @param id A reaction id to search for.
     */
    public suspend fun selectReactionById(id: Int): Reaction?

    /**
     * Selects all reactions with specified [ids]
     *
     * @param ids A list of reaction id to search for.
     */
    public suspend fun selectReactionsByIds(ids: List<Int>): List<Reaction>

    /**
     * Selects all reaction ids with specific [SyncStatus].
     *
     * @param syncStatus [SyncStatus]
     */
    public suspend fun selectReactionIdsBySyncStatus(syncStatus: SyncStatus): List<Int>

    /**
     * Selects all reactions with specific [SyncStatus]
     *
     * @param syncStatus [SyncStatus]
     */
    public suspend fun selectReactionsBySyncStatus(syncStatus: SyncStatus): List<Reaction>

    /**
     * Selects the reaction of given type to the message if exists.
     *
     * @param reactionType The type of reaction.
     * @param messageId The id of the message to which reaction belongs.
     * @param userId The id of the user who is the owner of reaction.
     *
     * @return [Reaction] if exists, null otherwise.
     */
    public suspend fun selectUserReactionToMessage(reactionType: String, messageId: String, userId: String): Reaction?

    /**
     * Selects all current user reactions of a message.
     *
     * @param messageId String.
     * @param userId String.
     */
    public suspend fun selectUserReactionsToMessage(
        messageId: String,
        userId: String,
    ): List<Reaction>

    /**
     * Deletes a reaction.
     *
     * @param reaction [Reaction]
     */
    public suspend fun deleteReaction(reaction: Reaction)

    /**
     * Clear reactions of this repository.
     */
    public suspend fun clear()
}
