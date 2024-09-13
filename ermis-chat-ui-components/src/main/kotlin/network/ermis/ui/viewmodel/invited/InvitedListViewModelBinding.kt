@file:JvmName("InvitedListViewModelBinding")

package network.ermis.ui.viewmodel.invited

import androidx.lifecycle.LifecycleOwner
import network.ermis.state.utils.EventObserver
import network.ermis.ui.view.invite.ChannelInviteListView
import network.ermis.ui.view.mentions.MentionListView

/**
 * Binds [MentionListView] with [MentionListViewModel], updating the view's state based on
 * data provided by the ViewModel and propagating view events to the ViewModel as needed.
 *
 * This function sets listeners on the view and ViewModel. Call this method
 * before setting any additional listeners on these objects yourself.
 */
@JvmName("bind")
public fun InvitedListViewModel.bindView(view: ChannelInviteListView, lifecycleOwner: LifecycleOwner) {
    state.observe(lifecycleOwner) { state ->
            when {
                state.isLoading -> view.showLoading()
                else -> {
                    view.showChannels(state.channels)
                }
            }
        }
    errorEvents.observe(
        lifecycleOwner,
        EventObserver {
            view.showError(it)
        },
    )
    view.setLoadMoreListener {
        onAction(InvitedListViewModel.Action.ReachedEndOfList)
    }
}
