package network.ermis.client.notifications

import android.app.Application
import android.content.Context
import io.getstream.android.push.permissions.NotificationPermissionManager
import network.ermis.client.ErmisClient
import network.ermis.client.events.NewMessageEvent
import network.ermis.client.notifications.handler.NotificationConfig
import network.ermis.client.notifications.handler.NotificationHandler
import network.ermis.core.internal.coroutines.DispatcherProvider
import network.ermis.core.models.Channel
import network.ermis.core.models.Device
import network.ermis.core.models.Message
import network.ermis.core.models.PushMessage
import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface ChatNotifications {
    fun onSetUser()
    fun setDevice(device: Device)
    fun onPushMessage(message: PushMessage, pushNotificationReceivedListener: PushNotificationReceivedListener)
    fun onNewMessageEvent(newMessageEvent: NewMessageEvent)
    suspend fun onLogout(flushPersistence: Boolean)
    fun displayNotification(channel: Channel, message: Message)
    fun dismissChannelNotifications(channelType: String, channelId: String)
}

@Suppress("TooManyFunctions")
internal class ChatNotificationsImpl constructor(
    private val handler: NotificationHandler,
    private val notificationConfig: NotificationConfig,
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(DispatcherProvider.IO),
) : ChatNotifications {
    private val logger by taggedLogger("Chat:Notifications")

    private val pushTokenUpdateHandler = PushTokenUpdateHandler(context)
    private val showedMessages = mutableSetOf<String>()
    private val permissionManager: NotificationPermissionManager =
        NotificationPermissionManager.createNotificationPermissionsManager(
            application = context.applicationContext as Application,
            requestPermissionOnAppLaunch = notificationConfig.requestPermissionOnAppLaunch,
            onPermissionStatus = { status ->
                logger.i { "[onPermissionStatus] status: $status" }
                handler.onNotificationPermissionStatus(status)
            },
        )

    init {
        logger.i { "<init> no args" }
    }

    override fun onSetUser() {
        logger.i { "[onSetUser] no args" }
        permissionManager
            .takeIf { notificationConfig.requestPermissionOnAppLaunch() }
            ?.start()
        notificationConfig.pushDeviceGenerators.firstOrNull { it.isValidForThisDevice(context) }
            ?.let {
                it.onPushDeviceGeneratorSelected()
                // it.asyncGeneratePushDevice { setDevice(it.toDevice()) }
            }
    }

    override fun setDevice(device: Device) {
        logger.i { "[setDevice] device: $device" }
        scope.launch {
            pushTokenUpdateHandler.updateDeviceIfNecessary(device)
        }
    }

    override fun onPushMessage(
        message: PushMessage,
        pushNotificationReceivedListener: PushNotificationReceivedListener,
    ) {
        logger.i { "[onReceivePushMessage] message: $message" }

        pushNotificationReceivedListener.onPushNotificationReceived(message.channelType, message.channelId)

        if (notificationConfig.shouldShowNotificationOnPush() && !handler.onPushMessage(message)) {
            handlePushMessage(message)
        }
    }

    override fun onNewMessageEvent(newMessageEvent: NewMessageEvent) {
        val currentUserId = ErmisClient.instance().getCurrentUser()?.id
        if (newMessageEvent.message.user.id == currentUserId) return

        logger.d { "[onNewMessageEvent] event: $newMessageEvent" }
        if (!handler.onChatEvent(newMessageEvent)) {
            logger.i { "[onNewMessageEvent] handle event internally" }
            handleEvent(newMessageEvent)
        }
    }

    override suspend fun onLogout(flushPersistence: Boolean) {
        logger.i { "[onLogout] flusPersistence: $flushPersistence" }
        permissionManager.stop()
        handler.dismissAllNotifications()
        cancelLoadDataWork()
        if (flushPersistence) { removeStoredDevice() }
    }

    private fun cancelLoadDataWork() {
        LoadNotificationDataWorker.cancel(context)
    }

    /**
     * Dismiss notification associated to the [channelType] and [channelId] received on the params.
     *
     * @param channelType String that represent the channel type of the channel you want to dismiss notifications.
     * @param channelId String that represent the channel id of the channel you want to dismiss notifications.
     *
     */
    override fun dismissChannelNotifications(channelType: String, channelId: String) {
        handler.dismissChannelNotifications(channelType, channelId)
    }

    private fun handlePushMessage(message: PushMessage) {
        obtainNotificationData(message.channelId, message.channelType, message.messageId)
    }

    private fun obtainNotificationData(channelId: String, channelType: String, messageId: String, newMessageEvent: NewMessageEvent? = null) {
        logger.d { "[obtainNotificationData] channelCid: $channelId:$channelType, messageId: $messageId" }
        LoadNotificationDataWorker.start(
            context = context,
            channelId = channelId,
            channelType = channelType,
            messageId = messageId,
            newMessageEvent = PartEventPushMoshi.gson.toJson(newMessageEvent)
        )
    }

    private fun handleEvent(event: NewMessageEvent) {
        logger.d { "[onNewMessageEvent] event=${event}" }
        obtainNotificationData(event.channelId, event.channelType, event.message.id, event)
    }

    private fun wasNotificationDisplayed(messageId: String) = showedMessages.contains(messageId)

    override fun displayNotification(channel: Channel, message: Message) {
        logger.d { "[displayNotification] channel.cid: ${channel.cid}, message.cid: ${message.cid}" }
        if (!wasNotificationDisplayed(message.id)) {
            showedMessages.add(message.id)
            handler.showNotification(channel, message)
        }
    }

    private suspend fun removeStoredDevice() {
        pushTokenUpdateHandler.removeStoredDevice()
    }
}

internal object NoOpChatNotifications : ChatNotifications {
    override fun onSetUser() = Unit
    override fun setDevice(device: Device) = Unit
    override fun onPushMessage(
        message: PushMessage,
        pushNotificationReceivedListener: PushNotificationReceivedListener,
    ) = Unit

    override fun onNewMessageEvent(newMessageEvent: NewMessageEvent) = Unit
    override suspend fun onLogout(flushPersistence: Boolean) = Unit
    override fun displayNotification(channel: Channel, message: Message) = Unit
    override fun dismissChannelNotifications(channelType: String, channelId: String) = Unit
}
