
package network.ermis.sample.application

import android.content.Context
import com.google.firebase.FirebaseApp
import network.ermis.chat.ui.sample.BuildConfig
import network.ermis.client.ErmisClient
import network.ermis.client.logger.ChatLogLevel
import network.ermis.client.notifications.handler.NotificationConfig
import network.ermis.client.notifications.handler.NotificationHandlerFactory
import network.ermis.markdown.MarkdownTextTransformer
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.UploadAttachmentsNetworkType
import network.ermis.offline.plugin.factory.ErmisOfflinePluginFactory
import network.ermis.state.plugin.config.StatePluginConfig
import network.ermis.state.plugin.factory.StreamStatePluginFactory
import network.ermis.ui.ChatUI
import network.ermis.ui.view.messages.adapter.viewholder.decorator.DecoratorProviderFactory
import network.ermis.ui.view.messages.adapter.viewholder.decorator.plus
import network.ermis.sample.debugger.CustomChatClientDebugger
import network.ermis.sample.feature.HostActivity
import network.ermis.sample.feature.chat.messagelist.CustomDecoratorProviderFactory

class ChatInitializer(
    private val context: Context,
    private val autoTranslationEnabled: Boolean,
) {

    @Suppress("UNUSED_VARIABLE")
    fun init(apiKey: String) {
        FirebaseApp.initializeApp(context)
        val notificationConfig =
            NotificationConfig(
                pushDeviceGenerators = listOf(
                    // FirebasePushDeviceGenerator(providerName = "Firebase"),
                    // HuaweiPushDeviceGenerator(
                    //     context = context,
                    //     appId = ApplicationConfigurator.HUAWEI_APP_ID,
                    //     providerName = "huawei",
                    // ),
                    // XiaomiPushDeviceGenerator(
                    //     context = context,
                    //     appId = ApplicationConfigurator.XIAOMI_APP_ID,
                    //     appKey = ApplicationConfigurator.XIAOMI_APP_KEY,
                    //     providerName = "Xiaomi",
                    // ),
                ),
                autoTranslationEnabled = autoTranslationEnabled,
            )
        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
            context = context,
            notificationConfig = notificationConfig,
            newMessageIntent = {
                    message: Message,
                    channel: Channel,
                ->
                HostActivity.createLaunchIntent(
                    context = context,
                    messageId = message.id,
                    parentMessageId = message.parentId,
                    channelType = channel.type,
                    channelId = channel.id,
                )
            },
        )
        val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING

        val offlinePlugin = ErmisOfflinePluginFactory(context)

        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = context,
        )

        val client = ErmisClient.Builder(apiKey, context)
            .loggerHandler(FirebaseLogger)
            .notifications(notificationConfig, notificationHandler)
            .logLevel(logLevel)
            .withPlugins(offlinePlugin,
                statePluginFactory)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
            .apply {
                if (BuildConfig.DEBUG) {
                    this.debugRequests(true)
                        .clientDebugger(CustomChatClientDebugger())
                }
            }
            .build()

        // Using markdown as text transformer
        ChatUI.autoTranslationEnabled = autoTranslationEnabled
        ChatUI.messageTextTransformer = MarkdownTextTransformer(context) { item ->
            if (autoTranslationEnabled) {
                client.getCurrentUser()?.language?.let { language ->
                    item.message.getTranslation(language).ifEmpty { item.message.text }
                } ?: item.message.text
            } else {
                item.message.text
            }
        }

        // ChatUI.channelAvatarRenderer = ChannelAvatarRenderer { _, channel, _, targetProvider ->
        //     val targetView: AvatarImageView = targetProvider.regular()
        //     if (channel.image.isBlank()) {
        //         targetView.setAvatar(R.drawable.ic_channel_avatar)
        //     } else {
        //         targetView.setAvatar(channel.image)
        //     }
        // }

        // TransformStyle.messageComposerStyleTransformer = StyleTransformer { defaultStyle ->
        //     defaultStyle.copy(
        //         audioRecordingHoldToRecordText = "Bla bla bla",
        //         audioRecordingSlideToCancelText = "Wash to cancel",
        //     )
        // }

        ChatUI.decoratorProviderFactory = CustomDecoratorProviderFactory() + DecoratorProviderFactory.defaultFactory()
    }
}
