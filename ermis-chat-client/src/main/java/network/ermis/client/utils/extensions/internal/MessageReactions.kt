package network.ermis.client.utils.extensions.internal

import network.ermis.core.models.Message
import network.ermis.core.models.Reaction

public fun Message.addMyReaction(reaction: Reaction, enforceUnique: Boolean = false): Message {
    return updateReactions {
        when (enforceUnique) {
            true -> clearOwnReactions(reaction.userId)
            false -> this
        }.let {
            it.copy(
                latestReactions = it.latestReactions + reaction,
                ownReactions = it.ownReactions + reaction,
                reactionCounts = it.reactionCounts + (reaction.type to ((reactionCounts[reaction.type] ?: 0) + 1)),
                reactionScores = it.reactionScores +
                    (reaction.type to ((reactionScores[reaction.type] ?: 0) + reaction.score)),
            )
        }
    }
}

public fun Message.removeMyReaction(reaction: Reaction): Message =
    updateReactions {
        val removed = ownReactions.filter { it.type == reaction.type && it.userId == reaction.userId }.toSet()
        copy(
            latestReactions = latestReactions.filterNot { it.type == reaction.type && it.userId == reaction.userId },
            ownReactions = ownReactions - removed,
            reactionCounts = reactionCounts.mapNotNull { (type, count) ->
                when (removed.firstOrNull { it.type == type }) {
                    null -> type to count
                    else -> type to (count - 1)
                }.takeUnless { it.second <= 0 }
            }.toMap(),
            reactionScores = reactionScores.mapNotNull { (type, score) ->
                when (val ownReaction = removed.firstOrNull { it.type == type }) {
                    null -> type to score
                    else -> type to (score - ownReaction.score)
                }.takeUnless { it.second <= 0 }
            }.toMap(),
        )
    }

private fun ReactionData.clearOwnReactions(userId: String): ReactionData {
    val ownReactionsMap = ownReactions.groupBy { it.type }
    return copy(
        latestReactions = latestReactions.filterNot { it.userId == userId },
        reactionCounts = reactionCounts.mapNotNull { (type, count) ->
            when (val ownReaction = ownReactionsMap[type]) {
                null -> type to count
                else -> type to (count - ownReaction.size)
            }.takeUnless { it.second <= 0 }
        }.toMap(),
        reactionScores = reactionScores.mapNotNull { (type, score) ->
            when (val ownReaction = ownReactionsMap[type]) {
                null -> type to score
                else -> type to (score - ownReaction.sumOf { it.score })
            }.takeUnless { it.second <= 0 }
        }.toMap(),
        ownReactions = emptyList(),
    )
}

private inline fun Message.updateReactions(actions: ReactionData.() -> ReactionData): Message {
    val reactionData = ReactionData(
        reactionCounts,
        reactionScores,
        latestReactions,
        ownReactions,
    ).actions()
    return copy(
        reactionCounts = reactionData.reactionCounts,
        reactionScores = reactionData.reactionScores,
        latestReactions = reactionData.latestReactions,
        ownReactions = reactionData.ownReactions,
    )
}

private data class ReactionData(
    val reactionCounts: Map<String, Int>,
    val reactionScores: Map<String, Int>,
    val latestReactions: List<Reaction>,
    val ownReactions: List<Reaction>,
)
