package network.ermis.client.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import network.ermis.client.ErmisClient
import network.ermis.client.R
import network.ermis.client.events.NewMessageEvent
import network.ermis.core.models.Channel
import io.getstream.log.taggedLogger

internal class LoadNotificationDataWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private val logger by taggedLogger("Chat:Notifications-Loader")

    override suspend fun doWork(): Result {
        val channelId: String = inputData.getString(DATA_CHANNEL_ID)!!
        val channelType: String = inputData.getString(DATA_CHANNEL_TYPE)!!
        val messageId: String = inputData.getString(DATA_MESSAGE_ID)!!
        val newMessageEvent: String = inputData.getString(DATA_MESSAGE_TEXT) ?: "{}"

        setForeground(createForegroundInfo())

        try {
            val newMessage = PartEventPushMoshi.gson.fromJson(newMessageEvent, NewMessageEvent::class.java)
            val client: ErmisClient = ErmisClient.instance()
            val channel = client.logicRegistry?.channelStateLogic(channelType = channelType, channelId = channelId)?.listenForChannelState()?.toChannel()
            if (channel?.membership?.channelRole == "pending") return Result.success() // not push notification invited channel
            val memberCurrent = channel?.members?.filter { it.user.id == ErmisClient.instance().getCurrentUser()?.id && it.channelRole == "pending" }?.firstOrNull()
            if (memberCurrent != null) return Result.success()
            val result = client.getUserInfo(newMessage.user.id).await()
            return when (result) {
                is io.getstream.result.Result.Success -> {
                    val user = result.value
                    ErmisClient.displayNotification(
                        channel = channel ?: Channel(id = channelId, type = channelType),
                        message = newMessage.message.copy(user = user))
                    Result.success()
                }

                is io.getstream.result.Result.Failure -> {
                    logger.e { "Error while loading notification data: ${result.value}" }
                    Result.failure()
                }
            }

            // val getMessage = client.getMessage(messageId)
            // val getChannel = client.queryChannel(
            //     channelType,
            //     channelId,
            //     QueryChannelRequest().apply {
            //         isNotificationUpdate = true
            //     },
            // )
            // val getUser = client.getUserInfo(message.user.id)
            // val result = getChannel.zipWith(getUser).await()
            // when (result) {
            //     is io.getstream.result.Result.Success -> {
            //         val (channel, user) = result.value
            //         // ChatClient.displayNotification(channel = channel, message = message.copy(user = user))
            //         Result.success()
            //     }
            //     is io.getstream.result.Result.Failure -> {
            //         logger.e { "Error while loading notification data: ${result.value}" }
            //         Result.failure()
            //     }
            // }
        } catch (exception: IllegalStateException) {
            logger.e { "Error while loading notification data: ${exception.message}" }
            return Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val foregroundNotification = createForegroundNotification(
            notificationChannelId = context.getString(R.string.ermis_chat_other_notifications_channel_id),
            notificationChannelName = context.getString(R.string.ermis_chat_other_notifications_channel_name),
        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(
                NOTIFICATION_ID,
                foregroundNotification,
                FOREGROUND_SERVICE_TYPE_SHORT_SERVICE,
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                foregroundNotification,
            )
        }
    }

    private fun createForegroundNotification(
        notificationChannelId: String,
        notificationChannelName: String,
    ): Notification {
        createSyncNotificationChannel(notificationChannelId, notificationChannelName)
        return NotificationCompat.Builder(context, notificationChannelId)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ermis_ic_notification)
            .setContentTitle(context.getString(R.string.ermis_chat_load_notification_data_title))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createSyncNotificationChannel(
        notificationChannelId: String,
        notificationChannelName: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                notificationChannelId,
                notificationChannelName,
                NotificationManager.IMPORTANCE_HIGH,
            ).run {
                context.getSystemService(NotificationManager::class.java).createNotificationChannel(this)
            }
        }
    }

    internal companion object {
        private const val DATA_CHANNEL_TYPE = "DATA_CHANNEL_TYPE"
        private const val DATA_CHANNEL_ID = "DATA_CHANNEL_ID"
        private const val DATA_MESSAGE_ID = "DATA_MESSAGE_ID"
        private const val DATA_MESSAGE_TEXT = "DATA_MESSAGE_Text"

        private const val NOTIFICATION_ID = 1
        private const val LOAD_NOTIFICATION_DATA_WORK_NAME = "LOAD_NOTIFICATION_DATA_WORK_NAME"

        fun start(
            context: Context,
            channelId: String,
            channelType: String,
            messageId: String,
            newMessageEvent: String?
        ) {
            val syncMessagesWork = OneTimeWorkRequestBuilder<LoadNotificationDataWorker>()
                .setInputData(
                    workDataOf(
                        DATA_CHANNEL_ID to channelId,
                        DATA_CHANNEL_TYPE to channelType,
                        DATA_MESSAGE_ID to messageId,
                        DATA_MESSAGE_TEXT to newMessageEvent,
                    ),
                )
                .build()

            WorkManager
                .getInstance(context)
                .enqueueUniqueWork(
                    LOAD_NOTIFICATION_DATA_WORK_NAME,
                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                    syncMessagesWork,
                )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(LOAD_NOTIFICATION_DATA_WORK_NAME)
        }
    }
}
