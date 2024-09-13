
package network.ermis.sample.feature.channel.add.group.selectname

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.chat.ui.sample.databinding.AddGroupChannelSelectNameMemberItemBinding
import network.ermis.core.models.User
import network.ermis.sample.common.appThemeContext
import network.ermis.sample.feature.channel.add.group.selectname.AddGroupChannelSelectNameMembersAdapter.DeleteMemberClickListener

class AddGroupChannelSelectNameMembersAdapter :
    ListAdapter<User, AddGroupChannelSelectNameMembersAdapter.MemberViewHolder>(
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        },
    ) {

    var deleteMemberClickListener: DeleteMemberClickListener = DeleteMemberClickListener { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return AddGroupChannelSelectNameMemberItemBinding
            .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
            .let(::MemberViewHolder)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MemberViewHolder(
        private val binding: AddGroupChannelSelectNameMemberItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.deleteMemberButton.setOnClickListener { deleteMemberClickListener.onDeleteMember(user) }
            binding.userAvatarView.setUser(user)
            binding.memberNameTextView.text = user.name
        }
    }

    fun interface DeleteMemberClickListener {
        fun onDeleteMember(member: User)
    }
}
