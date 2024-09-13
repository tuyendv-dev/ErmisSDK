package network.ermis.sample.feature.userlogin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import network.ermis.core.models.ConnectionData
import network.ermis.core.models.Device
import network.ermis.core.models.InitializationState
import network.ermis.core.models.PushProvider
import network.ermis.state.utils.Event
import network.ermis.sample.application.App
import network.ermis.sample.application.FirebaseLogger
import network.ermis.sample.data.user.SampleUser
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import network.ermis.client.ConfigBuilder
import network.ermis.client.ErmisClient
import network.ermis.client.token.TokenProvider
import java.util.UUID
import network.ermis.core.models.User as ChatUser

class UserLoginViewModel : ViewModel() {
    private val logger by taggedLogger("Chat:UserLoginViewModel")
    private val _events = MutableLiveData<Event<UiEvent>>()

    val events: LiveData<Event<UiEvent>> = _events

    fun init() {
        val user = App.instance.userRepository.getUser()
        if (user != SampleUser.None) {
            authenticateUser(user)
        } else {
            _events.postValue(Event(UiEvent.ShowUiLogin))
        }
    }

    fun connectWallet(address: String) {
        ErmisClient.instance().walletConnect(address).enqueue { result ->
            when (result) {
                is Result.Success -> {
                    _events.postValue(Event(UiEvent.WalletConnectSuccess(result.value.challenge, address)))
                }

                is Result.Failure -> {
                    _events.postValue(Event(UiEvent.Error(result.value.message)))
                    logger.e { "connectWallet Failure= ${result.value}" }
                }
            }
        }
    }

    fun signinWallet(address: String, signature: String) {
        val nonce = UUID.randomUUID().toString()
        _events.postValue(Event(UiEvent.Loading(true)))
        ErmisClient.instance().walletSignin(address = address, signature = signature, nonce = nonce)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        val user = SampleUser(
                            apiKey = "",
                            name = "",
                            id = result.value.user_id,
                            token = "Bearer ${result.value.token}",
                            image = "",
                            refresh_token = result.value.refresh_token,
                        )
                        connectUser(user)
                    }

                    is Result.Failure -> {
                        _events.postValue(Event(UiEvent.Error(result.value.message)))
                        logger.e { "walletSignin Failure= ${result.value}" }
                    }
                }
            }
    }

    private fun authenticateUser(user: SampleUser) {
        val chatUser = ChatUser(
            id = user.id,
            image = user.image,
            name = user.name,
        )
        ErmisClient.instance().run {
            viewModelScope.launch {
                clientState.initializationState
                    .transformWhile {
                        emit(it)
                        it != InitializationState.COMPLETE
                    }
                    .collect {
                        when (it) {
                            InitializationState.COMPLETE -> {
                                navigationPlatform(App.instance.userRepository.getUserSelectedSdk())
                            }
                            InitializationState.INITIALIZING -> {}
                            InitializationState.NOT_INITIALIZED -> {
                                launch {
                                    connectUser(chatUser, user.token).await().let(::handleUserConnection)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun connectUser(user: SampleUser) {
        val chatUser = ChatUser(
            id = user.id,
            image = user.image,
            name = user.name,
        )
        ErmisClient.instance().connectUser(chatUser, user.token) // ChatClient.instance().connectUser(chatUser, tokenProvider)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        _events.postValue(Event(UiEvent.Loading(false)))
                        _events.postValue(Event(UiEvent.ShowUiSelectPlatform))
                        FirebaseLogger.userId = result.value.user.id
                        App.instance.userRepository.setUser(
                            SampleUser(
                                apiKey = ConfigBuilder.API_KEY,
                                id = user.id,
                                token = user.token,
                                refresh_token = user.refresh_token
                            ),
                        )
                        addDevicePush()
                    }

                    is Result.Failure -> {
                        _events.postValue(Event(UiEvent.Error(result.value.message)))
                        logger.e { "Failed to connect User ${result.value}" }
                    }
                }
            }
    }

    private val tokenProvider = object : TokenProvider {
        override fun loadToken(): String {
            val user = App.instance.userRepository.getUser()
            // logger.d { "api loadToken refreshToken refesh=${user.refresh_token} " }
            // try {
            //     val newToken = CustomService.refreshToken(RefreshTokenRequest(user.refresh_token ?: "")).execute()
            //     if (newToken.isSuccessful && newToken.body()?.token != null) {
            //         App.instance.userRepository.setUser(
            //             SampleUser(
            //                 apiKey = user.apiKey,
            //                 id = user.id,
            //                 name = user.name,
            //                 token = "Bearer ${newToken.body()!!.token!!}",
            //                 image = user.image,
            //                 refresh_token = newToken.body()!!.refresh_token!!
            //             ),
            //         )
            //         return "Bearer ${newToken.body()?.token}"
            //     } else {
            //         return "Bearer ${newToken.body()?.token}"
            //         // _events.postValue(Event(UiEvent.RedirectToLogin))
            //     }
            // } catch (e: Exception) {
            //     e.printStackTrace()
            //     // _events.postValue(Event(UiEvent.RedirectToLogin))
            // }
            return user.token
        }
    }

    private fun navigationPlatform(sdkSelected: Boolean) {
        if (sdkSelected) {
            _events.postValue(Event(UiEvent.RedirectToProjects))
        } else {
            ErmisClient.instance().setProjectId(ErmisClient.ERMIS_PROJECT_ID, ErmisClient.ERMIS_PROJECT_NAME)
            _events.postValue(Event(UiEvent.RedirectToChannels))
        }
    }

    public fun userSelectPlasform(sdkSelected: Boolean) {
        App.instance.userRepository.setUserSelectedSdk(sdkSelected)
        navigationPlatform(sdkSelected)
    }

    private fun handleUserConnection(result: Result<ConnectionData>) {
        when (result) {
            is Result.Success -> {
                logger.d { "User set successfully" }
            }

            is Result.Failure -> {
                _events.postValue(Event(UiEvent.Error(result.value.message)))
                logger.d { "Failed to set user ${result.value}" }
            }
        }
    }

    private fun addDevicePush() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                logger.e { "Fetching FCM registration token failed ${task.exception}" }
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            ErmisClient.instance()
                .addDevice(Device(token = token, pushProvider = PushProvider.FIREBASE, providerName = null))
                .enqueue { result ->
                    when (result) {
                        is Result.Success -> {
                            logger.e { "User add device Success token fcm= $token" }
                            App.instance.userRepository.setTokenFCM(token)
                        }

                        is Result.Failure -> {
                            // _events.postValue(Event(UiEvent.Error(result.value.message)))
                            logger.e { "User add device Failure= ${result.value}" }
                        }
                    }
                }
        })
    }

    sealed class UiEvent {
        object RedirectToChannels : UiEvent()
        object RedirectToProjects : UiEvent()
        object ShowUiLogin : UiEvent()
        object ShowUiSelectPlatform : UiEvent()
        object RedirectToLogin : UiEvent()
        data class Loading(val show: Boolean) : UiEvent()
        data class WalletConnectSuccess(val challenge: String, val address: String) : UiEvent()
        data class Error(val errorMessage: String?) : UiEvent()
    }

    internal companion object {
        internal const val EXTRA_SWITCH_USER = "EXTRA_SWITCH_USER"
    }
}
