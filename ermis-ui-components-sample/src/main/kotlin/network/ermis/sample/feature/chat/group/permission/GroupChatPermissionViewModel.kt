package network.ermis.sample.feature.chat.group.permission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.channel.ChannelClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Channel
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class GroupChatPermissionViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    private val logger by taggedLogger("GroupChatPermissionViewModel")

    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: Flow<ChannelState> =
        chatClient.watchChannelAsState(cid, 0, viewModelScope).filterNotNull()

    private val channelClient: ChannelClient = chatClient.channel(cid)
    private val _events = MutableLiveData<Event<UiEvent>>()
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()
    val events: LiveData<Event<UiEvent>> = _events
    val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

    private val _state = MediatorLiveData<State>()
    val state: LiveData<State> = _state

    val channel: LiveData<Channel> =
        channelState.flatMapLatest { state ->
            combine(
                state.channelData,
                state.membersCount,
                state.watcherCount,
            ) { _, _, _ ->
                state.toChannel()
            }
        }.asLiveData()

    init {
        _state.value = INITIAL_STATE
        _state.addSource(channelState.flatMapLatest { it.channelData }.asLiveData()) { channelData ->
            _state.value = _state.value?.copy(
                ownCapabilities = channelData.ownCapabilities,
                memberCapabilities = channelData.memberCapabilities,
            )
        }
    }

    fun onAction(action: Action) {
        viewModelScope.launch {
            when (action) {
                is Action.PermissionChange -> changePermissionMember(action)
            }
        }
    }

    private fun changePermissionMember(action: Action.PermissionChange) {

        chatClient.channel(cid).updatePermisionMembersChannel(add = action.add, delete = action.delete)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> _events.value = Event(UiEvent.PermissionChangeSuccess)
                    is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.PermissionChangeError(result.value.message)))
                }
            }
    }

    data class State(
        val ownCapabilities: Set<String>,
        val memberCapabilities: Set<String>,
    )

    sealed class Action {
        data class PermissionChange(val add: List<String>, val delete: List<String>) : Action()
    }

    sealed class UiEvent {
        data object PermissionChangeSuccess : UiEvent()
    }

    sealed class ErrorEvent {
        data class PermissionChangeError(val error: String) : ErrorEvent()
    }

    companion object {

        private val INITIAL_STATE = State(
            emptySet(),
            emptySet(),
        )
    }
}
