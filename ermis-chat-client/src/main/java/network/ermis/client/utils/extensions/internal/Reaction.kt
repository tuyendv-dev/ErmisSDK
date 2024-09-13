package network.ermis.client.utils.extensions.internal

import network.ermis.core.errors.isPermanent
import network.ermis.core.models.Reaction
import network.ermis.core.models.SyncStatus
import network.ermis.core.models.User
import io.getstream.result.Error
import io.getstream.result.Result

/** Updates collection of reactions with more recent data of [users]. */
public fun Collection<Reaction>.updateByUsers(userMap: Map<String, User>): Collection<Reaction> =
    if (mapNotNull { it.user?.id }.any(userMap::containsKey)) {
        map { reaction ->
            if (userMap.containsKey(reaction.user?.id ?: reaction.userId)) {
                reaction.copy(user = userMap[reaction.userId] ?: reaction.user)
            } else {
                reaction
            }
        }
    } else {
        this
    }

/**
 * Merges two collections of reactions by their [Reaction.type].
 *
 * @param recentReactions More recent collection of reactions.
 * @param cachedReactions More outdated collection of reactions.
 *
 * @return Collection of reactions where cached data is substituted by more recent one if they have same [Reaction.type].
 */
public fun mergeReactions(
    recentReactions: Collection<Reaction>,
    cachedReactions: Collection<Reaction>,
): Collection<Reaction> {
    return (
        cachedReactions.associateBy(Reaction::type) +
            recentReactions.associateBy(Reaction::type)
        ).values
}

/**
 * Updates the reaction's sync status based on [result].
 *
 * @param result The API call result.
 *
 * @return [Reaction] object with updated [Reaction.syncStatus].
 */
public fun Reaction.updateSyncStatus(result: Result<*>): Reaction {
    return when (result) {
        is Result.Success -> copy(syncStatus = SyncStatus.COMPLETED)
        is Result.Failure -> updateFailedReactionSyncStatus(result.value)
    }
}

/**
 * Updates the reaction's sync status based on [error].
 * Status can be either [SyncStatus.FAILED_PERMANENTLY] or [SyncStatus.SYNC_NEEDED] depends on type of error.
 *
 * @param error The error returned by the API call.
 *
 * @return [Reaction] object with updated [Reaction.syncStatus].
 */
private fun Reaction.updateFailedReactionSyncStatus(error: Error): Reaction {
    return copy(
        syncStatus = if (error.isPermanent()) {
            SyncStatus.FAILED_PERMANENTLY
        } else {
            SyncStatus.SYNC_NEEDED
        },
    )
}

/**
 *
 */
public fun Reaction.enrichWithDataBeforeSending(
    currentUser: User,
    isOnline: Boolean,
    enforceUnique: Boolean,
): Reaction = copy(
    user = currentUser,
    userId = currentUser.id,
    syncStatus = if (isOnline) SyncStatus.IN_PROGRESS else SyncStatus.SYNC_NEEDED,
    enforceUnique = enforceUnique,
)
