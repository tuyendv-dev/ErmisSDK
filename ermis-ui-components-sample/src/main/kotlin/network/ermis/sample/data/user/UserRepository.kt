package network.ermis.sample.data.user

import android.content.Context
import android.content.SharedPreferences

class UserRepository(context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getUser(): SampleUser {
        val apiKey = prefs.getString(KEY_API_KEY, null)
        val id = prefs.getString(KEY_ID, null)
        val name = prefs.getString(KEY_NAME, null)
        val token = prefs.getString(KEY_TOKEN, null)
        val image = prefs.getString(KEY_IMAGE, null)
        val refresh_token = prefs.getString(KEY_REFRESH_TOKEN, null)
        return if (apiKey != null && id != null && name != null && token != null && image != null) {
            SampleUser(
                apiKey = apiKey,
                id = id,
                name = name,
                token = token,
                image = image,
                refresh_token = refresh_token
            )
        } else {
            SampleUser.None
        }
    }

    fun setUser(user: SampleUser) {
        prefs.edit()
            .putString(KEY_API_KEY, user.apiKey)
            .putString(KEY_ID, user.id)
            .putString(KEY_NAME, user.name)
            .putString(KEY_TOKEN, user.token)
            .putString(KEY_IMAGE, user.image)
            .putString(KEY_REFRESH_TOKEN, user.refresh_token)
            .apply()
    }

    fun setTokenFCM(fcm: String) {
        prefs.edit()
            .putString(KEY_TOKEN_FCM_NOTI, fcm)
            .apply()
    }

    fun getTokenFCM(): String? {
        return prefs.getString(KEY_TOKEN_FCM_NOTI, null)
    }

    fun setChainId(chainId: Int) {
        prefs.edit()
            .putInt(KEY_CHAIN_ID, chainId)
            .apply()
    }

    fun getChainId(): Int {
        return prefs.getInt(KEY_CHAIN_ID, 0)
    }

    fun setUserSelectedSdk(selected: Boolean) {
        prefs.edit()
            .putBoolean(KEY_USER_SELECTED_SDK, selected)
            .apply()
    }

    fun getUserSelectedSdk(): Boolean {
        return prefs.getBoolean(KEY_USER_SELECTED_SDK, false)
    }

    fun clearUser() {
        prefs.edit().clear().apply()
    }

    private companion object {
        private const val USER_PREFS_NAME = "logged_in_user"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_TOKEN = "token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_IMAGE = "image"
        private const val KEY_TOKEN_FCM_NOTI = "token_fcm"
        private const val KEY_CHAIN_ID = "chain_id"
        private const val KEY_USER_SELECTED_SDK = "user_selected_sdk"
    }
}
