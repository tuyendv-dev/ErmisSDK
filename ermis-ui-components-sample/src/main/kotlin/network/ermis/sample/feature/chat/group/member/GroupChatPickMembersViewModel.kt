package network.ermis.sample.feature.chat.group.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import network.ermis.client.ErmisClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Member
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event

class GroupChatPickMembersViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    private val logger by taggedLogger("GroupChatPickMembersViewModel")

    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: Flow<ChannelState> =
        chatClient.watchChannelAsState(cid, 0, viewModelScope).filterNotNull()

    private val _events = MutableLiveData<Event<UiEvent>>()
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()
    val events: LiveData<Event<UiEvent>> = _events
    val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

    private val _state = MediatorLiveData<State>()
    val state: LiveData<State> = _state

    init {
        _state.value = State(members = listOf(), membersSelected = setOf())
        // Update members
        _state.addSource(channelState.flatMapLatest { it.members }.asLiveData(), this::updateMembers)
    }

    private fun updateMembers(members: List<Member>) {
        val currentState = _state.value!!
        _state.value =
            currentState.copy(
                members = members
            )
    }

    fun onAction(action: Action) {
        logger.d { "[onAction] action: $action" }
        viewModelScope.launch {
            when (action) {
                is Action.MemberClicked -> selectedMember(action.member)
                is Action.MemberBanned -> banMemberFromChannel()
                is Action.MemberPromote -> promoteMemberFromChannel()
            }
        }
    }

    private fun selectedMember(member: Member) {
        var membersSelected = _state.value!!.membersSelected
        membersSelected = if (membersSelected.contains(member.getUserId())) {
            membersSelected.minus(member.getUserId())
        } else {
            membersSelected.plus(member.getUserId())
        }
        _state.postValue(_state.value!!.copy(membersSelected = membersSelected))
    }

    private fun banMemberFromChannel() {
        viewModelScope.launch {
            val membersSelected = _state.value!!.membersSelected
            when (val result = chatClient.channel(cid).banMembersChannel(membersSelected.toList()).await()) {
                is Result.Success -> {
                    _state.postValue(_state.value!!.copy(membersSelected = setOf()))
                    _events.value = Event(UiEvent.BannedMemberSuccess)
                }

                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.UpdateMemberError(result.value.message)))
            }
        }
    }

    private fun promoteMemberFromChannel() {
        viewModelScope.launch {
            val membersSelected = _state.value!!.membersSelected
            when (val result = chatClient.channel(cid).promoteMembersChannel(membersSelected.toList()).await()) {
                is Result.Success -> {
                    _state.postValue(_state.value!!.copy(membersSelected = setOf()))
                    _events.value = Event(UiEvent.PromoteMemberSuccess)
                }

                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.UpdateMemberError(result.value.message)))
            }
        }
    }

    data class State(
        val members: List<Member>,
        val membersSelected: Set<String>,
    )

    sealed class Action {
        data class MemberClicked(val member: Member) : Action()
        data object MemberBanned : Action()
        data object MemberPromote : Action()
    }

    sealed class UiEvent {
        data object BannedMemberSuccess : UiEvent()
        data object PromoteMemberSuccess : UiEvent()
    }

    sealed class ErrorEvent {
        data class UpdateMemberError(val error: String) : ErrorEvent()
    }
}
