
package network.ermis.ui.view.messages.reactions.user

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.ui.components.R
import io.getstream.chat.android.ui.common.extensions.internal.context
import network.ermis.ui.common.state.messages.list.MessageOptionsUserReactionAlignment
import network.ermis.ui.common.state.messages.list.isStartAlignment
import network.ermis.ui.components.databinding.ItemUserReactionBinding
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.extensions.updateConstraints

internal class UserReactionAdapter(
    private val userReactionClickListener: UserReactionClickListener,
) : ListAdapter<UserReactionItem, UserReactionAdapter.UserReactionViewHolder>(UserReactionItemDiffCallback) {

    var messageOptionsUserReactionAlignment: MessageOptionsUserReactionAlignment =
        MessageOptionsUserReactionAlignment.BY_USER
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReactionViewHolder {
        return ItemUserReactionBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { UserReactionViewHolder(it, userReactionClickListener, messageOptionsUserReactionAlignment) }
    }

    override fun onBindViewHolder(holder: UserReactionViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    class UserReactionViewHolder(
        private val binding: ItemUserReactionBinding,
        private val userReactionClickListener: UserReactionClickListener,
        private val messageOptionsUserReactionAlignment: MessageOptionsUserReactionAlignment =
            MessageOptionsUserReactionAlignment.BY_USER,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var userReactionItem: UserReactionItem

        init {
            binding.root.setOnClickListener { userReactionClickListener.onUserReactionClick(userReactionItem) }
        }

        fun bind(userReactionItem: UserReactionItem) {
            this.userReactionItem = userReactionItem
            bindUserAvatar()
            bindUserName()
            bindUserReaction()
        }

        private fun bindUserAvatar() {
            binding.userAvatarView.setUser(userReactionItem.user)
        }

        private fun bindUserName() {
            binding.userNameTextView.text = userReactionItem.user.name
        }

        private fun bindUserReaction() {
            binding.apply {
                reactionContainer.updateConstraints {
                    clear(R.id.userReactionView, ConstraintSet.START)
                    clear(R.id.userReactionView, ConstraintSet.END)
                }
                val isEndAlignment = !messageOptionsUserReactionAlignment.isStartAlignment(userReactionItem.isMine)

                userReactionView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    if (isEndAlignment) {
                        endToEnd = ConstraintSet.PARENT_ID
                        marginEnd = context.getDimension(R.dimen.ui_spacing_small)
                    } else {
                        startToStart = ConstraintSet.PARENT_ID
                        marginStart = context.getDimension(R.dimen.ui_spacing_small)
                    }
                }
                userReactionView.setReaction(userReactionItem)
            }
        }
    }

    private object UserReactionItemDiffCallback : DiffUtil.ItemCallback<UserReactionItem>() {
        override fun areItemsTheSame(oldItem: UserReactionItem, newItem: UserReactionItem): Boolean {
            return oldItem.user.id == newItem.user.id &&
                oldItem.reaction.type == newItem.reaction.type
        }

        override fun areContentsTheSame(oldItem: UserReactionItem, newItem: UserReactionItem): Boolean {
            return oldItem == newItem
        }
    }

    internal fun interface UserReactionClickListener {
        fun onUserReactionClick(userReaction: UserReactionItem)
    }
}
