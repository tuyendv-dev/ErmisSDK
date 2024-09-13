package network.ermis.client.persistance.noop

import network.ermis.client.persistance.ReactionRepository
import network.ermis.core.models.Reaction
import network.ermis.core.models.SyncStatus
import java.util.Date

/**
 * No-Op ReactionRepository.
 */
internal object NoOpReactionRepository : ReactionRepository {
    override suspend fun insertReaction(reaction: Reaction) { /* No-Op */ }
    override suspend fun selectReactionById(id: Int): Reaction? = null
    override suspend fun selectReactionsByIds(ids: List<Int>): List<Reaction> = emptyList()
    override suspend fun selectReactionIdsBySyncStatus(syncStatus: SyncStatus): List<Int> = emptyList()
    override suspend fun selectReactionsBySyncStatus(syncStatus: SyncStatus): List<Reaction> = emptyList()
    override suspend fun deleteReaction(reaction: Reaction) { /* No-Op */ }
    override suspend fun clear() { /* No-Op */ }

    override suspend fun updateReactionsForMessageByDeletedDate(
        userId: String,
        messageId: String,
        deletedAt: Date,
    ) { /* No-Op */ }

    override suspend fun selectUserReactionToMessage(
        reactionType: String,
        messageId: String,
        userId: String,
    ): Reaction? = null

    override suspend fun selectUserReactionsToMessage(
        messageId: String,
        userId: String,
    ): List<Reaction> = emptyList()
}
