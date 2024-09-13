package network.ermis.sample.feature.channel.add.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import network.ermis.client.ErmisClient
import network.ermis.core.models.User
import network.ermis.sample.util.extensions.containingSpecialCharacters
import network.ermis.sample.util.extensions.unaccent
import io.getstream.log.taggedLogger
import io.getstream.result.Result

class AddGroupChannelViewModel : ViewModel() {

    private val logger by taggedLogger("Chat:AddGroupChannelViewModel")
    private val chatClient = ErmisClient.instance()
    private val _state: MutableLiveData<State> = MutableLiveData()
    private val _paginationState: MutableLiveData<PaginationState> = MutableLiveData()
    val state: LiveData<State> = _state
    val paginationState: LiveData<PaginationState> = _paginationState
    private val mListAllContact: ArrayList<User> = arrayListOf()

    init {
        requestUsers()
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.SearchInputChanged -> searchUsers(event.query)
        }
    }

    private fun requestUsers() {
        chatClient.getListContactIds().enqueue { resultIds ->
            when (resultIds) {
                is Result.Success -> {
                    chatClient.getUsersByIds(userIds = resultIds.value).enqueue { result ->
                        when (result) {
                            is Result.Success -> {
                                mListAllContact.clear()
                                mListAllContact.addAll(result.value)
                                _state.postValue(State.Result(mListAllContact))
                            }
                            is Result.Failure -> {

                            }
                        }
                    }
                }
                is Result.Failure -> {

                }
            }
        }
    }

    private fun searchUsers(query: String) {
        val filter = mListAllContact
            .filter { user ->
                if (query.containingSpecialCharacters()) {
                    user.name.contains(query, true)
                } else {
                    user.name.unaccent().contains(query, true)
                }
            }
        _state.postValue(State.Result(filter))
    }

    sealed class State {
        object Loading : State()
        data class Result(val users: List<User>) : State()
    }

    sealed class Event {
        data class SearchInputChanged(val query: String) : Event()
    }

    data class PaginationState(
        val loadingMore: Boolean = false,
        val endReached: Boolean = false,
    )
}
