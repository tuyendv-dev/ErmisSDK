package network.ermis.sample.feature.customregister

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import network.ermis.client.ErmisClient
import network.ermis.client.ConfigBuilder
import network.ermis.sample.application.App
import network.ermis.sample.application.FirebaseLogger
import network.ermis.sample.data.user.SampleUser
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import network.ermis.core.models.User as ChatUser

class CustomRegisterViewModel : ViewModel() {
    private val logger by taggedLogger("Chat:CustomRegisterViewModel")
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun registerLogin(userId: String, userName: String, pass: String) {
        ErmisClient.instance().userRegister(userId, userName, pass)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        loginButtonClicked(
                            LoginCredentials(
                            apiKey = ConfigBuilder.API_KEY,
                            userId = userId,
                            userToken = "Bearer ${result.value.token}",
                            userName = ""
                        )
                        )
                    }
                    is Result.Failure -> {
                        _state.postValue(State.Error(result.value.message))
                        logger.d { "Failed to set user ${result.value}" }
                    }
                }
            }
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

        ErmisClient.instance().connectUser(chatUser, loginCredentials.userToken)
            .enqueue { result ->
                when (result) {
                    is Result.Success -> {
                        _state.postValue(State.RedirectToChannels)
                        logger.d { "User set successfully" }
                        FirebaseLogger.userId = result.value.user.id

                        App.instance.userRepository.setUser(
                            SampleUser(
                                apiKey = loginCredentials.apiKey,
                                id = loginCredentials.userId,
                                name = loginCredentials.userName,
                                token = loginCredentials.userToken,
                                image = "https://getstream.io/random_png?id=${loginCredentials.userId}&name=${loginCredentials.userName}&size=200",
                            ),
                        )
                    }
                    is Result.Failure -> {
                        _state.postValue(State.Error(result.value.message))
                        logger.d { "Failed to set user ${result.value}" }
                    }
                }
            }
    }

    private fun getInvalidFields(credentials: LoginCredentials): List<ValidatedField> {
        return ArrayList<ValidatedField>().apply {
            // if (credentials.apiKey.isEmpty()) {
            //     add(ValidatedField.API_KEY)
            // }
            if (credentials.userId.isEmpty()) {
                add(ValidatedField.USER_ID)
            }
            // if (credentials.userName.isEmpty()) {
            //     add(ValidatedField.USER_NAME)
            // }
            // if (credentials.userToken.isEmpty()) {
            //     add(ValidatedField.USER_TOKEN)
            // }
        }
    }
}

sealed class State {
    object RedirectToChannels : State()
    object Loading : State()
    data class Error(val errorMessage: String?) : State()
    data class ValidationError(val invalidFields: List<ValidatedField>) : State()
}

data class LoginCredentials(
    val apiKey: String,
    val userId: String,
    val userToken: String,
    val userName: String,
)

enum class ValidatedField {
    USER_ID,
    USER_NAME,
}
