package network.ermis.client.notifications.handler

import io.getstream.android.push.PushDeviceGenerator

/**
 * Push notifications configuration class
 */
public data class NotificationConfig @JvmOverloads constructor(
    /**
     * Enables/disables push notifications on the device.
     * Device's token won't be registered if push notifications are disabled.
     */
    val pushNotificationsEnabled: Boolean = true,

    /**
     * Push notifications are ignored and not displayed when the user is online (when there is an
     * active WebSocket connection). Set to false if you would like to receive and handle push
     * notifications even if user is online. Default value is true.
     */
    val ignorePushMessagesWhenUserOnline: Boolean = true,

    /**
     * A list of generators responsible for providing the information needed to register a device
     * @see [PushDeviceGenerator]
     */
    val pushDeviceGenerators: List<PushDeviceGenerator> = listOf(),

    /**
     * Allows enabling/disabling showing notification after receiving a push message.
     */
    val shouldShowNotificationOnPush: () -> Boolean = { true },

    /**
     * Allows SDK to request [android.Manifest.permission.POST_NOTIFICATIONS] permission for a connected user.
     */
    val requestPermissionOnAppLaunch: () -> Boolean = { true },

    /**
     * Whether or not the auto-translation feature is enabled.
     */
    val autoTranslationEnabled: Boolean = false,
)
