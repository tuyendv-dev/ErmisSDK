
package network.ermis.sample.feature.channel.add

import androidx.lifecycle.LifecycleOwner
import network.ermis.sample.feature.channel.add.group.AddGroupChannelViewModel

fun AddChannelViewModel.bindView(view: AddChannelView, lifecycleOwner: LifecycleOwner) {
    state.observe(lifecycleOwner) { state ->
        when (state) {
            AddChannelViewModel.State.Loading -> view.showLoadingView()
            is AddChannelViewModel.State.Result -> {
                view.setUsers(state.users)
            }
            is AddChannelViewModel.State.ResultMoreUsers -> {
                view.addMoreUsers(state.users)
            }
            is AddChannelViewModel.State.InitializeChannel,
            is AddChannelViewModel.State.NavigateToChannel,
            -> Unit
        }
    }

    paginationState.observe(lifecycleOwner) { state ->
        view.setPaginationEnabled(!state.endReached && !state.loadingMore)
    }

    view.endReachedListener = AddChannelView.EndReachedListener {
        onEvent(AddChannelViewModel.Event.ReachedEndOfList)
    }
    view.setSearchInputChangedListener {
        onEvent(AddChannelViewModel.Event.SearchInputChanged(it))
    }
}

fun AddGroupChannelViewModel.bindView(view: AddChannelView, lifecycleOwner: LifecycleOwner) {
    state.observe(lifecycleOwner) { state ->
        when (state) {
            AddGroupChannelViewModel.State.Loading -> view.showLoadingView()
            is AddGroupChannelViewModel.State.Result -> {
                view.setUsers(state.users)
            }
        }
    }

    paginationState.observe(lifecycleOwner) { state ->
        view.setPaginationEnabled(!state.endReached && !state.loadingMore)
    }

    view.endReachedListener = AddChannelView.EndReachedListener {

    }
    view.setSearchInputChangedListener {
        onEvent(AddGroupChannelViewModel.Event.SearchInputChanged(it))
    }
}
