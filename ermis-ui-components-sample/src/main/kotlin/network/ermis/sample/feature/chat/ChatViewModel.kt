
package network.ermis.sample.feature.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.core.models.Member
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.state.utils.Event
import network.ermis.sample.util.extensions.isAnonymousChannel
import io.getstream.log.taggedLogger
import io.getstream.result.call.enqueue
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ChatViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
) : ViewModel() {

    private val logger by taggedLogger("Chat:ChannelVM")

    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: StateFlow<ChannelState?> = observeChannelState()

    private val _navigationEvent: MutableLiveData<Event<NavigationEvent>> = MutableLiveData()
    val navigationEvent: LiveData<Event<NavigationEvent>> = _navigationEvent

    val members: LiveData<List<Member>> = channelState.filterNotNull().flatMapLatest { it.members }.asLiveData()

    private fun observeChannelState(): StateFlow<ChannelState?> {
        val messageLimit = 0
        logger.d { "[observeChannelState] cid: $cid, messageLimit: $messageLimit" }
        return chatClient.watchChannelAsState(cid, messageLimit, viewModelScope)
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.HeaderClicked -> {
                val channelData = requireNotNull(channelState.value?.channelData?.value)
                _navigationEvent.value = Event(
                    if (action.members.size > 2 || !channelData.isAnonymousChannel()) {
                        NavigationEvent.NavigateToGroupChatInfo(cid)
                    } else {
                        NavigationEvent.NavigateToChatInfo(cid)
                    },
                )
            }
            is Action.AcceptChannelInvited -> {
                onAcceptChannelInvited()
            }
            is Action.DeclineChannelInvited -> {
                onRejectChannelInvited()
            }
        }
    }

    private fun onAcceptChannelInvited() {
        viewModelScope.launch {
            val (channelType, channelId) = cid.cidToTypeAndId()
            chatClient
                .acceptInvite(channelType = channelType, channelId = channelId)
                .enqueue(
                    onError = { error->
                        logger.e {
                            "Could not join channel with id: ${channelId}. " +
                                "Error: ${error.message}.}"
                        }
                    }
                )
        }
    }

    private fun onRejectChannelInvited() {
        viewModelScope.launch {
            val (channelType, channelId) = cid.cidToTypeAndId()
            if (channelType == "messaging") {
                _navigationEvent.value = Event(NavigationEvent.NavigateUp(cid))
            } else {
                chatClient
                    .rejectInvite(channelType = channelType, channelId = channelId)
                    .enqueue(
                        onSuccess = {
                            _navigationEvent.value = Event(NavigationEvent.NavigateUp(cid))
                        },
                        onError = { error ->
                            logger.e {
                                "Could not reject Invite channel with id: ${channelId}. " +
                                    "Error: ${error.message}."
                            }
                        }
                    )
            }
        }
    }

    sealed class Action {
        class HeaderClicked(val members: List<Member>) : Action()
        class AcceptChannelInvited() : Action()
        class DeclineChannelInvited() : Action()
    }

    sealed class NavigationEvent {
        abstract val cid: String

        data class NavigateToChatInfo(override val cid: String) : NavigationEvent()
        data class NavigateToGroupChatInfo(override val cid: String) : NavigationEvent()
        data class NavigateUp(override val cid: String) : NavigationEvent()
    }
}
