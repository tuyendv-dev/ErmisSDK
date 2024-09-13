package network.ermis.sample.feature.chat.info.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ChannelSearchMessageViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData(State())

    /**
     * The current state of the search screen.
     */
    public val state: LiveData<State> = _state

    private val _errorEvents: MutableLiveData<Event<Unit>> = MutableLiveData()

    /**
     * One shot error events when search fails.
     */
    public val errorEvents: LiveData<Event<Unit>> = _errorEvents

    /**
     * Represents an ongoing search network request.
     */
    private var job: Job? = null

    private val logger by taggedLogger("Chat:ChannelSearchMessageViewModel")

    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: Flow<ChannelState> =
        chatClient.watchChannelAsState(cid, 0, viewModelScope).filterNotNull()

    val members: LiveData<List<Member>> =
        channelState.flatMapLatest { it.members }.asLiveData()

    /**
     * Changes the current query state. An empty search query
     */
    public fun setQuery(query: String) {
        job?.cancel()

        if (query.length < 2) {
            _state.value = State(
                query = query,
                results = emptyList(),
                canLoadMore = false,
                isLoading = false,
                isLoadingMore = false,
            )
        } else {
            _state.value = State(
                query = query,
                results = emptyList(),
                canLoadMore = true,
                isLoading = true,
                isLoadingMore = false,
            )
            searchMessages()
        }
    }

    /**
     * Loads more data when the user reaches the end of the found message list.
     *
     * Does nothing of the end of the search result list has already been reached or loading
     * is already in progress.
     */
    public fun loadMore() {
        job?.cancel()

        val currentState = _state.value!!
        if (currentState.canLoadMore && !currentState.isLoading && !currentState.isLoadingMore) {
            _state.value = currentState.copy(isLoadingMore = true)
            searchMessages()
        }
    }

    /**
     * Performs message search based on the current state.
     */
    private fun searchMessages() {
        viewModelScope.launch {
            val currentState = _state.value!!
            val result = searchMessages(
                query = currentState.query,
                offset = currentState.results.size,
            )
            when (result) {
                is Result.Success -> {
                    val messages = result.value.map { message ->
                        message.copy(
                            user = (members.value?.firstOrNull { it.user.id == message.user.id }?.user ?: User(
                                id = message.user.id
                            )))
                    }
                    handleSearchMessageSuccess(messages)
                }
                is Result.Failure -> {
                    handleSearchMessagesError(result.value)
                }
            }
        }
    }

    /**
     * Notifies the UI about the search results and enables the pagination.
     */
    private fun handleSearchMessageSuccess(messages: List<Message>) {
        logger.d { "Found messages: ${messages.size}" }
        val currentState = _state.value!!
        _state.value = currentState.copy(
            results = currentState.results + messages,
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = messages.size == QUERY_LIMIT,
        )
    }

    /**
     * Notifies the UI about the error and enables the pagination.
     */
    private fun handleSearchMessagesError(streamError: Error) {
        logger.d { "Error searching messages: ${streamError.message}" }
        _state.value = _state.value!!.copy(
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = true,
        )
        _errorEvents.value = Event(Unit)
    }

    /**
     * Searches messages containing [query] text across channels where the current user is a member.
     *
     * @param query The search query.
     * @param offset The pagination offset offset.
     */
    private suspend fun searchMessages(query: String, offset: Int): Result<List<Message>> {
        logger.d { "Searching for \"$query\" with offset: $offset" }
        return chatClient.searchMessageOfChannel(
            cid = cid,
            searchTerm = query,
            offset = offset,
            limit = QUERY_LIMIT,
        ).await()
    }

    /**
     * Represents the search screen state, used to render the required UI.
     *
     * @param query The current search query value.
     * @param results The found messages to render.
     * @param canLoadMore If we've reached the end of messages, to stop triggering pagination.
     * @param isLoading If we're currently loading data (initial load).
     * @param isLoadingMore If we're loading more items (pagination).
     */
    public data class State(
        val query: String = "",
        val canLoadMore: Boolean = true,
        val results: List<Message> = emptyList(),
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
    )

    private companion object {
        private const val QUERY_LIMIT = 30
    }
}
