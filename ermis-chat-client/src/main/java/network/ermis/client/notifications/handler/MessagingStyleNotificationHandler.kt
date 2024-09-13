package network.ermis.client.notifications.handler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import io.getstream.android.push.permissions.NotificationPermissionHandler
import io.getstream.android.push.permissions.NotificationPermissionStatus
import network.ermis.client.ErmisClient
import network.ermis.client.R
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.User
import io.getstream.log.taggedLogger
import java.util.Date

/**
 * Class responsible for displaying chat notifications using [NotificationCompat.MessagingStyle].
 * Notification channel should only be accessed if Build.VERSION.SDK_INT >= Build.VERSION_CODES.O.
 */
@RequiresApi(Build.VERSION_CODES.M)
@Suppress("TooManyFunctions")
internal class MessagingStyleNotificationHandler(
    private val context: Context,
    private val newMessageIntent: (message: Message, channel: Channel) -> Intent,
    private val notificationChannel: (() -> NotificationChannel),
    private val userIconBuilder: UserIconBuilder,
    private val permissionHandler: NotificationPermissionHandler?,
    private val autoTranslationEnabled: Boolean = false,
) : NotificationHandler {

    private val logger by taggedLogger("Chat:MsnHandler")

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    private val notificationManager: NotificationManager by lazy {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also { notificationManager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel())
            }
        }
    }

    override fun onNotificationPermissionStatus(status: NotificationPermissionStatus) {
        when (status) {
            NotificationPermissionStatus.REQUESTED -> permissionHandler?.onPermissionRequested()
            NotificationPermissionStatus.GRANTED -> permissionHandler?.onPermissionGranted()
            NotificationPermissionStatus.DENIED -> permissionHandler?.onPermissionDenied()
            NotificationPermissionStatus.RATIONALE_NEEDED -> permissionHandler?.onPermissionRationale()
        }
    }

    override fun showNotification(channel: Channel, message: Message) {
        logger.d { "[showNotification] channel.cid: ${channel.cid}, message.cid: ${message.cid}" }
        val currentUser = ErmisClient.instance().getCurrentUser()
            ?: ErmisClient.instance().getStoredUser()
            ?: User()
        val notificationId = createNotificationId(channel.type, channel.id)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            newMessageIntent(message, channel),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        ErmisClient.instance().launch {
            val initialMessagingStyle = restoreMessagingStyle(channel) ?: createMessagingStyle(currentUser, channel)
            val notification = NotificationCompat.Builder(context, getNotificationChannelId())
                .setSmallIcon(R.drawable.ermis_ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.ermis_ic_notification))
                .setStyle(initialMessagingStyle.addMessage(message.toMessagingStyleMessage(context, currentUser)))
                .setContentIntent(contentPendingIntent)
            // TODO tuyendv bỏ action message noti
            // if (currentUser.id.isNotBlank()) {
            //     notification
            //         .addAction(NotificationMessageReceiver.createReadAction(context, notificationId, channel, message))
            //         .addAction(NotificationMessageReceiver.createReplyAction(context, notificationId, channel))
            // }
            addNotificationId(notificationId)
            notificationManager.notify(notificationId, notification.build())
        }
    }

    override fun dismissChannelNotifications(channelType: String, channelId: String) {
        dismissNotification(createNotificationId(channelType, channelId))
    }

    override fun dismissAllNotifications() {
        getShownNotifications().forEach(::dismissNotification)
    }

    private fun dismissNotification(notificationId: Int) {
        removeNotificationId(notificationId)
        notificationManager.cancel(notificationId)
    }

    private fun addNotificationId(notificationId: Int) {
        sharedPreferences.edit {
            putStringSet(KEY_NOTIFICATIONS_SHOWN, (getShownNotifications() + notificationId).map(Int::toString).toSet())
        }
    }

    private fun removeNotificationId(notificationId: Int) {
        sharedPreferences.edit {
            putStringSet(KEY_NOTIFICATIONS_SHOWN, (getShownNotifications() - notificationId).map(Int::toString).toSet())
        }
    }

    private fun getShownNotifications(): Set<Int> =
        sharedPreferences.getStringSet(KEY_NOTIFICATIONS_SHOWN, null).orEmpty().map(String::toInt).toSet()

    private fun createNotificationId(channelType: String, channelId: String): Int = "$channelType:$channelId".hashCode()

    private fun restoreMessagingStyle(channel: Channel): NotificationCompat.MessagingStyle? =
        notificationManager.activeNotifications
            .firstOrNull { it.id == createNotificationId(channel.type, channel.id) }
            ?.notification
            ?.let(NotificationCompat.MessagingStyle::extractMessagingStyleFromNotification)

    private suspend fun createMessagingStyle(currentUser: User, channel: Channel): NotificationCompat.MessagingStyle =
        NotificationCompat.MessagingStyle(currentUser.toPerson(context))
            .setConversationTitle(channel.name)
            .setGroupConversation(channel.name.isNotBlank())

    private fun getNotificationChannelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel().id
        } else {
            ""
        }
    }

    private companion object {
        private const val SHARED_PREFERENCES_NAME = "stream_notifications.sp"
        private const val KEY_NOTIFICATIONS_SHOWN = "KEY_NOTIFICATIONS_SHOWN"
    }
    private suspend fun Message.toMessagingStyleMessage(
        context: Context,
        currentUser: User?,
    ): NotificationCompat.MessagingStyle.Message {
        var displayedText = when (autoTranslationEnabled) {
            true -> currentUser?.language?.let { userLanguage ->
                getTranslation(userLanguage).ifEmpty { text }
            } ?: text
            else -> text
        }
        if(displayedText.isEmpty()) displayedText = context.getString(R.string.ermis_chat_notification_message_defaul)
        return NotificationCompat.MessagingStyle.Message(displayedText, timestamp, person(context))
    }

    private suspend fun Message.person(context: Context): Person = user.toPerson(context)

    private val Message.timestamp: Long
        get() = (createdAt ?: createdLocallyAt ?: Date()).time

    private suspend fun User.toPerson(context: Context): Person =
        Person.Builder()
            .setKey(id)
            .setName(personName(context))
            .setIcon(userIconBuilder.buildIcon(this))
            .build()

    private fun User.personName(context: Context): String =
        name.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.ermis_chat_notification_empty_username)
}
