package network.ermis.client.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Logger
import network.ermis.client.StreamLifecycleObserver
import network.ermis.client.api.AnonymousApi
import network.ermis.client.api.AuthenticatedApi
import network.ermis.client.api.ChatApi
import network.ermis.client.api.ErmisClientConfig
import network.ermis.client.api.RetrofitCallAdapterFactory
import network.ermis.client.api.RetrofitCdnApi
import network.ermis.client.api.interceptor.ApiKeyInterceptor
import network.ermis.client.api.interceptor.ApiRequestAnalyserInterceptor
import network.ermis.client.api.interceptor.HeadersInterceptor
import network.ermis.client.api.interceptor.HttpLoggingInterceptor
import network.ermis.client.api.interceptor.ProgressInterceptor
import network.ermis.client.api.interceptor.TokenAuthInterceptor
import network.ermis.client.api.interceptor.UserIdInterceptor
import network.ermis.client.api.internal.DistinctChatApi
import network.ermis.client.api.internal.DistinctChatApiEnabler
import network.ermis.client.api.internal.ExtraDataValidator
import network.ermis.client.api.MoshiChatApi
import network.ermis.client.api.endpoint.ChannelApi
import network.ermis.client.api.endpoint.ConfigApi
import network.ermis.client.api.endpoint.DeviceApi
import network.ermis.client.api.endpoint.ErmisClientApi
import network.ermis.client.api.endpoint.FileDownloadApi
import network.ermis.client.api.endpoint.GeneralApi
import network.ermis.client.api.endpoint.GuestApi
import network.ermis.client.api.endpoint.LoginRegisterUserApi
import network.ermis.client.api.endpoint.MessageApi
import network.ermis.client.api.endpoint.ModerationApi
import network.ermis.client.api.endpoint.OpenGraphApi
import network.ermis.client.api.endpoint.UserApi
import network.ermis.client.api.endpoint.VideoCallApi
import network.ermis.client.api.endpoint.WalletApi
import network.ermis.client.clientstate.UserStateService
import network.ermis.client.debugger.ChatClientDebugger
import network.ermis.client.network.NetworkStateProvider
import network.ermis.client.notifications.ChatNotifications
import network.ermis.client.notifications.ChatNotificationsImpl
import network.ermis.client.notifications.NoOpChatNotifications
import network.ermis.client.notifications.handler.NotificationConfig
import network.ermis.client.notifications.handler.NotificationHandler
import network.ermis.client.parser.ChatParser
import network.ermis.client.parser.MoshiChatParser
import network.ermis.client.plugin.requests.ApiRequestsAnalyser
import network.ermis.client.scope.ClientScope
import network.ermis.client.scope.UserScope
import network.ermis.client.setup.MutableClientState
import network.ermis.client.socket.ChatSocket
import network.ermis.client.socket.SocketFactory
import network.ermis.client.token.TokenManager
import network.ermis.client.token.TokenManagerImpl
import network.ermis.client.uploader.FileUploader
import network.ermis.client.uploader.ErmisFileUploader
import network.ermis.client.user.CurrentUserFetcher
import network.ermis.client.utils.TokenUtils.logger
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@Suppress("TooManyFunctions")
internal open class BaseChatModule(
    private val appContext: Context,
    private val clientScope: ClientScope,
    private val userScope: UserScope,
    private val config: ErmisClientConfig,
    private val notificationsHandler: NotificationHandler,
    private val fileUploader: FileUploader? = null,
    private val tokenManager: TokenManager = TokenManagerImpl(),
    private val customOkHttpClient: OkHttpClient? = null,
    private val clientDebugger: ChatClientDebugger? = null,
    private val lifecycle: Lifecycle,
    private val httpClientConfig: (OkHttpClient.Builder) -> OkHttpClient.Builder = { it },
) {

    private val moshiParser: ChatParser by lazy { MoshiChatParser() }
    private val socketFactory: SocketFactory by lazy { SocketFactory(moshiParser, tokenManager) }

    private val defaultNotifications by lazy { buildNotification(notificationsHandler, config.notificationConfig) }
    private val defaultApi by lazy { buildApi(config) }
    internal val chatSocket: ChatSocket by lazy { buildChatSocket(config) }
    private val defaultFileUploader by lazy {
        ErmisFileUploader(buildRetrofitCdnApi())
    }

    val lifecycleObserver: StreamLifecycleObserver by lazy { StreamLifecycleObserver(userScope, lifecycle) }
    val networkStateProvider: NetworkStateProvider by lazy {
        NetworkStateProvider(userScope, appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    }
    val userStateService: UserStateService = UserStateService()

    val mutableClientState by lazy {
        MutableClientState(networkStateProvider)
    }

    val currentUserFetcher by lazy {
        CurrentUserFetcher(
            networkStateProvider = networkStateProvider,
            socketFactory = socketFactory,
            config = config,
        )
    }

    //region Modules

    fun api(): ChatApi = defaultApi

    fun notifications(): ChatNotifications = defaultNotifications

    //endregion

    private fun buildNotification(
        handler: NotificationHandler,
        notificationConfig: NotificationConfig,
    ): ChatNotifications = if (notificationConfig.pushNotificationsEnabled) {
        ChatNotificationsImpl(handler, notificationConfig, appContext)
    } else {
        NoOpChatNotifications
    }

    private fun buildRetrofit(
        endpoint: String,
        timeout: Long,
        config: ErmisClientConfig,
        parser: ChatParser,
        isAnonymousApi: Boolean,
    ): Retrofit {
        val okHttpClient = clientBuilder(timeout, config, parser, isAnonymousApi).build()

        return Retrofit.Builder()
            .baseUrl(endpoint)
            .client(okHttpClient)
            .also(parser::configRetrofit)
            .addCallAdapterFactory(RetrofitCallAdapterFactory.create(parser, userScope))
            .build()
    }

    // Create Builders from a single client to share threadpools
    private val baseClient: OkHttpClient by lazy { customOkHttpClient ?: OkHttpClient() }
    private fun baseClientBuilder(): OkHttpClient.Builder =
        baseClient.newBuilder().followRedirects(false)

    protected open fun clientBuilder(
        timeout: Long,
        config: ErmisClientConfig,
        parser: ChatParser,
        isAnonymousApi: Boolean,
    ): OkHttpClient.Builder {
        return baseClientBuilder()
            .apply {
                if (baseClient != customOkHttpClient) {
                    connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    readTimeout(timeout, TimeUnit.MILLISECONDS)
                }
            }


            // timeouts
            // interceptors
            .addInterceptor(ApiKeyInterceptor(config.apiKey))
            .addInterceptor(UserIdInterceptor(tokenManager))
            .addInterceptor(HeadersInterceptor(getAnonymousProvider(config, isAnonymousApi)))
            .apply {
                if (config.debugRequests) {
                    addInterceptor(ApiRequestAnalyserInterceptor(ApiRequestsAnalyser.get()))
                }
            }
            .let(httpClientConfig)
            .addInterceptor(
                TokenAuthInterceptor(
                    tokenManager,
                    parser,
                    getAnonymousProvider(config, isAnonymousApi),
                ),
            )
            .apply {
                // if (config.loggerConfig.level != ChatLogLevel.NOTHING) {
                    logger.i{ "Chat:LoggerConfig: ${config.loggerConfig}"}
                    // val httpLoggingInterceptor = okhttp3.logging.HttpLoggingInterceptor()
                    // httpLoggingInterceptor.level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                    this.addInterceptor(HttpLoggingInterceptor())

                    addInterceptor(
                        CurlInterceptor(
                            logger = object : Logger {
                                override fun log(message: String) {
                                    logger.i { "Chat:CURL $message" }
                                }
                            },
                        ),
                    )
                // }
            }
            .addNetworkInterceptor(ProgressInterceptor())
    }

    private fun getAnonymousProvider(
        config: ErmisClientConfig,
        isAnonymousApi: Boolean,
    ): () -> Boolean {
        return { isAnonymousApi || config.isAnonymous }
    }

    private fun buildChatSocket(
        chatConfig: ErmisClientConfig,
    ) = ChatSocket(
        chatConfig.apiKey,
        chatConfig.wssUrl,
        tokenManager,
        socketFactory,
        userScope,
        lifecycleObserver,
        networkStateProvider,
        clientDebugger,
    )

    @Suppress("RemoveExplicitTypeArguments")
    private fun buildApi(chatConfig: ErmisClientConfig): ChatApi = MoshiChatApi(
        fileUploader ?: defaultFileUploader,
        buildRetrofitApi<LoginRegisterUserApi>(),
        buildRetrofitApi<UserApi>(),
        buildRetrofitApi<GuestApi>(),
        buildRetrofitApi<MessageApi>(),
        buildRetrofitApi<ChannelApi>(),
        buildRetrofitApi<DeviceApi>(),
        buildRetrofitApi<ModerationApi>(),
        buildRetrofitApi<GeneralApi>(),
        buildRetrofitApi<ConfigApi>(),
        buildRetrofitApi<VideoCallApi>(),
        buildRetrofitApi<FileDownloadApi>(),
        buildRetrofitApi<OpenGraphApi>(),
        buildRetrofitApi<WalletApi>(config.walletHttpUrl),
        buildRetrofitApi<ErmisClientApi>(config.walletHttpUrl),
        userScope,
        userScope,
    ).let { originalApi ->
        DistinctChatApiEnabler(DistinctChatApi(userScope, originalApi)) {
            chatConfig.distinctApiCalls
        }
    }.let { originalApi ->
        ExtraDataValidator(userScope, originalApi)
    }

    private inline fun <reified T> buildRetrofitApi(baseUrl: String = config.httpUrl): T {
        val apiClass = T::class.java
        return buildRetrofit(
            baseUrl,
            BASE_TIMEOUT,
            config,
            moshiParser,
            apiClass.isAnonymousApi,
        ).create(apiClass)
    }

    private val Class<*>.isAnonymousApi: Boolean
        get() {
            val anon = this.annotations.any { it is AnonymousApi }
            val auth = this.annotations.any { it is AuthenticatedApi }

            if (anon && auth) {
                throw IllegalStateException(
                    "Api class must be annotated with either @AnonymousApi or @AuthenticatedApi, and not both",
                )
            }

            if (anon) return true
            if (auth) return false

            throw IllegalStateException("Api class must be annotated with either @AnonymousApi or @AuthenticatedApi")
        }

    private fun buildRetrofitCdnApi(): RetrofitCdnApi {
        val apiClass = RetrofitCdnApi::class.java
        return buildRetrofit(
            config.cdnHttpUrl,
            CDN_TIMEOUT,
            config,
            moshiParser,
            apiClass.isAnonymousApi,
        ).create(apiClass)
    }

    private companion object {
        private const val BASE_TIMEOUT = 30_000L
        private var CDN_TIMEOUT = 30_000L
    }
}
