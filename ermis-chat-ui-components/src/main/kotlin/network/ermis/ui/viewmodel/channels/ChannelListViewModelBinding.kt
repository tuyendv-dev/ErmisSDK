
@file:JvmName("ChannelListViewModelBinding")

package network.ermis.ui.viewmodel.channels

import android.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.distinctUntilChanged
import network.ermis.state.utils.EventObserver
import network.ermis.ui.components.R
import network.ermis.ui.view.ChannelListView
import network.ermis.ui.view.channels.adapter.ChannelListItem
import network.ermis.ui.utils.extensions.combineWith

/**
 * Binds [ChannelListView] with [ChannelListViewModel], updating the view's state based on
 * data provided by the ViewModel, and propagating view events to the ViewModel as needed.
 *
 * This function sets listeners on the view and ViewModel. Call this method
 * before setting any additional listeners on these objects yourself.
 */
@JvmName("bind")
public fun ChannelListViewModel.bindView(
    view: ChannelListView,
    lifecycleOwner: LifecycleOwner,
) {
    state.combineWith(paginationState) { state, paginationState -> state to paginationState }
        .combineWith(typingEvents) { states, typingEvents ->
            val state = states?.first
            val paginationState = states?.second

            paginationState?.let {
                view.setPaginationEnabled(!it.endOfChannels && !it.loadingMore)
            }

            var list: List<ChannelListItem> = state?.channels?.map {
                ChannelListItem.ChannelItem(it, typingEvents?.get(it.cid)?.users ?: emptyList())
            } ?: emptyList()
            if (paginationState?.loadingMore == true) {
                list = list + ChannelListItem.LoadingMoreItem
            }

            list to (state?.isLoading == true)
        }.distinctUntilChanged().observe(lifecycleOwner) { (list, isLoading) ->

            when {
                isLoading && list.isEmpty() -> view.showLoadingView()

                list.isNotEmpty() -> {
                    view.hideLoadingView()
                    view.setChannels(list)
                }

                else -> {
                    view.hideLoadingView()
                    view.setChannels(emptyList())
                }
            }
        }

    view.setOnEndReachedListener {
        onAction(ChannelListViewModel.Action.ReachedEndOfList)
    }

    view.setChannelDeleteClickListener {
        AlertDialog.Builder(view.context)
            .setTitle(R.string.ermis_ui_channel_list_delete_confirmation_title)
            .setMessage(R.string.ermis_ui_channel_list_delete_confirmation_message)
            .setPositiveButton(R.string.ermis_ui_channel_list_delete_confirmation_positive_button) { dialog, _ ->
                dialog.dismiss()
                deleteChannel(it)
            }
            .setNegativeButton(R.string.ermis_ui_channel_list_delete_confirmation_negative_button) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    view.setChannelLeaveClickListener { channel ->
        leaveChannel(channel)
    }

    errorEvents.observe(
        lifecycleOwner,
        EventObserver {
            view.showError(it)
        },
    )
}
