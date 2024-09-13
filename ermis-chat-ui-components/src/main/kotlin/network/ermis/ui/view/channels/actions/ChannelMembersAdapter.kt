
package network.ermis.ui.view.channels.actions

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.Member
import network.ermis.ui.components.databinding.ItemChannelMemberBinding
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class ChannelMembersAdapter(
    private val onMemberClicked: (Member) -> Unit,
) : ListAdapter<Member, ChannelMembersAdapter.ChannelMemberViewHolder>(ChannelMembersDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelMemberViewHolder {
        return ItemChannelMemberBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { ChannelMemberViewHolder(it, onMemberClicked) }
    }

    override fun onBindViewHolder(holder: ChannelMemberViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    object ChannelMembersDiffCallback : DiffUtil.ItemCallback<Member>() {
        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.user.id == newItem.user.id
        }

        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem == newItem
        }
    }

    class ChannelMemberViewHolder(
        private val binding: ItemChannelMemberBinding,
        private val onMemberClicked: (Member) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var member: Member

        init {
            binding.root.setOnClickListener { onMemberClicked(member) }
        }

        fun bind(member: Member) {
            this.member = member
            val user = member.user

            binding.apply {
                userAvatarView.setUser(user)
                userNameTextView.text = user.name
            }
        }
    }
}
