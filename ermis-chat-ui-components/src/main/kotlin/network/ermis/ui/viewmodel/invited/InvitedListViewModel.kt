package network.ermis.ui.viewmodel.invited

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.core.errors.extractCause
import network.ermis.client.utils.extensions.channelIdToProjectId
import network.ermis.core.models.BeloFilterChannelObject
import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelMute
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Filters
import network.ermis.core.models.querysort.QuerySortByField
import network.ermis.core.models.querysort.QuerySorter
import network.ermis.state.event.chat.factory.ChatEventHandlerFactory
import network.ermis.state.extensions.globalState
import network.ermis.state.extensions.queryChannelsAsState
import network.ermis.state.plugin.state.global.GlobalState
import network.ermis.state.plugin.state.querychannels.ChannelsStateData
import network.ermis.state.plugin.state.querychannels.QueryChannelsState
import network.ermis.state.utils.Event
import network.ermis.ui.utils.extensions.addFlow
import network.ermis.ui.utils.extensions.getLastMessage
import network.ermis.ui.utils.extensions.isMuted
import network.ermis.ui.utils.extension.defaultChannelListFilter
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.enqueue
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

public class InvitedListViewModel(
    private val filter: FilterObject? = null,
    private val sort: QuerySorter<Channel> = DEFAULT_SORT,
    private val limit: Int = 30,
    private val messageLimit: Int = 1,
    private val memberLimit: Int = 30,
    private val chatEventHandlerFactory: ChatEventHandlerFactory = ChatEventHandlerFactory(),
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val globalState: GlobalState = chatClient.globalState,
) : ViewModel() {

    private var queryJob: Job? = null

    private val logger by taggedLogger("Chat:InvitedListViewModel")

    /**
     * Represents the current state containing channel list
     * information that is a product of multiple sources.
     */
    private val stateMerger = MediatorLiveData<State>()


    /**
     * Represents the current state containing channel list information.
     */
    public val state: LiveData<State> = stateMerger.distinctUntilChanged()

    /**
     * Filters the requested channels.
     */
    private val filterLiveData: MutableLiveData<FilterObject?> = MutableLiveData(filter)

    /**
     * Represents the current pagination state that is a product
     * of multiple sources.
     */
    private val paginationStateMerger = MediatorLiveData<PaginationState>()

    /**
     * Represents the current pagination state by containing
     * information about the loading state and if we have
     * reached the end of all available channels.
     */
    public val paginationState: LiveData<PaginationState> = paginationStateMerger.distinctUntilChanged()

    /**
     * Used to update and emit error events.
     */
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()

    /**
     * Emits error events.
     */
    public val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

    /**
     * Represents the current state of the channels query.
     */
    private var queryChannelsState: StateFlow<QueryChannelsState?> = MutableStateFlow(null)

    private val mUserIds = arrayListOf<String>()

    init {
        if (filter == null) {
            viewModelScope.launch {
                val filter = buildDefaultFilter().first()

                this@InvitedListViewModel.filterLiveData.value = filter
            }
        }

        stateMerger.addSource(filterLiveData) { filter ->
            if (filter != null) {
                initData(filter)
            }
        }
    }

    /**
     * Initializes the data necessary for the screen.
     */
    private fun initData(filterObject: FilterObject) {
        stateMerger.value = INITIAL_STATE
        init(filterObject)
    }

    /**
     * Initializes this ViewModel with OfflinePlugin implementation. It makes the initial query to request channels
     * and starts to observe state changes.
     */
    private fun init(filterObject: FilterObject) {
        val queryChannelsRequest =
            QueryChannelsRequest(
                filter = filterObject,
                querySort = sort,
                limit = limit,
                messageLimit = messageLimit,
                memberLimit = memberLimit,
            )
        queryChannelsState =
            chatClient.queryChannelsAsState(queryChannelsRequest, chatEventHandlerFactory, viewModelScope)

        logger.d { "Querying channels with request: $queryChannelsRequest" }
        /**
         * We clean up any previous loads to make sure the current one is the only one running.
         */
        queryJob?.cancel()
        val queryJob = Job(viewModelScope.coroutineContext.job).also {
            this.queryJob = it
        }

        viewModelScope.launch(queryJob) {
            queryChannelsState.filterNotNull().collectLatest { queryChannelsState ->
                if (!isActive) {
                    return@collectLatest
                }
                stateMerger.addFlow(queryJob, queryChannelsState.channelsStateData) { channelsState ->
                    stateMerger.value = handleChannelStateNews(channelsState, globalState.channelMutes.value)
                }
                stateMerger.addFlow(queryJob, globalState.channelMutes) { channelMutes ->
                    val state = stateMerger.value

                    if (state?.channels?.isNotEmpty() == true) {
                        stateMerger.value = state.copy(channels = parseMutedChannels(state.channels, channelMutes))
                    } else {
                        stateMerger.value = state?.copy()
                    }
                }

                paginationStateMerger.addFlow(queryJob, queryChannelsState.loadingMore) { loadingMore ->
                    setPaginationState { copy(loadingMore = loadingMore) }
                }
                paginationStateMerger.addFlow(queryJob, queryChannelsState.endOfChannels) { endOfChannels ->
                    setPaginationState { copy(endOfChannels = endOfChannels) }
                }
            }
        }
    }

    /**
     * Handles update about [ChannelsStateData] changes and emit new [State].
     *
     * @param channelState Current state of the channels query.
     * @param channelMutes List of muted channels.
     *
     * @return New [State] after handling channels state changes.
     */
    private fun handleChannelStateNews(
        channelState: ChannelsStateData,
        channelMutes: List<ChannelMute>,
    ): State {
        return when (channelState) {
            is ChannelsStateData.NoQueryActive,
            is ChannelsStateData.Loading,
            -> {
                State(isLoading = true, emptyList())
            }
            is ChannelsStateData.OfflineNoResults -> {
                State(
                    isLoading = false,
                    channels = emptyList(),
                )
            }
            is ChannelsStateData.Result -> {
                // TODO tuyendv sắp xếp lại danh sách channels hiển thị theo time giảm dần
                val channels = channelState.channels
                    // TODO tuyendv filter channel_role tạm (fix case new room creater from other user)
                    .filter {
                        it.membership?.channelRole in (filter as BeloFilterChannelObject).roles
                    }
                    .filter {
                        it.id.channelIdToProjectId().contains(chatClient.getProjectId() ?: "")
                    }
                    // TODO tuyendv sắp xếp lại danh sách channels hiển thị theo time giảm dần
                    .sortedByDescending {
                        it.lastMessageAt ?: it.getLastMessage()?.createdAt ?: it.createdAt
                    }
                // TODO tuyendv get info member in channels
                if (chatClient.isSocketConnected()) {
                    val newUserId = arrayListOf<String>()
                    channels.forEach { channel ->
                        newUserId.addAll(
                            channel.members
                                .filter { member ->
                                    member.user.id !in mUserIds
                                }
                                .filter { member ->
                                    member.user.id !in newUserId
                                }
                                .map { it.user.id }
                        )
                    }
                    if (newUserId.isNotEmpty()) {
                        chatClient.getUsersByIds(newUserId).enqueue { result ->
                            when (result) {
                                is Result.Success -> {
                                    mUserIds.addAll(newUserId)
                                    logger.d { "get info member in channels size=${result.value.size} names=${result.value.map { it.name }}" }
                                }
                                is Result.Failure -> logger.e { "get info member in channels Failure =${result.value}" }
                            }
                        }
                    }
                }
                State(
                    isLoading = false,
                    channels = parseMutedChannels(channels, channelMutes),
                )
            }
        }
    }

    /**
     * Checks against available actions and creates side-effects accordingly.
     *
     * @param action The action to process.
     */
    public fun onAction(action: Action) {
        when (action) {
            is Action.ReachedEndOfList -> requestMoreChannels()
            is Action.AcceptChannelInvited -> onAcceptChannelInvited(action.channel)
            is Action.DeclineChannelInvited -> onRejectChannelInvited(action.channel)
        }
    }

    /**
     * Requests more channels.
     * Called when scrolling to the end of the list.
     */
    private fun requestMoreChannels() {
        filterLiveData.value?.let {
            val queryChannelsState = queryChannelsState.value ?: return

            queryChannelsState.nextPageRequest.value?.let {
                viewModelScope.launch {
                    chatClient.queryChannels(it).enqueue(
                        onError = { streamError ->
                            logger.e {
                                "Could not load more channels. Error: ${streamError.message}. " +
                                    "Cause: ${streamError.extractCause()}"
                            }
                        },
                    )
                }
            }
        }
    }

    private fun onAcceptChannelInvited(channel: Channel) {
        viewModelScope.launch {
            chatClient
                .acceptInvite(channelType = channel.type, channelId = channel.id)
                .enqueue(
                    onError = { error->
                        logger.e {
                            "Could not join channel with id: ${channel.id}. " +
                                "Error: ${error.message}. Cause: ${error.extractCause()}"
                        }
                        _errorEvents.postValue(Event(ErrorEvent.AcceptInviteError(error)))
                    }
                )
        }
    }

    private fun onRejectChannelInvited(channel: Channel) {
        viewModelScope.launch {
            chatClient
                .rejectInvite(channelType = channel.type, channelId = channel.id)
                .enqueue(
                    onError = { error->
                        logger.e {
                            "Could not reject Invite channel with id: ${channel.id}. " +
                                "Error: ${error.message}. Cause: ${error.extractCause()}"
                        }
                        _errorEvents.postValue(Event(ErrorEvent.RejectInvitedError(error)))
                    }
                )
        }
    }

    /**
     * Describes the actions that were taken.
     */
    public sealed class ErrorEvent(public open val error: Error) {
        public data class AcceptInviteError(val streamError: Error) : ErrorEvent(streamError)

        public data class RejectInvitedError(val streamError: Error) : ErrorEvent(streamError)
    }


    /**
     * Builds the default channel filter, which represents "messaging" channels that the current user is a part of.
     */
    private fun buildDefaultFilter(): Flow<FilterObject> {
        return chatClient.clientState.user.map(Filters::defaultChannelListFilter).filterNotNull()
    }

    /**
     * Sets the current pagination state.
     *
     * @param reducer A lambda function that returns [PaginationState].
     */
    private fun setPaginationState(reducer: PaginationState.() -> PaginationState) {
        paginationStateMerger.value = reducer(paginationStateMerger.value ?: PaginationState())
    }

    /**
     * Described the state of the list of channels.
     *
     * @param isLoading If the list is currently loading.
     * @param channels The list of channels to be displayed.
     */
    public data class State(val isLoading: Boolean, val channels: List<Channel>)

    /**
     * Takes in a list of channels and returns the muted ones.
     *
     * @param channels The list of channels to be filtered.
     * @param channelMutes The list of muted channels.
     */
    private fun parseMutedChannels(
        channels: List<Channel>,
        channelMutes: List<ChannelMute>,
    ): List<Channel> {
        val mutedChannelsIds = channelMutes.map { channelMute -> channelMute.channel.id }.toSet()
        return channels.map { channel ->
            when {
                channel.isMuted != channel.id in mutedChannelsIds ->
                    channel.copy(extraData = channel.extraData.clone(network.ermis.ui.utils.extensions.EXTRA_DATA_MUTED, !channel.isMuted))

                else -> channel
            }
        }
    }

    /**
     * Clones the given map while changing the given key-value pair.
     */
    private fun <K, V> Map<K, V>.clone(changeKey: K, changeValue: V): MutableMap<K, V> {
        val originalMap = this

        return mutableMapOf<K, V>().apply {
            putAll(originalMap)
            put(changeKey, changeValue)
        }
    }

    public sealed class Action {
        public object ReachedEndOfList : Action() {
            override fun toString(): String = "ReachedEndOfList"
        }

        public class AcceptChannelInvited(public val channel: Channel) : Action() {
            override fun toString(): String = "AcceptChannelInvited"
        }

        public class DeclineChannelInvited(public val channel: Channel) : Action() {
            override fun toString(): String = "DeclineChannelInvited"
        }
    }

    /**
     * Describes the pagination state.
     *
     * @param loadingMore If we are currently loading more channels.
     * @param endOfChannels If we have reached the end of all available channels
     * for the current user.
     */
    public data class PaginationState(
        val loadingMore: Boolean = false,
        val endOfChannels: Boolean = false,
    )

    public companion object {

        /**
         * The default sorting option for queries.
         */
        @JvmField
        public val DEFAULT_SORT: QuerySorter<Channel> = QuerySortByField.descByName("last_updated")

        /**
         *  The initial state.
         */
        private val INITIAL_STATE: State =
            State(isLoading = true, channels = emptyList())

        /**
         * Default value of number of channels to return when querying channels.
         */
        internal const val DEFAULT_CHANNEL_LIMIT = 30

        /**
         * Default value of the number of messages to include in each channel when querying channels.
         */
        internal const val DEFAULT_MESSAGE_LIMIT = 1

        /**
         * Default value of the number of members to include in each channel when querying channels.
         */
        internal const val DEFAULT_MEMBER_LIMIT = 30
    }
}
