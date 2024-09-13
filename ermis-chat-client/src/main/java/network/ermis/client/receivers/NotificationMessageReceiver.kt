package network.ermis.client.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import network.ermis.client.ErmisClient
import network.ermis.client.R
import network.ermis.client.utils.extensions.internal.toCid
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import io.getstream.log.taggedLogger

internal class NotificationMessageReceiver : BroadcastReceiver() {

    companion object {
        private const val ACTION_READ = "network.ermis.chat.READ"
        private const val ACTION_REPLY = "network.ermis.chat.REPLY"
        private const val ACTION_DISMISS = "network.ermis.chat.DISMISS"
        private const val KEY_MESSAGE_ID = "message_id"
        private const val KEY_CHANNEL_ID = "id"
        private const val KEY_CHANNEL_TYPE = "type"
        private const val KEY_TEXT_REPLY = "text_reply"

        private val IMMUTABLE_PENDING_INTENT_FLAGS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        private val MUTABLE_PENDING_INTENT_FLAGS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        private fun createReplyPendingIntent(
            context: Context,
            notificationId: Int,
            channel: Channel,
        ): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                createNotifyIntent(context, channel, ACTION_REPLY),
                MUTABLE_PENDING_INTENT_FLAGS,
            )

        private fun createReadPendingIntent(
            context: Context,
            notificationId: Int,
            channel: Channel,
            message: Message,
        ): PendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            createNotifyIntent(context, channel, ACTION_READ).apply {
                putExtra(KEY_MESSAGE_ID, message.id)
            },
            IMMUTABLE_PENDING_INTENT_FLAGS,
        )

        internal fun createDismissPendingIntent(
            context: Context,
            notificationId: Int,
            channel: Channel,
        ): PendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            createNotifyIntent(context, channel, ACTION_DISMISS),
            IMMUTABLE_PENDING_INTENT_FLAGS,
        )

        internal fun createReadAction(
            context: Context,
            notificationId: Int,
            channel: Channel,
            message: Message,
        ): NotificationCompat.Action {
            return NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_view,
                context.getString(R.string.ermis_chat_notification_read),
                createReadPendingIntent(context, notificationId, channel, message),
            ).build()
        }

        internal fun createReplyAction(
            context: Context,
            notificationId: Int,
            channel: Channel,
        ): NotificationCompat.Action {
            val remoteInput =
                RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel(context.getString(R.string.ermis_chat_notification_type_hint))
                    .build()
            return NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_send,
                context.getString(R.string.ermis_chat_notification_reply),
                createReplyPendingIntent(context, notificationId, channel),
            )
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()
        }

        private fun createNotifyIntent(context: Context, channel: Channel, action: String) =
            Intent(context, NotificationMessageReceiver::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channel.id)
                putExtra(KEY_CHANNEL_TYPE, channel.type)
                this.action = action
            }
    }

    private val logger by taggedLogger("NotificationMessageReceiver")

    override fun onReceive(context: Context, intent: Intent) {
        val channelType = intent.getStringExtra(KEY_CHANNEL_TYPE) ?: return
        val channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: return
        when (intent.action) {
            ACTION_READ -> intent.getStringExtra(KEY_MESSAGE_ID)?.let { messageId ->
                markAsRead(
                    messageId,
                    channelId,
                    channelType,
                )
            }
            ACTION_REPLY -> {
                RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)?.let { message ->
                    replyText(
                        channelId,
                        channelType,
                        message,
                    )
                }
            }
        }
        cancelNotification(channelType, channelId)
    }

    private fun markAsRead(messageId: String, channelId: String, channelType: String) {
        if (!ErmisClient.isInitialized) {
            logger.d {
                "[markAsRead] ChatClient is not initialized, returning."
            }
            return
        }

        ErmisClient.instance().markMessageRead(channelType, channelId, messageId).enqueue()
    }

    private fun replyText(
        channelId: String,
        type: String,
        messageChars: CharSequence,
    ) {
        if (!ErmisClient.isInitialized) {
            logger.d {
                "[replyText] ChatClient is not initialized, returning."
            }
            return
        }

        ErmisClient.instance().sendMessage(
            channelType = type,
            channelId = channelId,
            message = Message(
                text = messageChars.toString(),
                cid = (type to channelId).toCid(),
            ),
        ).enqueue()
    }

    private fun cancelNotification(channelType: String, channelId: String) {
        if (!ErmisClient.isInitialized) {
            logger.d {
                "[cancelNotification] ChatClient is not initialized, returning."
            }
            return
        }
        ErmisClient.instance().dismissChannelNotifications(channelType, channelId)
    }
}
