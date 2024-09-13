
@file:JvmName("MentionListViewModelBinding")

package network.ermis.ui.viewmodel.mentions

import androidx.lifecycle.LifecycleOwner
import network.ermis.state.utils.EventObserver
import network.ermis.ui.view.mentions.MentionListView

/**
 * Binds [MentionListView] with [MentionListViewModel], updating the view's state based on
 * data provided by the ViewModel and propagating view events to the ViewModel as needed.
 *
 * This function sets listeners on the view and ViewModel. Call this method
 * before setting any additional listeners on these objects yourself.
 */
@JvmName("bind")
public fun MentionListViewModel.bindView(view: MentionListView, lifecycleOwner: LifecycleOwner) {
    state.observe(lifecycleOwner) { state ->
        when {
            state.isLoading -> {
                view.showLoading()
            }
            else -> {
                view.showMessages(state.results)
            }
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
