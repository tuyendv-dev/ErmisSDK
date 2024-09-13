package network.ermis.ui.view.messages.reactions

import android.graphics.Color
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.ItemMessageReactionBinding
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class ReactionsAdapter(
    @Px private val itemSize: Int,
    private val showScore: Boolean = false,
    private val reactionClickListener: ReactionClickListener,
) : ListAdapter<ReactionItem, ReactionsAdapter.ReactionViewHolder>(ReactionItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        return ItemMessageReactionBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { ReactionViewHolder(it, itemSize, showScore, reactionClickListener) }
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReactionViewHolder(
        private val binding: ItemMessageReactionBinding,
        @Px private val itemSize: Int,
        private val showScore: Boolean = false,
        private val reactionClickListener: ReactionClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var reactionItem: ReactionItem

        init {
            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                // width = itemSize
                height = itemSize
            }
            binding.root.setOnClickListener {
                reactionClickListener.onReactionClick(reactionItem.type)
            }
        }

        fun bind(reactionItem: ReactionItem) {
            this.reactionItem = reactionItem
            binding.reactionIcon.text = reactionItem.icon
            if (showScore) {
                binding.tvScoreReaction.text = reactionItem.score.toString()
                binding.tvScoreReaction.isVisible = true
                binding.root.setBackgroundResource(if (reactionItem.isMine) R.drawable.reaction_ismine else R.drawable.reaction_not_me)
            } else {
                binding.tvScoreReaction.isVisible = false
                if (reactionItem.isMine) {
                    binding.root.setBackgroundResource(R.drawable.reaction_ismine)
                } else {
                    binding.root.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }
}
