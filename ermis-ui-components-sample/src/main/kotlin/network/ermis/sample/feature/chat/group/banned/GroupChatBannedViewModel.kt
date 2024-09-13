package network.ermis.sample.feature.chat.group.banned

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.channel.ChannelClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Member
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class GroupChatBannedViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    private val logger by taggedLogger("GroupChatBannedViewModel")

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

    val members: LiveData<List<Member>> =
        channelState.flatMapLatest { it.members }.asLiveData()

    private fun unbanMemberFromChannel(member: Member) {
        viewModelScope.launch {
            when (val result = chatClient.channel(cid).unbanMembersChannel(listOf(member.getUserId())).await()) {
                is Result.Success -> _events.value = Event(UiEvent.UnBanMemberSuccess)
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.UnBanMemberError(result.value.message)))
            }
        }
    }

    fun onAction(action: Action) {
        viewModelScope.launch {
            when (action) {
                is Action.MemberClicked -> unbanMemberFromChannel(action.member)
            }
        }
    }

    sealed class Action {
        data class MemberClicked(val member: Member) : Action()
    }

    sealed class UiEvent {
        data object UnBanMemberSuccess : UiEvent()
    }

    sealed class ErrorEvent {
        data class UnBanMemberError(val error: String) : ErrorEvent()
    }
}
