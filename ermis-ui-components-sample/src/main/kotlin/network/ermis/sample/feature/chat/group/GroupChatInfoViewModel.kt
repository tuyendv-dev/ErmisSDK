
package network.ermis.sample.feature.chat.group

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
import network.ermis.core.models.ChannelMute
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.User
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.io.File

class GroupChatInfoViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    private val logger by taggedLogger("GroupChatInfo-VM")

    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: Flow<ChannelState> =
        chatClient.watchChannelAsState(cid, 0, viewModelScope).filterNotNull()

    private val channelClient: ChannelClient = chatClient.channel(cid)
    private val _state = MediatorLiveData<State>()
    private val _events = MutableLiveData<Event<UiEvent>>()
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()
    val events: LiveData<Event<UiEvent>> = _events
    val state: LiveData<State> = _state
    val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

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

        // Update channel mute status
        clientState.user.value?.channelMutes?.let(::updateChannelMuteStatus)

        // Update members
        _state.addSource(channelState.flatMapLatest { it.members }.asLiveData(), this::updateMembers)

        _state.addSource(channelState.flatMapLatest { it.channelData }.asLiveData()) { channelData ->
            _state.value = _state.value?.copy(
                memberShip = channelData.membership,
                ownCapabilities = channelData.ownCapabilities,
                createdBy = channelData.createdBy,
            )
        }

        _state.addSource(channelState.flatMapLatest { state ->
            combine(
                state.channelData,
                state.membersCount,
            ) { _, _ ->
                state.toChannel()
            }
        }.asLiveData()) { channel ->
            _state.value = _state.value?.copy(
                channel = channel,
            )
        }

        _state.addSource(
            channelState.flatMapLatest { it.hidden }
                .distinctUntilChanged()
                .take(1) // TODO we use take(1), cause ChannelState.hidden seems to be not updated properly
                .asLiveData(),
        ) { hidden ->
            logger.v { "[onHiddenChanged] hidden: $hidden" }
            _state.value = _state.value?.copy(
                channelHidden = hidden,
            )
        }
    }

    fun onAction(action: Action) {
        logger.d { "[onAction] action: $action" }
        viewModelScope.launch {
            when (action) {
                is Action.NameAndDescriptionChanged -> changeGroupNameAndDes(action.name, action.description)
                is Action.MemberClicked -> handleMemberClick(action.member)
                is Action.MembersSeparatorClicked -> _state.value = _state.value!!.copy(shouldExpandMembers = true)
                is Action.MuteChannelClicked -> switchGroupMute(action.isEnabled)
                is Action.HideChannelClicked -> switchGroupHide(action.isHidden, action.clearHistory)
                is Action.ChannelMutesUpdated -> updateChannelMuteStatus(action.channelMutes)
                is Action.ChannelHiddenUpdated -> updateChannelHideStatus(action.cid, action.hidden)
                is Action.LeaveChannelClicked -> leaveChannel()
                is Action.UploadAvatarChannel -> uploadAvatarChannel(action.file)
                is Action.SaveUpdateChannel -> saveUpdateChannel()
            }
        }
    }

    private fun saveUpdateChannel() {
        viewModelScope.launch {
            val mapEdit = hashMapOf<String,String>()
            if (_state.value?.channelNameEdit.isNullOrEmpty().not()) {
                mapEdit.put("name", _state.value?.channelNameEdit!!)
            }
            if (_state.value?.channelDescriptionEdit.isNullOrEmpty().not()) {
                mapEdit.put("description", _state.value?.channelDescriptionEdit!!)
            }
            if (_state.value?.channelAvatarEdit.isNullOrEmpty().not()) {
                mapEdit.put("image", _state.value?.channelAvatarEdit!!)
            }
            if (mapEdit.isNotEmpty()) {
                val result = channelClient.update(mapEdit).await()
                when (result) {
                    is Result.Success -> _events.postValue(Event(UiEvent.UpdateChannelSuccess))
                    is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.ErrorOther(result.value.message)))
                }
            }
        }
    }

    private fun uploadAvatarChannel(file: File) {
        channelClient.sendFile(file).enqueue { result ->
            when (result) {
                is Result.Success -> {
                    _state.value = _state.value?.copy(
                        channelAvatarEdit = result.value.file,
                    )
                }
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.ErrorOther(result.value.message)))
            }
        }
    }

    private fun handleMemberClick(member: Member) {
        if (member.getUserId() != clientState.user.value?.id) {
            val currentState = _state.value!!
            _events.value = Event(UiEvent.ShowMemberOptions(member, currentState.channel.name))
        }
    }

    private fun changeGroupNameAndDes(name: String, des: String) {
        _state.value = _state.value?.copy(
            channelNameEdit = name,
            channelDescriptionEdit = des
        )
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
                is Result.Success -> _events.value = Event(UiEvent.LeaveChannelSuccess)
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.LeaveChannelError))
            }
        }
    }

    private fun updateMembers(members: List<Member>) {
        val currentState = _state.value!!
        _state.value =
            currentState.copy(
                members = members,
                shouldExpandMembers = currentState.shouldExpandMembers ?: false || members.size <= COLLAPSED_MEMBERS_COUNT,
                membersToShowCount = members.size - COLLAPSED_MEMBERS_COUNT,
            )
    }

    private fun updateChannelMuteStatus(channelMutes: List<ChannelMute>) {
        _state.value = _state.value!!.copy(channelMuted = channelMutes.any { it.channel.cid == cid })
    }

    private fun updateChannelHideStatus(eventCid: String, hidden: Boolean) {
        if (eventCid != cid) return
        logger.v { "[updateChannelHideStatus] hidden: $hidden" }
        _state.value = _state.value!!.copy(channelHidden = hidden)
    }

    private fun switchGroupMute(isEnabled: Boolean) {
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

    private fun switchGroupHide(hide: Boolean, clearHistory: Boolean?) {
        logger.v { "[switchGroupHide] hide: $hide, clearHistory: $clearHistory" }
        viewModelScope.launch {
            val result = if (hide) {
                channelClient.hide(clearHistory = clearHistory == true).await()
            } else {
                channelClient.show().await()
            }
            if (result is Result.Failure) {
                _errorEvents.postValue(Event(ErrorEvent.HideChannelError))
            }
        }
    }

    data class State(
        val members: List<Member>,
        val memberShip: Member?,
        val createdBy: User,
        val channelNameEdit: String,
        val channelDescriptionEdit: String,
        val channelAvatarEdit: String,
        val channelMuted: Boolean,
        val channelHidden: Boolean,
        val shouldExpandMembers: Boolean?,
        val membersToShowCount: Int,
        val ownCapabilities: Set<String>,
        val channel: Channel,
    )

    sealed class Action {
        data class NameAndDescriptionChanged(val name: String, val description: String) : Action()
        data class UploadAvatarChannel(val file: File) : Action()
        data object SaveUpdateChannel : Action()
        data class MemberClicked(val member: Member) : Action()
        data object MembersSeparatorClicked : Action()
        data class MuteChannelClicked(val isEnabled: Boolean) : Action()
        data class HideChannelClicked(val isHidden: Boolean, val clearHistory: Boolean? = null) : Action()
        data class ChannelMutesUpdated(val channelMutes: List<ChannelMute>) : Action()

        data class ChannelHiddenUpdated(
            val cid: String,
            val hidden: Boolean,
            val clearHistory: Boolean? = null,
        ) : Action()
        data object LeaveChannelClicked : Action()
    }

    sealed class UiEvent {
        data class ShowMemberOptions(val member: Member, val channelName: String) : UiEvent()
        data object LeaveChannelSuccess : UiEvent()
        data object UpdateChannelSuccess : UiEvent()
    }

    sealed class ErrorEvent {
        data object ChangeGroupNameError : ErrorEvent()
        data object MuteChannelError : ErrorEvent()
        data object HideChannelError : ErrorEvent()
        data object LeaveChannelError : ErrorEvent()
        data class ErrorOther(val error: String) : ErrorEvent()
    }

    companion object {
        const val COLLAPSED_MEMBERS_COUNT = 5

        private val INITIAL_STATE = State(
            members = emptyList(),
            memberShip = null,
            createdBy = User(),
            channelNameEdit = "",
            channelDescriptionEdit = "",
            channelAvatarEdit = "",
            channelMuted = false,
            channelHidden = false,
            shouldExpandMembers = null,
            membersToShowCount = 0,
            emptySet(),
            Channel(),
        )
    }
}
