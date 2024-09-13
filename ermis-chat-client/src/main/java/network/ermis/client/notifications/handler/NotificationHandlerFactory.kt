package network.ermis.client.notifications.handler

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.IconCompat
import io.getstream.android.push.permissions.DefaultNotificationPermissionHandler
import io.getstream.android.push.permissions.NotificationPermissionHandler
import network.ermis.client.R
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.User
import kotlin.reflect.full.primaryConstructor

/**
 * Factory for default [NotificationHandler].
 * Use it to customize an intent the user triggers when clicking on a notification.
 */
public object NotificationHandlerFactory {

    /**
     * Method that creates a [NotificationHandler].
     *
     * @param context The [Context] to build the [NotificationHandler] with.
     * @param notificationConfig Configuration for push notifications.
     * @param newMessageIntent Lambda expression used to generate an [Intent] to open your app
     * @param notificationChannel Lambda expression used to generate a [NotificationChannel].
     * Used in SDK_INT >= VERSION_CODES.O.
     * @param userIconBuilder Generates [IconCompat] to be shown on notifications.
     * @param permissionHandler Handles [android.Manifest.permission.POST_NOTIFICATIONS] permission lifecycle.
     */
    @SuppressLint("NewApi")
    @JvmOverloads
    @JvmStatic
    public fun createNotificationHandler(
        context: Context,
        notificationConfig: NotificationConfig,
        newMessageIntent: ((message: Message, channel: Channel) -> Intent)? = null,
        notificationChannel: (() -> NotificationChannel)? = null,
        userIconBuilder: UserIconBuilder = provideDefaultUserIconBuilder(context),
        permissionHandler: NotificationPermissionHandler? = provideDefaultNotificationPermissionHandler(context),
    ): NotificationHandler {
        return createNotificationHandler(
            context = context,
            newMessageIntent = newMessageIntent,
            notificationChannel = notificationChannel,
            userIconBuilder = userIconBuilder,
            permissionHandler = permissionHandler,
            autoTranslationEnabled = notificationConfig.autoTranslationEnabled,
        )
    }

    /**
     * Method that creates a [NotificationHandler].
     *
     * @param context The [Context] to build the [NotificationHandler] with.
     * @param newMessageIntent Lambda expression used to generate an [Intent] to open your app
     * @param notificationChannel Lambda expression used to generate a [NotificationChannel].
     * Used in SDK_INT >= VERSION_CODES.O.
     * @param userIconBuilder Generates [IconCompat] to be shown on notifications.
     * @param permissionHandler Handles [android.Manifest.permission.POST_NOTIFICATIONS] permission lifecycle.
     * @param autoTranslationEnabled Enables automatic translation of push notifications.
     */
    @SuppressLint("NewApi")
    @JvmOverloads
    @JvmStatic
    public fun createNotificationHandler(
        context: Context,
        newMessageIntent: ((message: Message, channel: Channel) -> Intent)? = null,
        notificationChannel: (() -> NotificationChannel)? = null,
        userIconBuilder: UserIconBuilder = provideDefaultUserIconBuilder(context),
        permissionHandler: NotificationPermissionHandler? = provideDefaultNotificationPermissionHandler(context),
        autoTranslationEnabled: Boolean = false,
    ): NotificationHandler {
        val notificationChannelFun = notificationChannel ?: getDefaultNotificationChannel(context)
        (newMessageIntent ?: getDefaultNewMessageIntentFun(context)).let { newMessageIntentFun ->
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                MessagingStyleNotificationHandler(
                    context,
                    newMessageIntentFun,
                    notificationChannelFun,
                    userIconBuilder,
                    permissionHandler,
                    autoTranslationEnabled,
                )
            } else {
                ChatNotificationHandler(context, newMessageIntentFun, notificationChannelFun, autoTranslationEnabled)
            }
        }
    }

    private fun getDefaultNewMessageIntentFun(
        context: Context,
    ): (message: Message, channel: Channel) -> Intent {
        return { _, _ -> createDefaultNewMessageIntent(context) }
    }

    private fun createDefaultNewMessageIntent(context: Context): Intent =
        context.packageManager!!.getLaunchIntentForPackage(context.packageName)!!

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDefaultNotificationChannel(context: Context): (() -> NotificationChannel) {
        return {
            NotificationChannel(
                context.getString(R.string.ermis_chat_notification_channel_id),
                context.getString(R.string.ermis_chat_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            )
        }
    }

    private fun provideDefaultUserIconBuilder(context: Context): UserIconBuilder {
        // We search for the StreamCoilUserIconBuilder by reflection and this is slow - we need
        // to postpone to not block the SDK initialisation
        return object : UserIconBuilder {
            private val builder by lazy {
                val appContext = context.applicationContext
                runCatching {
                    Class.forName(
                        "io.getstream.chat.android.ui.common.notifications." +
                            "StreamCoilUserIconBuilder",
                    )
                        .kotlin.primaryConstructor
                        ?.call(appContext) as UserIconBuilder
                }.getOrDefault(DefaultUserIconBuilder(appContext))
            }

            override suspend fun buildIcon(user: User): IconCompat? {
                return builder.buildIcon(user)
            }
        }
    }

    private fun provideDefaultNotificationPermissionHandler(context: Context): NotificationPermissionHandler {
        // We search for the SnackbarNotificationPermissionHandler by reflection and this is slow - we need
        // to postpone to not block the SDK initialisation
        return object : NotificationPermissionHandler {
            private val handler by lazy {
                val appContext = context.applicationContext
                runCatching {
                    Class.forName(
                        "io.getstream.android.push.permissions.snackbar.SnackbarNotificationPermissionHandler",
                    ).kotlin.primaryConstructor?.call(appContext) as NotificationPermissionHandler
                }.getOrDefault(
                    DefaultNotificationPermissionHandler
                        .createDefaultNotificationPermissionHandler(appContext as Application),
                )
            }

            override fun onPermissionDenied() {
                handler.onPermissionDenied()
            }

            override fun onPermissionGranted() {
                handler.onPermissionGranted()
            }

            override fun onPermissionRationale() {
                handler.onPermissionRationale()
            }

            override fun onPermissionRequested() {
                handler.onPermissionRequested()
            }
        }
    }
}
