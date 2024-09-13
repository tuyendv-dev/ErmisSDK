
@file:JvmName("ChannelListHeaderViewModelBinding")

package network.ermis.ui.viewmodel.channels

import androidx.lifecycle.LifecycleOwner
import network.ermis.core.models.ConnectionState
import network.ermis.ui.view.channels.header.ChannelListHeaderView

/**
 * Binds [ChannelListHeaderView] with [ChannelListHeaderViewModel], updating the view's state
 * based on data provided by the ViewModel, and propagating view events to the ViewModel as needed.
 *
 * This function sets listeners on the view and ViewModel. Call this method
 * before setting any additional listeners on these objects yourself.
 */
@JvmName("bind")
public fun ChannelListHeaderViewModel.bindView(view: ChannelListHeaderView, lifecycleOwner: LifecycleOwner) {
    with(view) {
        // TODO tuyendv tạm bỏ live thay bằng http vì socket đang chưa tra dữ liệu user
        // currentUser.observe(lifecycleOwner) { user ->
        //     user?.let(::setUser)
        // }
        connectionState.observe(lifecycleOwner) { connectionState ->
            when (connectionState) {
                is ConnectionState.Connected -> showOnlineTitle()
                is ConnectionState.Connecting -> showConnectingTitle()
                is ConnectionState.Offline -> showOfflineTitle()
            }
        }
    }
}
