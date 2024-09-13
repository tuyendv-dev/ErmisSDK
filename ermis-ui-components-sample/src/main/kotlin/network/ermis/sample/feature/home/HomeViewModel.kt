
package network.ermis.sample.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Device
import network.ermis.core.models.PushProvider
import network.ermis.core.models.User
import network.ermis.state.extensions.globalState
import network.ermis.state.plugin.state.global.GlobalState
import network.ermis.state.utils.Event
import network.ermis.ui.viewmodel.channels.ChannelListViewModel.PaginationState
import network.ermis.sample.application.App
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling the state of bottom navigation bar and navigation
 * drawer on the home screen.
 *
 * @param chatClient  The main entry point for all low-level operations.
 * @param clientState The client state used to obtain the current user.
 * @param globalState The global state of OfflinePlugin.
 */
class HomeViewModel(
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
    private val globalState: GlobalState = chatClient.globalState,
) : ViewModel() {

    private val logger by taggedLogger("HomeViewModel")

    /**
     * The initial empty state of the screen.
     */
    private val initialState = UiState()

    /**
     * The home screen state wrapped in MutableLiveData.
     */
    private val _state: MediatorLiveData<UiState> = MediatorLiveData()

    /**
     * The home screen state wrapped in LiveData.
     */
    val state: LiveData<UiState> = _state.distinctUntilChanged()

    /**
     * Emits one-shot events that should be handled only once.
     */
    private val _events: MutableLiveData<Event<UiEvent>> = MutableLiveData()

    /**
     * Emits one-shot events that should be handled only once.
     */
    val events: LiveData<Event<UiEvent>> = _events

    init {
        _state.postValue(initialState)

        _state.addSource(globalState.totalUnreadCount.asLiveData()) { count ->
            setState { copy(totalUnreadCount = count) }
        }
        // TODO tuyendv tạm bỏ live thay bằng http vì socket đang chưa trả dữ liệu user
        // _state.addSource(clientState.user.asLiveData()) { user ->
        //     if (user != null) {
        //         setState { copy(user = user) }
        //     }
        // }
        ErmisClient.instance().getUsersChange()
    }

    fun getUserInfo() {
        chatClient.getCurrentUser()
        chatClient.getUserInfo(chatClient.clientState.user.value?.id ?: "")
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        setState { copy(user = result.value) }
                    }

                    is Result.Failure -> {
                        logger.e { "getUserInfo Failure= ${result.value}" }
                    }
                }
            }
    }

    /**
     * Processes actions and updates the state accordingly.
     *
     * @param action The action to process. Results in a state update as a side-effect.
     */
    fun onUiAction(action: UiAction) {
        when (action) {
            is UiAction.LogoutClicked -> {
                viewModelScope.launch {
                    removeDeviceNoti()
                    chatClient.disconnect(false).await()
                    App.instance.userRepository.clearUser()
                    _events.value = Event(UiEvent.NavigateToLoginScreenLogout)
                }
            }

            is UiAction.SwitchUserClicked -> {
                _events.value = Event(UiEvent.NavigateToLoginScreenSwitchUser)
            }
        }
    }

    private fun removeDeviceNoti() {
        val tokenFcm = App.instance.userRepository.getTokenFCM()
        tokenFcm?.let {
            chatClient.deleteDevice(Device(token = it, pushProvider = PushProvider.FIREBASE, providerName = null)).enqueue { result ->
                when (result) {
                    is Result.Success -> logger.d { "User delete device Success token fcm= $it" }
                    is Result.Failure -> {
                        logger.e { "User delete device Failure= ${result.value}" }
                    }
                }
            }
        }
    }

    /**
     * Sets the current home screen state.
     *
     * @param reducer A lambda function that returns [PaginationState].
     */
    private fun setState(reducer: UiState.() -> UiState) {
        _state.value = reducer(_state.value ?: UiState())
    }

    /**
     * Holds information about the state of the home screen.
     *
     * @param user The currently logged in user.
     * @param totalUnreadCount The total unread messages count for the current user.
     * @param mentionsUnreadCount The number of unread mentions by the current user.
     */
    data class UiState(
        val user: User? = null,
        val totalUnreadCount: Int = 0,
        val mentionsUnreadCount: Int = 0,
    )

    /**
     * Describes actions that are meant to be taken and result in a state
     * update.
     */
    sealed class UiAction {
        /**
         * A click on logout button in navigation drawer.
         */
        object LogoutClicked : UiAction()
        object SwitchUserClicked : UiAction()
    }

    /**
     * Describes one-shot events that should be handled only once.
     */
    sealed class UiEvent {
        /**
         * An event to redirect the user to login screen.
         */
        object NavigateToLoginScreenLogout : UiEvent()
        object NavigateToLoginScreenSwitchUser : UiEvent()
    }
}
