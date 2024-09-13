package network.ermis.sample.feature.customlogin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import network.ermis.client.ErmisClient
import network.ermis.client.ConfigBuilder
import network.ermis.core.models.Device
import network.ermis.core.models.PushProvider
import network.ermis.sample.application.App
import network.ermis.sample.application.FirebaseLogger
import network.ermis.sample.data.user.SampleUser
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import java.util.UUID
import network.ermis.core.models.User as ChatUser

class CustomLoginViewModel : ViewModel() {

    private val logger by taggedLogger("Chat:CustomLoginViewModel")
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun userLogin(userId: String, pass: String) {
        ErmisClient.instance().userLogin(userId, pass)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        loginButtonClicked(
                            LoginCredentials(
                                apiKey = ConfigBuilder.API_KEY,
                                userId = userId,
                                userToken = "Bearer ${result.value.token}",
                                userName = "",
                                refresh_token = result.value.refresh_token
                            )
                        )
                    }

                    is Result.Failure -> {
                        _state.postValue(State.Error(result.value.message))
                        logger.e { "Failed to set user ${result.value}" }
                    }
                }
            }
    }

    fun connectWallet(address: String) {
        ErmisClient.instance().walletConnect(address).enqueue { result ->
            when (result) {
                is Result.Success -> {
                    _state.postValue(State.WalletConnectSuccess(result.value.challenge, address))
                }

                is Result.Failure -> {
                    logger.e { "connectWallet Failure= ${result.value}" }
                }
            }
        }
    }

    fun signinWallet(address: String, signature: String) {
        val nonce = UUID.randomUUID().toString()
        ErmisClient.instance().walletSignin(address = address, signature = signature, nonce = nonce)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        loginButtonClicked(
                            LoginCredentials(
                                apiKey = ConfigBuilder.API_KEY,
                                userId = result.value.user_id ?: address,
                                userToken = "Bearer ${result.value.token}",
                                userName = address.lowercase(),
                                refresh_token = result.value.refresh_token
                            )
                        )
                    }

                    is Result.Failure -> {
                        logger.e { "walletSignin Failure= ${result.value}" }
                    }
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
                            logger.e { "User add device Failure= ${result.value}" }
                        }
                    }
                }
        })
    }

    private fun loginButtonClicked(credentials: LoginCredentials) {
        val invalidFields = getInvalidFields(credentials)
        if (invalidFields.isEmpty()) {
            _state.postValue(State.Loading)
            initChatSdk(credentials)
            initChatUser(credentials)
        } else {
            _state.postValue(State.ValidationError(invalidFields))
        }
    }

    /**
     * You would normally initialize the Chat SDK only once in the Application class,
     * but since we allow changing API keys at runtime in this demo app, we have to
     * reinitialize the Chat SDK here with the new API key.
     */
    private fun initChatSdk(credentials: LoginCredentials) {
        App.instance.chatInitializer.init(credentials.apiKey)
    }

    private fun initChatUser(loginCredentials: LoginCredentials) {
        val chatUser = ChatUser(
            id = loginCredentials.userId,
            name = loginCredentials.userName,
        )

        // val tokenProvider = object : TokenProvider {
        //     override fun loadToken(): String {
        //         logger.d { "api loadToken refreshToken refesh=${loginCredentials.refresh_token} " }
        //         try {
        //             val newToken =
        //                 CustomService.refreshToken(RefreshTokenRequest(loginCredentials.refresh_token ?: "")).execute()
        //             if (newToken.isSuccessful && newToken.body()?.token != null) {
        //                 logger.e { "TokenProvider loadToken login custom token=Bearer ${newToken.body()!!.token!!}  refresh_token=${newToken.body()!!.refresh_token!!}" }
        //                 App.instance.userRepository.setUser(
        //                     SampleUser(
        //                         apiKey = loginCredentials.apiKey,
        //                         id = loginCredentials.userId,
        //                         name = loginCredentials.userName,
        //                         token = "Bearer ${newToken.body()!!.token!!}",
        //                         image = "https://getstream.io/random_png?id=${loginCredentials.userId}&name=${loginCredentials.userName}&size=200",
        //                         refresh_token = newToken.body()!!.refresh_token!!
        //                     ),
        //                 )
        //
        //                 return "Bearer ${newToken.body()?.token}"
        //             }
        //         } catch (e: Exception) {
        //             e.printStackTrace()
        //         }
        //         return loginCredentials.userToken
        //     }
        // }

        //TODO tuyendv redirect projectList
        ErmisClient.instance().connectUser(chatUser, loginCredentials.userToken)
            // ChatClient.instance().connectUser(chatUser, tokenProvider)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        logger.e { "User set successfully" }
                        ErmisClient.instance().setProjectId(ErmisClient.ERMIS_PROJECT_ID, ErmisClient.ERMIS_PROJECT_NAME)
                        FirebaseLogger.userId = result.value.user.id
                        App.instance.userRepository.setUser(
                            SampleUser(
                                apiKey = loginCredentials.apiKey,
                                id = loginCredentials.userId,
                                name = loginCredentials.userName,
                                token = loginCredentials.userToken,
                                image = "https://getstream.io/random_png?id=${loginCredentials.userId}&name=${loginCredentials.userName}&size=200",
                                refresh_token = loginCredentials.refresh_token
                            ),
                        )
                        addDevicePush()
                        _state.postValue(State.RedirectToProjectList)
                        // _state.postValue(State.RedirectToChannels)
                    }

                    is Result.Failure -> {
                        _state.postValue(State.Error(result.value.message))
                        logger.e { "Failed to set user ${result.value}" }
                    }
                }
            }
    }

    private fun getInvalidFields(credentials: LoginCredentials): List<ValidatedField> {
        return ArrayList<ValidatedField>().apply {
            if (credentials.apiKey.isEmpty()) {
                add(ValidatedField.API_KEY)
            }
            if (credentials.userId.isEmpty()) {
                add(ValidatedField.USER_ID)
            }
            if (credentials.userToken.isEmpty()) {
                add(ValidatedField.USER_TOKEN)
            }
        }
    }
}

sealed class State {
    object RedirectToChannels : State()
    object RedirectToProjectList : State()
    object Loading : State()
    data class Error(val errorMessage: String?) : State()
    data class ValidationError(val invalidFields: List<ValidatedField>) : State()
    data class WalletConnectSuccess(val challenge: String, val address: String) : State()
}

data class LoginCredentials(
    val apiKey: String,
    val userId: String,
    val userToken: String,
    val userName: String,
    val refresh_token: String?,
)

enum class ValidatedField {
    API_KEY,
    USER_ID,
    USER_TOKEN,
}
