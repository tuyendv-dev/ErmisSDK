
package network.ermis.sample.feature.chat.group.member

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.core.models.Filters
import network.ermis.core.models.Message
import network.ermis.core.models.querysort.QuerySortByField
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.launch

class GroupChatInfoMemberOptionsViewModel(
    private val cid: String,
    private val memberId: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
) : ViewModel() {

    companion object {
        private const val CHANNEL_TYPE_MESSAGING = "messaging"
    }

    private val logger by taggedLogger("GroupChatInfoMemberOptionsViewModel")
    private val _events = MutableLiveData<Event<UiEvent>>()
    private val _state: MediatorLiveData<State> = MediatorLiveData()
    private val _errorEvents: MutableLiveData<Event<ErrorEvent>> = MutableLiveData()
    val events: LiveData<Event<UiEvent>> = _events
    val state: LiveData<State> = _state
    val errorEvents: LiveData<Event<ErrorEvent>> = _errorEvents

    init {
        viewModelScope.launch {
            val currentUser = chatClient.clientState.user.value!!
            // TODO Get Channels Direct
            val result = chatClient.queryChannels(
                request = QueryChannelsRequest(
                    filter = Filters.beloChannels(
                        type = "messaging",
                        memberIds = listOf(memberId, currentUser.id),
                        projectId = chatClient.getProjectId()
                    ),
                    querySort = QuerySortByField.descByName("last_updated"),
                    messageLimit = 0,
                    limit = 1,
                ),
            ).await()

            val directChannelCid = when (result) {
                is Result.Success -> if (result.value.isNotEmpty()) result.value.first().cid else null
                is Result.Failure -> null
            }
            logger.d { "directChannelCid=$directChannelCid" }
            _state.value = State(directChannelCid = directChannelCid, loading = false)
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.MessageClicked -> handleMessageClicked()
            is Action.RemoveFromChannel -> removeFromChannel(action.username)
        }
    }

    private fun handleMessageClicked() {
        val state = state.value!!
        if (state.directChannelCid != null) {
            _events.value = Event(UiEvent.RedirectToChat(state.directChannelCid))
        } else {
            // _events.value = Event(UiEvent.RedirectToChatPreview)
            createChannelDirect()
        }
    }

    private fun createChannelDirect() {
        val currentUserId =
            chatClient.clientState.user.value?.id ?: error("User must be set before create new channel!")
        viewModelScope.launch {
            val result = chatClient.createChannel(
                channelType = CHANNEL_TYPE_MESSAGING,
                channelId = "",
                memberIds = listOf(memberId, currentUserId),
                extraData = mapOf()//mapOf(CHANNEL_ARG_DRAFT to true),
            ).await()
            if (result is Result.Success) {
                val cid = result.value.cid
                logger.d { "createChannelDirect Success UiEvent.RedirectToChat(${cid})" }
                _events.postValue(Event(UiEvent.RedirectToChat(cid)))
            }
        }
    }

    private fun removeFromChannel(username: String) {
        viewModelScope.launch {
            val message = Message(text = "$username has been removed from this channel")
            when (chatClient.channel(cid).removeMembers(listOf(memberId), message).await()) {
                is Result.Success -> _events.value = Event(UiEvent.Dismiss)
                is Result.Failure -> _errorEvents.postValue(Event(ErrorEvent.RemoveMemberError))
            }
        }
    }

    data class State(val directChannelCid: String?, val loading: Boolean)

    sealed class Action {
        object MessageClicked : Action()
        data class RemoveFromChannel(val username: String) : Action()
    }

    sealed class UiEvent {
        object Dismiss : UiEvent()
        data class RedirectToChat(val cid: String) : UiEvent()
        object RedirectToChatPreview : UiEvent()
    }

    sealed class ErrorEvent {
        object RemoveMemberError : ErrorEvent()
    }
}

class GroupChatInfoMemberOptionsViewModelFactory(private val cid: String, private val memberId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == GroupChatInfoMemberOptionsViewModel::class.java) {
            "GroupChatInfoMemberOptionsViewModelFactory can only create instances of GroupChatInfoMemberOptionsViewModel"
        }

        @Suppress("UNCHECKED_CAST")
        return GroupChatInfoMemberOptionsViewModel(cid, memberId) as T
    }
}
