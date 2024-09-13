
package network.ermis.sample.feature.chat.group.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.User
import network.ermis.sample.common.appThemeContext
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupAddUsersItemBinding

class GroupChatInfoAddUsersAdapter : ListAdapter<UserSelect, GroupChatInfoAddUsersAdapter.UserViewHolder>(
    object : DiffUtil.ItemCallback<UserSelect>() {
        override fun areItemsTheSame(oldItem: UserSelect, newItem: UserSelect): Boolean {
            return oldItem.user.id == newItem.user.id && oldItem.selected == newItem.selected
        }

        override fun areContentsTheSame(oldItem: UserSelect, newItem: UserSelect): Boolean {
            return oldItem == newItem
        }
    },
) {

    private var userClickListener: UserClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return ChatInfoGroupAddUsersItemBinding
            .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
            .let(::UserViewHolder)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setUserClickListener(listener: UserClickListener?) {
        userClickListener = listener
    }

    inner class UserViewHolder(private val binding: ChatInfoGroupAddUsersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var user: User

        init {
            binding.userContainer.setOnClickListener { userClickListener?.onClick(user) }
        }

        fun bind(item: UserSelect) {
            this.user = item.user
            binding.userAvatarView.setUser(user)
            binding.userNameTextView.text = user.name
            binding.cbSelected.isInvisible = item.selected.not()
        }
    }

    fun interface UserClickListener {
        fun onClick(user: User)
    }
}
