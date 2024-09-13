
package network.ermis.sample.feature.chat.info

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.Member
import network.ermis.ui.utils.extensions.getLastSeenText
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.getColorFromRes
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupAvatarBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupMemberBannedItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupMemberItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupNameItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupPickMemberItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoMemberItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoMembersSeparatorItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoOptionItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoSeparatorItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoStatefulOptionItemBinding
import network.ermis.sample.feature.chat.group.GroupChatInfoAdapter

abstract class BaseViewHolder<T : ChatInfoItem>(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    /**
     * Workaround to allow a downcast of the ChatInfoItem to T.
     */
    @Suppress("UNCHECKED_CAST")
    internal fun bindListItem(item: ChatInfoItem) = bind(item as T)

    protected abstract fun bind(item: T)
}

class ChatInfoMemberViewHolder(private val binding: ChatInfoMemberItemBinding) :
    BaseViewHolder<ChatInfoItem.MemberItem>(binding.root) {

    override fun bind(item: ChatInfoItem.MemberItem) {
        with(item.member) {
            if (user.image.isNotEmpty()) {
                binding.userAvatarView.isInvisible = false
                binding.userAvatarView.setUser(user)
            } else {
                binding.userAvatarView.isInvisible = true
            }
            binding.memberUsername.text = user.name
            binding.memberOnlineIndicator.isVisible = user.online
            binding.memberOnlineText.text = user.getLastSeenText(itemView.context)
            binding.mentionSymbolText.text = "@${user.name.lowercase()}"
        }
    }
}

class ChatInfoSeparatorViewHolder(binding: ChatInfoSeparatorItemBinding) :
    BaseViewHolder<ChatInfoItem.Separator>(binding.root) {

    override fun bind(item: ChatInfoItem.Separator) = Unit
}

class ChatInfoOptionViewHolder(
    private val binding: ChatInfoOptionItemBinding,
    private val optionClickListener: ChatInfoAdapter.ChatInfoOptionClickListener?,
) : BaseViewHolder<ChatInfoItem.Option>(binding.root) {

    private lateinit var option: ChatInfoItem.Option

    init {
        binding.optionContainer.setOnClickListener { optionClickListener?.onClick(option) }
    }

    override fun bind(item: ChatInfoItem.Option) {
        option = item
        binding.optionTextView.setText(item.textResId)
        binding.optionTextView.setTextColor(itemView.context.getColorFromRes(item.textColorResId))
        binding.optionImageView.setImageResource(item.iconResId)
        binding.optionImageView.setColorFilter(itemView.context.getColorFromRes(item.tintResId))
        binding.optionArrowRight.isVisible = item.showRightArrow
        binding.optionCompound.isVisible = item.checkedState != null
        binding.optionCompound.isChecked = item.checkedState == true
    }
}

class ChatInfoStatefulOptionViewHolder(
    private val binding: ChatInfoStatefulOptionItemBinding,
    private val optionChangedListener: ChatInfoAdapter.ChatInfoStatefulOptionChangedListener?,
) : BaseViewHolder<ChatInfoItem.Option.Stateful>(binding.root) {

    private lateinit var option: ChatInfoItem.Option.Stateful

    init {
        binding.optionSwitch.setOnCheckedChangeListener { _, isChecked ->
            optionChangedListener?.onClick(option, isChecked)
        }
    }

    override fun bind(item: ChatInfoItem.Option.Stateful) {
        option = item
        binding.optionTextView.setText(item.textResId)
        binding.optionImageView.setImageResource(item.iconResId)
        binding.optionImageView.setColorFilter(itemView.context.getColorFromRes(item.tintResId))
        binding.optionSwitch.isChecked = item.isChecked
    }
}

