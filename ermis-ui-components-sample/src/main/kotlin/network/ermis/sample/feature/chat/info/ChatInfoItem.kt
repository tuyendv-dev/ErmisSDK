
package network.ermis.sample.feature.chat.info

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import network.ermis.core.models.Channel
import network.ermis.core.models.Member
import network.ermis.chat.ui.sample.R

sealed class ChatInfoItem {

    val id: String
        get() = when (this) {
            is MemberItem -> member.getUserId()
            is MemberBannedItem -> member.getUserId()
            else -> this::class.java.simpleName
        }

    data class MemberItem(val member: Member, val currentMember: Member?, val isEdit: Boolean = false) : ChatInfoItem()
    data class MemberBannedItem(val member: Member, val currentMember: Member?) : ChatInfoItem()
    data class PickMemberItem(val member: Member, val selected: Boolean) : ChatInfoItem()
    data class MembersSeparator(val membersToShow: Int) : ChatInfoItem()
    data class ChannelName(val name: String, val description: String) : ChatInfoItem()
    data class ChannelAvatar(val channel: Channel) : ChatInfoItem()
    data object Separator : ChatInfoItem()

    sealed class Option : ChatInfoItem() {

        @get:DrawableRes
        abstract val iconResId: Int

        @get:StringRes
        abstract val textResId: Int

        @get:ColorRes
        open val tintResId: Int = R.color.ui_grey

        @get:ColorRes
        open val textColorResId: Int = R.color.ui_text_color_primary

        open val showRightArrow: Boolean = true

        open val checkedState: Boolean? = null

        data object ChannelMembers : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_new_group
            override val textResId: Int
                get() = R.string.chat_group_info_members
        }

        data object ChannelPermissions : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_member_add
            override val textResId: Int
                get() = R.string.chat_group_info_permissions
        }

        data object ChannelAdminstrator : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_member
            override val textResId: Int
                get() = R.string.chat_group_info_administrators
        }

        data object BannedUser : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_leave_group
            override val textResId: Int
                get() = R.string.chat_group_info_banned_users
            override val tintResId: Int
                get() = R.color.red
            override val textColorResId: Int
                get() = R.color.red
        }

        data object PinnedMessages : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_pin
            override val textResId: Int
                get() = R.string.chat_info_option_pinned_messages
        }

        data object SharedMedia : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_media
            override val textResId: Int
                get() = R.string.chat_info_option_media
        }

        data object SharedFiles : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_files
            override val textResId: Int
                get() = R.string.chat_info_option_files
        }

        data object SharedGroups : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_new_group
            override val textResId: Int
                get() = R.string.chat_info_option_shared_groups
        }

        data object DeleteConversation : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_delete
            override val textResId: Int
                get() = R.string.chat_info_option_delete_conversation
            override val tintResId: Int
                get() = R.color.red
            override val textColorResId: Int
                get() = R.color.red
            override val showRightArrow: Boolean = false
        }

        data object LeaveGroup : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_leave_group
            override val textResId: Int
                get() = R.string.chat_group_info_option_leave
            override val showRightArrow: Boolean = false
        }

        data class HideChannel(var isHidden: Boolean) : Option() {
            override val iconResId: Int
                get() = R.drawable.ic_hide
            override val textResId: Int
                get() = R.string.chat_group_info_option_hide

            override val showRightArrow: Boolean = false

            override val checkedState: Boolean
                get() = isHidden
        }

        sealed class Stateful : Option() {
            abstract val isChecked: Boolean

            data class MuteDistinctChannel(override val isChecked: Boolean) : Stateful() {
                override val iconResId: Int
                    get() = R.drawable.ic_mute
                override val textResId: Int
                    get() = R.string.chat_info_option_mute_user
            }

            data class MuteChannel(override val isChecked: Boolean) : Stateful() {
                override val iconResId: Int
                    get() = R.drawable.ic_mute
                override val textResId: Int
                    get() = R.string.chat_group_info_option_mute
            }

            data class Block(override val isChecked: Boolean) : Stateful() {
                override val iconResId: Int
                    get() = R.drawable.ic_block
                override val textResId: Int
                    get() = R.string.chat_info_option_block_user
            }
        }
    }
}
