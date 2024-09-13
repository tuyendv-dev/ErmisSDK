package network.ermis.client.user.storage

import android.content.Context
import android.content.SharedPreferences
import network.ermis.client.user.CredentialConfig

internal class SharedPreferencesCredentialStorage(context: Context) : UserCredentialStorage {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(SYNC_CONFIG_PREFS_NAME, Context.MODE_PRIVATE)

    override fun get(): CredentialConfig? = prefs.run {
        val userId = getString(KEY_USER_ID, "") ?: ""
        val userToken = getString(KEY_USER_TOKEN, "") ?: ""
        val userName = getString(KEY_USER_NAME, "") ?: ""
        val isAnonymous = getBoolean(KEY_IS_ANONYMOUS, false)

        val config = CredentialConfig(
            userId = userId,
            userToken = userToken,
            userName = userName,
            isAnonymous = isAnonymous,
        )

        return config.takeIf(CredentialConfig::isValid)
    }

    override fun clear() = prefs.edit().clear().apply()

    override fun put(credentialConfig: CredentialConfig) {
        prefs.edit().apply {
            putString(KEY_USER_ID, credentialConfig.userId)
            putString(KEY_USER_TOKEN, credentialConfig.userToken)
            putString(KEY_USER_NAME, credentialConfig.userName)
            putBoolean(KEY_IS_ANONYMOUS, credentialConfig.isAnonymous)
        }.apply()
    }

    companion object {
        private const val SYNC_CONFIG_PREFS_NAME = "stream_credential_config_store"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_ANONYMOUS = "is_anonymous"
    }
}
