package network.ermis.sample.feature.chat.group.member

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
import network.ermis.core.models.Message
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class GroupChatMembersViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    private val logger by taggedLogger("GroupChatMembersViewModel")

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
    var selectedMembers = setOf<String>()

    fun onAction(action: Action) {
        logger.d { "[onAction] action: $action" }
        viewModelScope.launch {
            when (action) {
                is Action.MemberClicked -> selectedMember(action.member)
                is Action.LeaveChannelClicked -> leaveChannel()
                is Action.MemberRemove -> removeFromChannel(action.member)
            }
        }
    }

    private fun selectedMember(member: Member) {
        if (selectedMembers.contains(member.getUserId())) {
            selectedMembers.minus(member.getUserId())
        } else {
            selectedMembers.plus(member.getUserId())
        }
    }

    private fun removeFromChannel(member: Member) {
        viewModelScope.launch {
            val message = Message(text = "${member.user.name} has been removed from this channel")
            when (val result = chatClient.channel(cid).removeMembers(listOf(member.getUserId()), message).await()) {
                is Result.Success -> _events.value = Event(UiEvent.RemoveMemberSuccess)
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.UpdateMemberError(result.value.message)))
            }
        }
    }

    private fun leaveChannel() {
        viewModelScope.launch {
            val result = clientState.user.value?.let { user ->
                val message = Message(text = "${user.name} left")
                chatClient.channel(channelClient.channelType, channelClient.channelId)
                    .removeMembers(listOf(user.id), message)
                    .await()
            } ?: return@launch

            when (result) {
                is Result.Success -> _events.value = Event(UiEvent.RedirectToHome)
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.UpdateMemberError(result.value.message)))
            }
        }
    }

    sealed class Action {
        data class MemberClicked(val member: Member) : Action()
        data class MemberRemove(val member: Member) : Action()
        data object LeaveChannelClicked : Action()
    }

    sealed class UiEvent {
        data object RedirectToHome : UiEvent()
        data object RemoveMemberSuccess : UiEvent()
    }

    sealed class ErrorEvent {
        data class UpdateMemberError(val error: String) : ErrorEvent()
    }
}
