
package network.ermis.sample.feature.chat.group

import android.view.LayoutInflater
import android.view.ViewGroup
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupAvatarBinding
import network.ermis.core.models.Member
import network.ermis.sample.common.appThemeContext
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupMemberBannedItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupMemberItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupNameItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupPickMemberItemBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoMembersSeparatorItemBinding
import network.ermis.sample.feature.chat.info.BaseViewHolder
import network.ermis.sample.feature.chat.info.ChatInfoAdapter
import network.ermis.sample.feature.chat.info.ChatInfoGroupAvatarViewHolder
import network.ermis.sample.feature.chat.info.ChatInfoGroupMemberBannedViewHolder
import network.ermis.sample.feature.chat.info.ChatInfoGroupMemberViewHolder
import network.ermis.sample.feature.chat.info.ChatInfoGroupNameViewHolder
import network.ermis.sample.feature.chat.info.ChatInfoGroupPickMemberViewHolder
import network.ermis.sample.feature.chat.info.ChatInfoItem
import network.ermis.sample.feature.chat.info.ChatInfoMembersSeparatorViewHolder

class GroupChatInfoAdapter : ChatInfoAdapter() {

    private var memberClickListener: MemberClickListener? = null
    private var membersSeparatorClickListener: MembersSeparatorClickListener? = null
    private var nameChangedListener: NameChangedListener? = null
    private var avatarChangedListener: AvatarChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_GROUP_MEMBER_ITEM ->
                ChatInfoGroupMemberItemBinding
                    .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
                    .let { ChatInfoGroupMemberViewHolder(it, memberClickListener) }
            TYPE_GROUP_MEMBER_BANNED ->
                ChatInfoGroupMemberBannedItemBinding
                    .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
                    .let { ChatInfoGroupMemberBannedViewHolder(it, memberClickListener) }
            TYPE_GROUP_PICK_MEMBER ->
                ChatInfoGroupPickMemberItemBinding
                    .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
                    .let { ChatInfoGroupPickMemberViewHolder(it, memberClickListener) }
            TYPE_MEMBERS_SEPARATOR ->
                ChatInfoMembersSeparatorItemBinding
                    .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
                    .let { ChatInfoMembersSeparatorViewHolder(it, membersSeparatorClickListener) }
            TYPE_EDIT_GROUP_NAME ->
                ChatInfoGroupNameItemBinding
                    .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
                    .let { ChatInfoGroupNameViewHolder(it, nameChangedListener) }
            TYPE_EDIT_GROUP_AVATAR ->
                ChatInfoGroupAvatarBinding
                    .inflate(LayoutInflater.from(parent.context.appThemeContext), parent, false)
                    .let { ChatInfoGroupAvatarViewHolder(it, avatarChangedListener) }
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatInfoItem.MemberItem -> TYPE_GROUP_MEMBER_ITEM
            is ChatInfoItem.MemberBannedItem -> TYPE_GROUP_MEMBER_BANNED
            is ChatInfoItem.PickMemberItem -> TYPE_GROUP_PICK_MEMBER
            is ChatInfoItem.MembersSeparator -> TYPE_MEMBERS_SEPARATOR
            is ChatInfoItem.ChannelName -> TYPE_EDIT_GROUP_NAME
            is ChatInfoItem.ChannelAvatar -> TYPE_EDIT_GROUP_AVATAR
            else -> super.getItemViewType(position)
        }
    }

    fun setMemberClickListener(listener: MemberClickListener) {
        memberClickListener = listener
    }

    fun setMembersSeparatorClickListener(listener: MembersSeparatorClickListener?) {
        membersSeparatorClickListener = listener
    }

    fun setNameChangedListener(listener: NameChangedListener?) {
        nameChangedListener = listener
    }

    fun setAvatarChangedListener(listener: AvatarChangedListener?) {
        avatarChangedListener = listener
    }

    companion object {
        private const val TYPE_GROUP_MEMBER_ITEM = 10
        private const val TYPE_MEMBERS_SEPARATOR = 11
        private const val TYPE_EDIT_GROUP_NAME = 12
        private const val TYPE_EDIT_GROUP_AVATAR = 13
        private const val TYPE_GROUP_MEMBER_BANNED = 14
        private const val TYPE_GROUP_PICK_MEMBER = 15
    }

    fun interface MemberClickListener {
        fun onClick(member: Member)
    }

    fun interface MembersSeparatorClickListener {
        fun onClick()
    }

    fun interface NameChangedListener {
        fun onChange(name: String, description: String)
    }

    fun interface AvatarChangedListener {
        fun onAvatarChange()
    }
}
