
@file:JvmName("PinnedMessageListViewModelBinding")

package network.ermis.ui.viewmodel.pinned

import androidx.lifecycle.LifecycleOwner
import network.ermis.state.utils.EventObserver
import network.ermis.ui.view.pinned.PinnedMessageListView

/**
 * Binds [PinnedMessageListView] with [PinnedMessageListViewModel], updating the view's state based on
 * data provided by the ViewModel and propagating view events to the ViewModel as needed.
 *
 * This function sets listeners on the view and ViewModel. Call this method
 * before setting any additional listeners on these objects yourself.
 */
@JvmName("bind")
public fun PinnedMessageListViewModel.bindView(view: PinnedMessageListView, lifecycleOwner: LifecycleOwner) {
    state.observe(lifecycleOwner) { state ->
        val isLoadingMore = state.results.isNotEmpty() && state.results.last().id == ""

        when {
            isLoadingMore -> {
                view.showLoading()
                view.showMessages(state.results)
            }
            state.isLoading -> view.showLoading()
            else -> view.showMessages(state.results)
        }
    }
    errorEvents.observe(
        lifecycleOwner,
        EventObserver {
            view.showError()
        },
    )
    view.setLoadMoreListener {
        this.loadMore()
    }
}
