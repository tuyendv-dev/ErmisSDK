
package network.ermis.sample.feature.chat.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.channel.ChannelClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.setup.ClientState
import network.ermis.core.models.ChannelCapabilities
import network.ermis.core.models.ChannelMute
import network.ermis.core.models.Filters
import network.ermis.core.models.Member
import network.ermis.core.models.User
import network.ermis.core.models.querysort.QuerySortByField
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ChatInfoViewModel(
    private val cid: String?,
    userData: UserData?,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: Flow<ChannelState> =
        chatClient.watchChannelAsState(cid ?: "", 0, viewModelScope).filterNotNull()
    private val logger by taggedLogger("ChatInfoViewModel")
    private lateinit var channelClient: ChannelClient
    private val _state = MediatorLiveData<State>()
    private val _channelDeletedState = MutableLiveData(false)
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()
    val state: LiveData<State> = _state
    val channelDeletedState: LiveData<Boolean> = _channelDeletedState
    val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

    init {
        if (cid != null) {
            channelClient = chatClient.channel(cid)
            _state.value = State()
            viewModelScope.launch {
                // Update channel mute status
                clientState.user.value?.channelMutes?.let(::updateChannelMuteStatus)

                _state.addSource(channelState.flatMapLatest { it.members }.asLiveData()) { memberList ->
                    // Updates only if the user state is already set
                    memberList.find { member -> member.user.id == _state.value?.member?.user?.id }?.let { member ->
                        _state.value = _state.value?.copy(member = member)
                    }
                }
                _state.addSource(channelState.flatMapLatest { it.channelData }.asLiveData()) { channelData ->
                    _state.value = _state.value?.copy(
                        createdBy = channelData.createdBy,
                        canDeleteChannel = channelData.ownCapabilities.contains(ChannelCapabilities.DELETE_CHANNEL),
                    )
                }
                // Currently, we don't receive any event when channel member is banned/shadow banned, so
                // we need to get member data from the server
                val result =
                    channelClient.queryMembers(
                        offset = 0,
                        limit = 1,
                        filter = clientState.user.value?.id?.let { Filters.ne("id", it) } ?: Filters.neutral(),
                        sort = QuerySortByField(),
                    ).await()

                when (result) {
                    is Result.Success -> {
                        val member = result.value.firstOrNull()
                        // Update member, member block status, and channel notifications
                        _state.value = _state.value!!.copy(
                            member = member,
                            isMemberBlocked = member?.shadowBanned ?: false,
                            loading = false,
                        )
                    }
                    is Result.Failure -> _state.value = _state.value!!.copy(loading = false)
                }
            }
        } else {
            _state.value =
                State(
                    member = Member(user = userData!!.toUser()),
                    canDeleteChannel = false,
                    channelExists = false,
                    loading = false,
                )
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.OptionMuteDistinctChannelClicked -> switchChannelMute(action.isEnabled)
            is Action.OptionBlockUserClicked -> switchUserBlock(action.isEnabled)
            is Action.ChannelMutesUpdated -> updateChannelMuteStatus(action.channelMutes)
            is Action.ChannelDeleted -> deleteChannel()
        }
    }

    private fun updateChannelMuteStatus(channelMutes: List<ChannelMute>) {
        _state.postValue(_state.value!!.copy(channelMuted = channelMutes.any { it.channel.cid == cid }))
    }

    private fun switchChannelMute(isEnabled: Boolean) {
        viewModelScope.launch {
            val result = if (isEnabled) {
                channelClient.mute().await()
            } else {
                channelClient.unmute().await()
            }
            if (result is Result.Failure) {
                _errorEvents.postValue(Event(ErrorEvent.MuteChannelError))
            }
        }
    }

    private fun switchUserBlock(isEnabled: Boolean) {
        viewModelScope.launch {
            val currentState = _state.value!!
            if (currentState.member == null) {
                return@launch
            }
            val result = if (isEnabled) {
                channelClient.shadowBanUser(
                    targetId = currentState.member.getUserId(),
                    reason = null,
                    timeout = null,
                ).await()
            } else {
                channelClient.removeShadowBan(currentState.member.getUserId()).await()
            }
            if (result is Result.Failure) {
                _errorEvents.postValue(Event(ErrorEvent.BlockUserError))
            }
        }
    }

    /**
     * Deletes the current channel.
     */
    private fun deleteChannel() {
        val cid = requireNotNull(cid)
        viewModelScope.launch {
            when (chatClient.channel(cid).delete().await()) {
                is Result.Success -> _channelDeletedState.value = true
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.DeleteChannelError))
            }
        }
    }

    data class State(
        val member: Member? = null,
        val createdBy: User = User(),
        val channelMuted: Boolean = false,
        val isMemberBlocked: Boolean = false,
        val canDeleteChannel: Boolean = false,
        val channelExists: Boolean = true,
        val loading: Boolean = true,
    )

    sealed class Action {
        data class OptionMuteDistinctChannelClicked(val isEnabled: Boolean) : Action()
        data class OptionBlockUserClicked(val isEnabled: Boolean) : Action()
        data class ChannelMutesUpdated(val channelMutes: List<ChannelMute>) : Action()
        object ChannelDeleted : Action()
    }

    sealed class ErrorEvent {
        object MuteChannelError : ErrorEvent()
        object BlockUserError : ErrorEvent()
        object DeleteChannelError : ErrorEvent()
    }
}

class ChatInfoViewModelFactory(private val cid: String?, private val userData: UserData?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(cid != null || userData != null) {
            "Either cid or userData should not be null"
        }
        require(modelClass == ChatInfoViewModel::class.java) {
            "ChatInfoViewModelFactory can only create instances of ChatInfoViewModel"
        }

        @Suppress("UNCHECKED_CAST")
        return ChatInfoViewModel(cid, userData) as T
    }
}
