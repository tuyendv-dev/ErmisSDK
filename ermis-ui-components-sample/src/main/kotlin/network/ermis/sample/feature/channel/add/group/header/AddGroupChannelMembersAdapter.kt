
package network.ermis.sample.feature.channel.add.group.header

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.chat.ui.sample.databinding.AddGroupChannelMemberItemBinding
import network.ermis.core.models.User
import network.ermis.sample.common.appThemeContext
import network.ermis.sample.feature.channel.add.header.MemberClickListener

class AddGroupChannelMembersAdapter :
    ListAdapter<User, AddGroupChannelMembersAdapter.MemberViewHolder>(
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        },
    ) {

    var memberClickListener: MemberClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return AddGroupChannelMemberItemBinding
            .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
            .let(::MemberViewHolder)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MemberViewHolder(private val binding: AddGroupChannelMemberItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var member: User

        init {
            binding.deleteMemberButton.setOnClickListener { memberClickListener?.onMemberClicked(member) }
        }

        fun bind(user: User) {
            member = user
            binding.userAvatarView.setUser(user)
            binding.memberNameTextView.text = user.name
        }
    }
}