class ChatInfoGroupMemberViewHolder(
    private val binding: ChatInfoGroupMemberItemBinding,
    private val memberClickListener: GroupChatInfoAdapter.MemberClickListener?,
) : BaseViewHolder<ChatInfoItem.MemberItem>(binding.root) {

    private lateinit var member: Member

    init {
        binding.deleteMember.setOnClickListener {
            memberClickListener?.onClick(member)
        }
    }

    override fun bind(item: ChatInfoItem.MemberItem) {
        with(item.member) {
            member = this
            binding.userAvatarView.setUser(user)
            binding.nameTextView.text = user.name
            binding.ownerTextView.text = channelRole
            if (item.isEdit) {
                if (item.currentMember == null) {
                    binding.deleteMember.isVisible = channelRole in listOf("pending", "member")
                } else {
                    binding.deleteMember.isVisible =
                        item.currentMember.channelRole in listOf("owner") && channelRole != "owner"
                }
            } else {
                binding.deleteMember.isVisible = false
            }
        }
    }
}

class ChatInfoGroupMemberBannedViewHolder(
    private val binding: ChatInfoGroupMemberBannedItemBinding,
    private val memberClickListener: GroupChatInfoAdapter.MemberClickListener?,
) : BaseViewHolder<ChatInfoItem.MemberBannedItem>(binding.root) {

    private lateinit var member: Member

    init {
        binding.deleteMember.setOnClickListener {
            memberClickListener?.onClick(member)
        }
    }

    override fun bind(item: ChatInfoItem.MemberBannedItem) {
        with(item.member) {
            member = this
            binding.userAvatarView.setUser(user)
            binding.nameTextView.text = user.name
            binding.ownerTextView.text = channelRole
            binding.deleteMember.isVisible = item.currentMember?.channelRole in listOf("owner")
        }
    }
}

class ChatInfoGroupPickMemberViewHolder(
    private val binding: ChatInfoGroupPickMemberItemBinding,
    private val memberClickListener: GroupChatInfoAdapter.MemberClickListener?,
) : BaseViewHolder<ChatInfoItem.PickMemberItem>(binding.root) {

    private lateinit var member: Member

    init {
        binding.root.setOnClickListener {
            memberClickListener?.onClick(member)
        }
    }

    override fun bind(item: ChatInfoItem.PickMemberItem) {
        with(item.member) {
            member = this
            binding.userAvatarView.setUser(user)
            binding.nameTextView.text = user.name
            binding.ownerTextView.text = channelRole
            binding.cbSelected.isInvisible = item.selected.not()
        }
    }
}

class ChatInfoMembersSeparatorViewHolder(
    private val binding: ChatInfoMembersSeparatorItemBinding,
    private val membersSeparatorClickListener: GroupChatInfoAdapter.MembersSeparatorClickListener?,
) : BaseViewHolder<ChatInfoItem.MembersSeparator>(binding.root) {

    init {
        binding.membersSeparatorTextView.setOnClickListener { membersSeparatorClickListener?.onClick() }
    }

    override fun bind(item: ChatInfoItem.MembersSeparator) {
        binding.membersSeparatorTextView.text =
            itemView.context.getString(R.string.chat_group_info_option_members_separator_title, item.membersToShow)
    }
}

class ChatInfoGroupNameViewHolder(
    private val binding: ChatInfoGroupNameItemBinding,
    private val nameChangedListener: GroupChatInfoAdapter.NameChangedListener?,
) : BaseViewHolder<ChatInfoItem.ChannelName>(binding.root) {

    init {
        binding.editNameView.setGroupNameChangedListener { name, description ->
            nameChangedListener?.onChange(name, description)
        }
    }

    override fun bind(item: ChatInfoItem.ChannelName) {
        binding.editNameView.setChannelData(item.name, item.description)
    }
}

class ChatInfoGroupAvatarViewHolder(
    private val binding: ChatInfoGroupAvatarBinding,
    private val avatarChangedListener: GroupChatInfoAdapter.AvatarChangedListener?,
) : BaseViewHolder<ChatInfoItem.ChannelAvatar>(binding.root) {

    init {
        binding.ivChangeAvatar.setOnClickListener {
            avatarChangedListener?.onAvatarChange()
        }
    }

    override fun bind(item: ChatInfoItem.ChannelAvatar) {
        binding.avatarChannel.setChannel(item.channel)
    }
}
