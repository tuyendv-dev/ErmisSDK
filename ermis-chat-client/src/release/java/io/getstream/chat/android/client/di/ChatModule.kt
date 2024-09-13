package io.getstream.chat.android.client.di

import android.content.Context
import androidx.lifecycle.Lifecycle
import io.getstream.chat.android.client.api.ChatClientConfig
import io.getstream.chat.android.client.debugger.ChatClientDebugger
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.client.scope.ClientScope
import io.getstream.chat.android.client.scope.UserScope
import io.getstream.chat.android.client.token.TokenManager
import io.getstream.chat.android.client.uploader.FileUploader
import okhttp3.OkHttpClient

/**
 * Release variant of [BaseChatModule].
 */
internal class ChatModule(
    appContext: Context,
    clientScope: ClientScope,
    userScope: UserScope,
    config: ChatClientConfig,
    notificationsHandler: NotificationHandler,
    uploader: FileUploader?,
    tokenManager: TokenManager,
    customOkHttpClient: OkHttpClient?,
    clientDebugger: ChatClientDebugger?,
    lifecycle: Lifecycle,
) : BaseChatModule(
    appContext,
    clientScope,
    userScope,
    config,
    notificationsHandler,
    uploader,
    tokenManager,
    customOkHttpClient,
    clientDebugger,
    lifecycle,
)
