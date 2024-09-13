package network.ermis.ui.view.messages.reactions.user

import network.ermis.core.models.Reaction
import network.ermis.core.models.User

internal data class UserReactionItem(
    val user: User,
    val reaction: Reaction,
    val isMine: Boolean,
    val icon: String,
    val score: Int
)
