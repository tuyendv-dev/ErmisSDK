
package network.ermis.sample.feature.chat.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.setup.ClientState
import network.ermis.state.utils.Event
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class ChatPreviewViewModel(
    private val memberId: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
) : ViewModel() {

    companion object {
        private const val CHANNEL_TYPE_MESSAGING = "messaging"
    }
    private val logger by taggedLogger("ChatPreviewViewModel")
    private var cid: String? = null
    private val _state: MutableLiveData<State> = MutableLiveData()
    private val _events: MutableLiveData<Event<UiEvent>> = MutableLiveData()
    val state: LiveData<State> = _state
    val events = _events

    init {
        _state.value = State(cid = null)
        viewModelScope.launch {
            logger.d { "get Channel direct" }
            clientState.user.filterNotNull().collect { user ->
                val result = chatClient.createChannel(
                    channelType = CHANNEL_TYPE_MESSAGING,
                    channelId = "",
                    memberIds = listOf(memberId, user.id),
                    extraData = mapOf()//mapOf(CHANNEL_ARG_DRAFT to true),
                ).await()

                if (result is Result.Success) {
                    cid = result.value.cid
                    _state.postValue(State(cid!!))
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.MessageSent -> updateChannel()
        }
    }

    private fun updateChannel() {
        val cid = requireNotNull(cid)
        _events.postValue(Event(UiEvent.NavigateToChat(cid)))
        // viewModelScope.launch {
        //     val result =
        //         chatClient.channel(cid).update(message = null, extraData = mapOf(CHANNEL_ARG_DRAFT to false)).await()
        //     if (result is Result.Success) {
        //         _events.postValue(Event(UiEvent.NavigateToChat(cid)))
        //     }
        // }
    }

    data class State(val cid: String?)

    sealed class Action {
        object MessageSent : Action()
    }

    sealed class UiEvent {
        data class NavigateToChat(val cid: String) : UiEvent()
    }
}

class ChatPreviewViewModelFactory(private val memberId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ChatPreviewViewModel::class.java) {
            "ChatPreviewViewModelFactory can only create instances of ChatPreviewViewModel"
        }

        @Suppress("UNCHECKED_CAST")
        return ChatPreviewViewModel(memberId) as T
    }
}
