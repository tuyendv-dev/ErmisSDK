
package network.ermis.ui.view.messages.reactions

import androidx.recyclerview.widget.DiffUtil

internal class ReactionItemDiffCallback : DiffUtil.ItemCallback<ReactionItem>() {
    override fun areItemsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean {
        return oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItem: ReactionItem, newItem: ReactionItem): Boolean {
        return oldItem == newItem
    }
}
