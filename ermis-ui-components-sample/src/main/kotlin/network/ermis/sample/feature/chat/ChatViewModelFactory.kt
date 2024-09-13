package network.ermis.sample.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import network.ermis.sample.feature.chat.group.GroupChatInfoViewModel
import network.ermis.sample.feature.chat.group.administrator.GroupChatAdminViewModel
import network.ermis.sample.feature.chat.group.banned.GroupChatBannedViewModel
import network.ermis.sample.feature.chat.group.member.GroupChatMembersViewModel
import network.ermis.sample.feature.chat.group.member.GroupChatPickMembersViewModel
import network.ermis.sample.feature.chat.group.permission.GroupChatPermissionViewModel
import network.ermis.sample.feature.chat.group.users.GroupChatInfoAddUsersViewModel
import network.ermis.sample.feature.chat.info.search.ChannelSearchMessageViewModel

class ChatViewModelFactory(private val cid: String) : ViewModelProvider.Factory {
    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        ChatViewModel::class.java to { ChatViewModel(cid) },
        GroupChatInfoViewModel::class.java to { GroupChatInfoViewModel(cid) },
        GroupChatInfoAddUsersViewModel::class.java to { GroupChatInfoAddUsersViewModel(cid) },
        GroupChatPermissionViewModel::class.java to { GroupChatPermissionViewModel(cid) },
        GroupChatBannedViewModel::class.java to { GroupChatBannedViewModel(cid) },
        GroupChatAdminViewModel::class.java to { GroupChatAdminViewModel(cid) },
        GroupChatMembersViewModel::class.java to { GroupChatMembersViewModel(cid) },
        GroupChatPickMembersViewModel::class.java to { GroupChatPickMembersViewModel(cid) },
        ChannelSearchMessageViewModel::class.java to { ChannelSearchMessageViewModel(cid) },
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException("ChatViewModelFactory can only create instances of the following classes: ${factories.keys.joinToString { it.simpleName }}")

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}
