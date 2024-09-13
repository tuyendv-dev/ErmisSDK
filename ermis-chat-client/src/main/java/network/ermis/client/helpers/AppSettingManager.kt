package network.ermis.client.helpers

import network.ermis.client.api.ChatApi
import network.ermis.core.models.App
import network.ermis.core.models.AppSettings
import network.ermis.core.models.FileUploadConfig

/**
 * Maintains application settings fetched from the backend.
 */
internal class AppSettingManager(private val chatApi: ChatApi) {

    /**
     * Application settings configured in the dashboard and fetched from the backend.
     */
    private var appSettings: AppSettings? = null

    /**
     * Initializes [AppSettingManager] with application settings from the backend.
     */
    fun loadAppSettings() {
        if (appSettings == null) {
            // chatApi.appSettings().enqueue { result ->
            //     if (result is Result.Success) {
            //         this.appSettings = result.value
            //     }
            // }
        }
    }

    /**
     * Returns application settings from the server or the default ones as a fallback.
     *
     * @return The application settings.
     */
    fun getAppSettings(): AppSettings = appSettings ?: createDefaultAppSettings()

    /**
     * Clears the application settings fetched from the backend.
     */
    fun clear() {
        appSettings = null
    }

    companion object {
        /**
         * Builds the default application settings with the reasonable defaults.
         */
        fun createDefaultAppSettings(): AppSettings {
            return AppSettings(
                app = App(
                    name = "",
                    fileUploadConfig = FileUploadConfig(
                        allowedFileExtensions = emptyList(),
                        allowedMimeTypes = emptyList(),
                        blockedFileExtensions = emptyList(),
                        blockedMimeTypes = emptyList(),
                    ),
                    imageUploadConfig = FileUploadConfig(
                        allowedFileExtensions = emptyList(),
                        allowedMimeTypes = emptyList(),
                        blockedFileExtensions = emptyList(),
                        blockedMimeTypes = emptyList(),
                    ),
                ),
            )
        }
    }
}
