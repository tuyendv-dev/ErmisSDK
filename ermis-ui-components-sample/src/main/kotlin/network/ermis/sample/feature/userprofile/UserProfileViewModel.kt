package network.ermis.sample.feature.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.core.models.Device
import network.ermis.core.models.PushProvider
import network.ermis.core.models.User
import network.ermis.sample.application.App
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.launch
import java.io.File

class UserProfileViewModel : ViewModel() {
    private val logger by taggedLogger("Chat:UserProfileViewModel")
    private val chatClient = ErmisClient.instance()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun getUserInfo() {
        chatClient.getUserInfo(chatClient.clientState.user.value?.id ?: "")
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        _state.postValue(State.GetUserProfileSuccess(user = result.value))
                    }

                    is Result.Failure -> {
                        _state.postValue(State.Error(result.value.message))
                    }
                }
            }
    }

    fun updateUserProfile(name: String? = null, phone: String? = null, about_me: String? = null) {
        _state.postValue(State.Loading)
        chatClient.updateUserProfile(name, phone, about_me)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        _state.postValue(State.UpdateUserProfileSuccess(user = result.value))
                    }

                    is Result.Failure -> {
                        _state.postValue(State.Error(result.value.message))
                    }
                }
            }
    }

    fun updateUserAvater(imageFile: File) {
        _state.postValue(State.Loading)
        chatClient.updateUserAvatar(imageFile)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        _state.postValue(State.UpdateUserAvatarSuccess(avatar = result.value))
                    }

                    is Result.Failure -> {
                        _state.postValue(State.Error(result.value.message))
                    }
                }
            }
    }

    fun signOut() {
        viewModelScope.launch {
            removeDeviceNoti()
            chatClient.disconnect(false).await()
            App.instance.userRepository.clearUser()
            _state.postValue(State.GotoLogin)
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
}

sealed class State {
    object Loading : State()
    data class Error(val errorMessage: String?) : State()
    data class GetUserProfileSuccess(val user: User) : State()
    data class UpdateUserProfileSuccess(val user: User) : State()
    data class UpdateUserAvatarSuccess(val avatar: String) : State()
    object GotoLogin : State()
}

