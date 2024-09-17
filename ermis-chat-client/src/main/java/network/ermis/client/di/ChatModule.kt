package network.ermis.client.di

import android.content.Context
import androidx.lifecycle.Lifecycle
import network.ermis.client.api.ErmisClientConfig
import network.ermis.client.debugger.ChatClientDebugger
import network.ermis.client.notifications.handler.NotificationHandler
import network.ermis.client.parser.ChatParser
import network.ermis.client.scope.ClientScope
import network.ermis.client.scope.UserScope
import network.ermis.client.token.TokenManager
import network.ermis.client.uploader.FileUploader
import okhttp3.OkHttpClient

/**
 * Debug implementation of [BaseChatModule].
 *
 * When updating this class, don't forget to update its empty release variant as well, as their
 * interfaces have to match.
 */
internal class ChatModule(
    appContext: Context,
    clientScope: ClientScope,
    userScope: UserScope,
    config: ErmisClientConfig,
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
) {

    override fun clientBuilder(
        timeout: Long,
        config: ErmisClientConfig,
        parser: ChatParser,
        isAnonymousApi: Boolean,
    ): OkHttpClient.Builder {
        return super.clientBuilder(
            timeout,
            config,
            parser,
            isAnonymousApi,
        )
//            .addNetworkInterceptor(flipperInterceptor())
    }
}
