package network.ermis.sample.feature.channel.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryUsersRequest
import network.ermis.client.channel.ChannelClient
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Filters
import network.ermis.core.models.User
import network.ermis.core.models.querysort.QuerySortByField
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import io.getstream.result.call.Call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.ermis.state.utils.Event as EventWrapper

class AddChannelViewModel : ViewModel() {

    private val logger by taggedLogger("Chat:AddChannelViewModel")
    private val chatClient = ErmisClient.instance()
    private val _state: MutableLiveData<State> = MutableLiveData()
    private val _paginationState: MutableLiveData<PaginationState> = MutableLiveData()
    private val _errorEvents: MutableLiveData<EventWrapper<ErrorEvent>> = MutableLiveData()
    val state: LiveData<State> = _state
    val paginationState: LiveData<PaginationState> = _paginationState
    val errorEvents: LiveData<EventWrapper<ErrorEvent>> = _errorEvents

    private var channelClient: ChannelClient? = null
    private var searchQuery: String = ""
    private var page: Int = 1
    private var latestSearchCall: Call<List<User>>? = null

    init {
        requestUsers(isRequestingMore = false)
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.ReachedEndOfList -> requestMoreUsers()
            Event.MessageSent -> createChannel()
            is Event.MembersChanged -> createDraftChannel(event.members)
            is Event.SearchInputChanged -> searchUsers(event.query)
        }
    }

    private fun requestUsers(isRequestingMore: Boolean) {
        // TODO tuyendv lấy dữ liệu danh sách user chỉ từ api
        if (!isRequestingMore) {
            _state.value = State.Loading
        }
        latestSearchCall?.cancel()
        latestSearchCall = chatClient.queryUsers(searchQuery, page, USERS_LIMIT)

        latestSearchCall?.enqueue { result ->
            if (result is Result.Success) {
                val users = result.value
                _state.postValue(if (isRequestingMore) State.ResultMoreUsers(users) else State.Result(users))
                updatePaginationData(users)
            }
        }
    }

    private fun createSearchQuery(
        querySearch: String,
        page: Int,
        usersLimit: Int,
    ): QueryUsersRequest {
        return QueryUsersRequest(
            name = querySearch,
            page = page,
            limit = usersLimit,
        )
    }

    private fun createFilter(defaultFilter: FilterObject, optionalFilter: FilterObject?): FilterObject {
        return if (optionalFilter != null) {
            Filters.and(defaultFilter, optionalFilter)
        } else {
            defaultFilter
        }
    }

    private fun createChannel() {
        val client = requireNotNull(channelClient) { "Cannot create Channel without initializing ChannelClient" }
        _state.postValue(State.NavigateToChannel(client.cid))
        // viewModelScope.launch(Dispatchers.IO) {
        //     when (val result = client.update(message = null, extraData = mapOf(CHANNEL_ARG_DRAFT to false)).await()) {
        //         is Result.Success -> _state.postValue(State.NavigateToChannel(result.value.cid))
        //         is Result.Failure -> _errorEvents.postValue(EventWrapper(ErrorEvent.CreateChannelError))
        //     }
        // }
    }

    private fun createDraftChannel(members: List<User>) {
        if (members.isEmpty()) {
            channelClient = null
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId =
                chatClient.clientState.user.value?.id ?: error("User must be set before create new channel!")
            val result = chatClient.createChannel(
                channelType = CHANNEL_TYPE_MESSAGING,
                channelId = "",
                memberIds = members.map(User::id) + currentUserId,
                extraData = mapOf(),
            ).await()
            if (result is Result.Success) {
                val cid = result.value.cid
                channelClient = ErmisClient.instance().channel(cid)
                _state.postValue(State.InitializeChannel(cid))
            }
        }
    }

    private fun searchUsers(query: String) {
        page = 1
        searchQuery = query
        requestUsers(isRequestingMore = false)
    }

    private fun requestMoreUsers() {
        _paginationState.value = _paginationState.value?.copy(loadingMore = true) ?: PaginationState(loadingMore = true)
        requestUsers(isRequestingMore = true)
    }

    private fun updatePaginationData(result: List<User>) {
        page ++
        _paginationState.postValue(PaginationState(loadingMore = false, endReached = result.size < USERS_LIMIT))
    }

    companion object {
        private const val USERS_LIMIT = 30
        private const val CHANNEL_TYPE_MESSAGING = "messaging"
        private const val CHANNEL_TYPE_TEAM = "team"


        private val USERS_QUERY_SORT = QuerySortByField.ascByName<User>("name")

        private const val FIELD_NAME = "name"
        private const val FIELD_ID = "id"
    }

    sealed class State {
        object Loading : State()
        data class InitializeChannel(val cid: String) : State()
        data class Result(val users: List<User>) : State()
        data class ResultMoreUsers(val users: List<User>) : State()
        data class NavigateToChannel(val cid: String) : State()
    }

    sealed class Event {
        object ReachedEndOfList : Event()
        object MessageSent : Event()
        data class MembersChanged(val members: List<User>) : Event()
        data class SearchInputChanged(val query: String) : Event()
    }

    sealed class ErrorEvent {
        object CreateChannelError : ErrorEvent()
    }

    data class PaginationState(
        val loadingMore: Boolean = false,
        val endReached: Boolean = false,
    )
}
