package network.ermis.offline.repository.domain.reaction

import network.ermis.core.models.Reaction
import network.ermis.core.models.User

internal fun Reaction.toEntity(): ReactionEntity = ReactionEntity(
    messageId = messageId,
    userId = fetchUserId(),
    type = type,
    score = score,
    createdAt = createdAt,
    createdLocallyAt = createdLocallyAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    extraData = extraData,
    syncStatus = syncStatus,
    enforceUnique = enforceUnique,
)

internal suspend fun ReactionEntity.toModel(getUser: suspend (userId: String) -> User): Reaction = Reaction(
    messageId = messageId,
    type = type,
    score = score,
    user = getUser(userId),
    extraData = extraData.toMutableMap(),
    createdAt = createdAt,
    createdLocallyAt = createdLocallyAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncStatus = syncStatus,
    userId = userId,
    enforceUnique = enforceUnique,
)
