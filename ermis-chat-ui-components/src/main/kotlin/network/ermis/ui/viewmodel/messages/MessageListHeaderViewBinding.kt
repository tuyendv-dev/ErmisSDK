
@file:JvmName("MessageListHeaderViewModelBinding")

package network.ermis.ui.viewmodel.messages

import androidx.lifecycle.LifecycleOwner
import network.ermis.core.models.ConnectionState
import network.ermis.ui.ChatUI
import network.ermis.ui.view.messages.header.MessageListHeaderView

/**
 * Binds [MessageListHeaderView] with [MessageListHeaderViewModel], updating the view's state
 * based on data provided by the ViewModel.
 *
 * This function sets listeners on the view and ViewModel. Call this method
 * before setting any additional listeners on these objects yourself.
 */
@JvmName("bind")
public fun MessageListHeaderViewModel.bindView(view: MessageListHeaderView, lifecycle: LifecycleOwner) {
    channel.observe(lifecycle) { channel ->
        val channelName = ChatUI.channelNameFormatter.formatChannelName(
            channel = channel,
            currentUser = ChatUI.currentUserProvider.getCurrentUser(),
        )
        view.setTitle(channelName)
        view.setAvatar(channel)
        // view.setOnlineStateSubtitle(channel.getMembersStatusText(view.context))
    }

    online.observe(lifecycle) { onlineState ->
        when (onlineState) {
            is ConnectionState.Connected -> {
                view.showOnlineStateSubtitle()
            }
            is ConnectionState.Connecting -> {
                view.showSearchingForNetworkLabel()
            }
            is ConnectionState.Offline -> {
                view.showOfflineStateLabel()
            }
        }
    }
    // tuyendv đưa view typing xuống dưới list message
    // typingUsers.observe(lifecycle, view::showTypingStateLabel)

    activeThread.observe(lifecycle) { message ->
        if (message != null) {
            view.setThreadMode()
        } else {
            view.setNormalMode()
        }
    }
}
