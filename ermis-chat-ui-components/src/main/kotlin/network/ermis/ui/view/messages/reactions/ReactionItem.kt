package network.ermis.ui.view.messages.reactions

internal data class ReactionItem(
    val type: String,
    val isMine: Boolean,
    val icon: String,
    val score: Int
)
