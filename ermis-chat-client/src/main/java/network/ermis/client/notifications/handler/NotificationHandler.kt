package network.ermis.client.notifications.handler

import io.getstream.android.push.permissions.NotificationPermissionStatus
import network.ermis.client.ErmisClient
import network.ermis.client.events.NewMessageEvent
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.PushMessage

/**
 * Handler responsible for showing and dismissing notification.
 * Implement this interface and use [ErmisClient.Builder.notifications] if you want to customize default behavior
 *
 * @see [MessagingStyleNotificationHandler]
 * @see [ChatNotificationHandler]
 */
public interface NotificationHandler {

    /**
     * Handles showing notification after receiving [NewMessageEvent] from other users.
     * Default implementation loads necessary data and displays notification even if app is in foreground.
     *
     * @return False if notification should be handled internally.
     */
    public fun onChatEvent(event: NewMessageEvent): Boolean {
        return true
    }

    /**
     * Handles showing notification after receiving [PushMessage].
     * Default implementation loads necessary data from the server and shows notification if application is not in
     * foreground.
     *
     * @return False if remote message should be handled internally.
     */
    public fun onPushMessage(message: PushMessage): Boolean {
        return false
    }

    /**
     * Show a notification for the given [channel] and [message]
     *
     * @param channel where the new message was posted
     * @param message was received
     */
    public fun showNotification(channel: Channel, message: Message)

    /**
     * Dismiss notifications from a given [channelType] and [channelId].
     *
     * @param channelType String that represent the channel type of the channel you want to dismiss notifications.
     * @param channelId String that represent the channel id of the channel you want to dismiss notifications.
     */
    public fun dismissChannelNotifications(channelType: String, channelId: String)

    /**
     * Dismiss all notifications.
     */
    public fun dismissAllNotifications()

    /**
     * Handles [android.Manifest.permission.POST_NOTIFICATIONS] permission lifecycle.
     *
     * @param status Represents current [android.Manifest.permission.POST_NOTIFICATIONS] permission status.
     */
    public fun onNotificationPermissionStatus(status: NotificationPermissionStatus)
}
