
package network.ermis.sample.feature.channel.add.group.selectname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.core.models.User
import io.getstream.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import network.ermis.state.utils.Event as EventWrapper

class AddGroupChannelSelectNameViewModel : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData()
    private val _errorEvents: MutableLiveData<EventWrapper<ErrorEvent>> = MutableLiveData()
    val state: LiveData<State> = _state
    val errorEvents: LiveData<EventWrapper<ErrorEvent>> = _errorEvents

    fun onEvent(event: Event) {
        when (event) {
            is Event.CreateChannel -> createChannel(event.name, event.members)
        }
    }

    private fun createChannel(name: String, members: List<User>) {
        _state.value = State.Loading
        viewModelScope.launch(Dispatchers.Main) {
            val currentUserId =
                ErmisClient.instance().clientState.user.value?.id ?: error("User must be set before create new channel!")
            val result = ErmisClient.instance()
                .createChannel(
                    channelType = CHANNEL_TYPE_TEAM,//CHANNEL_TYPE_MESSAGING,
                    channelId = ErmisClient.instance().getProjectId() + ":" + UUID.randomUUID().toString().replace("-",""),
                    memberIds = members.map(User::id) + currentUserId,
                    extraData = mapOf(EXTRA_DATA_CHANNEL_NAME to name),
                ).await()
            when (result) {
                is Result.Success -> _state.value = State.NavigateToChannel(result.value.cid)
                is Result.Failure -> _errorEvents.postValue(EventWrapper(ErrorEvent.CreateChannelError))
            }
        }
    }

    companion object {
        private const val CHANNEL_TYPE_MESSAGING = "messaging"
        private const val CHANNEL_TYPE_TEAM = "team"
        private const val EXTRA_DATA_CHANNEL_NAME = "name"
    }

    sealed class State {
        object Loading : State()
        data class NavigateToChannel(val cid: String) : State()
    }

    sealed class Event {
        data class CreateChannel(val name: String, val members: List<User>) : Event()
    }

    sealed class ErrorEvent {
        object CreateChannelError : ErrorEvent()
    }
}
