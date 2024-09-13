package network.ermis.sample.feature.chat.group.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.core.models.Member
import network.ermis.core.models.User
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import network.ermis.sample.util.extensions.containingSpecialCharacters
import network.ermis.sample.util.extensions.unaccent
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class GroupChatInfoAddUsersViewModel(
    cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
) : ViewModel() {

    private val logger by taggedLogger("Chat:GroupChatInfoAddUsersViewModel")
    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: Flow<ChannelState> =
        chatClient.watchChannelAsState(cid, 0, viewModelScope).filterNotNull()

    private val channelClient = chatClient.channel(cid)
    private var members: List<Member> = emptyList()
    private val _state: MutableLiveData<State> = MutableLiveData(INITIAL_STATE)
    private val _userAddedState: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()
    private var isLoadingMore: Boolean = false
    val state: LiveData<State> = _state
    val userAddedState: LiveData<Boolean> = _userAddedState
    val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

    private val membersLiveData: LiveData<List<Member>> = channelState.flatMapLatest { it.members }.asLiveData()

    private val observer = Observer<List<Member>> { members = it }

    private var page = 1
    private val mListAllContact: ArrayList<User> = arrayListOf()

    init {
        membersLiveData.observeForever(observer)
        viewModelScope.launch {
            fetchUsers()
        }
    }

    override fun onCleared() {
        membersLiveData.removeObserver(observer)
        super.onCleared()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UserClicked -> selectedUser(action.user)
            is Action.SearchQueryChanged -> setQuery(action.query)
            Action.LoadMoreRequested -> loadMore()
            Action.AddMembers -> addMembers()
        }
    }

    private fun selectedUser(user: User) {
        var membersSelected = _state.value!!.membersSelected
        membersSelected = if (membersSelected.contains(user.id)) {
            membersSelected.minus(user.id)
        } else {
            membersSelected.plus(user.id)
        }
        _state.postValue(_state.value!!.copy(membersSelected = membersSelected))
    }

    private fun addMembers() {
        viewModelScope.launch {
            val membersSelected = _state.value!!.membersSelected
            when (channelClient.addMembers(membersSelected.toList()).await()) {
                is Result.Success -> {
                    _state.postValue(_state.value!!.copy(membersSelected = setOf()))
                    _userAddedState.value = true
                }
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.AddMemberError))
            }
        }
    }

    private fun loadMore() {
        viewModelScope.launch {
            val currentState = _state.value!!

            if (!currentState.canLoadMore || isLoadingMore) {
                return@launch
            }
            isLoadingMore = true
            page ++
            fetchUsers()
        }
    }

    private fun setQuery(query: String) {
        val filter = mListAllContact
            .filter { user ->
                if (query.containingSpecialCharacters()) {
                    user.name.contains(query, true)
                } else {
                    user.name.unaccent().contains(query, true)
                }
            }
        val currentState = _state.value!!
        _state.postValue(currentState.copy(
            results = filter,
            isLoading = false,
            canLoadMore = false,
        ))
    }

    private suspend fun fetchUsers() {
        if (members.isEmpty()) {
            return
        }
        val currentMembers = members
        val currentMemberIds = currentMembers.map { it.getUserId() }
        val currentState = _state.value!!
        when (val resultIds = ErmisClient.instance().getListContactIds().await()) {
            is Result.Success -> {
                val result = ErmisClient.instance().getUsersByIds(userIds = resultIds.value).await()
                when (result) {
                    is Result.Success -> {
                        val userFilter = (currentState.results + result.value).filter { currentMemberIds.contains(it.id).not() }
                        mListAllContact.clear()
                        mListAllContact.addAll(userFilter)
                        _state.value = currentState.copy(
                            results = mListAllContact,
                            isLoading = false,
                            canLoadMore = result.value.size == QUERY_LIMIT,
                        )
                        isLoadingMore = false
                    }
                    is Result.Failure -> _state.value = currentState.copy(
                        isLoading = false,
                        canLoadMore = true,
                    )
                }
            }
            is Result.Failure -> _state.value = currentState.copy(
                isLoading = false,
                canLoadMore = true,
            )
        }
    }

    data class State(
        val query: String,
        val canLoadMore: Boolean,
        val results: List<User>,
        val isLoading: Boolean,
        val membersSelected: Set<String>,
    )

    sealed class Action {
        data class UserClicked(val user: User) : Action()
        data object AddMembers : Action()
        data class SearchQueryChanged(val query: String) : Action()
        object LoadMoreRequested : Action()
    }

    sealed class ErrorEvent {
        object AddMemberError : ErrorEvent()
    }

    companion object {
        private const val QUERY_LIMIT = 30
        private val INITIAL_STATE = State(query = "", canLoadMore = true, results = emptyList(), isLoading = true, membersSelected = setOf())
    }
}
