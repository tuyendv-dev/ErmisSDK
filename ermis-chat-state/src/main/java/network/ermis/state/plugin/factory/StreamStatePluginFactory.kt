package network.ermis.state.plugin.factory

import android.content.Context
import io.getstream.log.StreamLog
import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.job
import network.ermis.client.ErmisClient
import network.ermis.client.events.ChatEvent
import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.plugin.Plugin
import network.ermis.client.plugin.factory.PluginFactory
import network.ermis.client.setup.ClientState
import network.ermis.core.internal.coroutines.DispatcherProvider
import network.ermis.core.models.User
import network.ermis.state.errorhandler.StateErrorHandlerFactory
import network.ermis.state.event.handler.EventHandler
import network.ermis.state.event.handler.EventHandlerSequential
import network.ermis.state.plugin.config.StatePluginConfig
import network.ermis.state.plugin.internal.StatePlugin
import network.ermis.state.plugin.logic.LogicRegistry
import network.ermis.state.plugin.state.StateRegistry
import network.ermis.state.plugin.state.global.MutableGlobalState
import network.ermis.state.sync.OfflineSyncFirebaseMessagingHandler
import network.ermis.state.sync.SyncManager
import kotlin.reflect.KClass

/**
 * Implementation of [PluginFactory] that provides [StatePlugin].
 *
 * @param config [StatePluginConfig] Configuration of persistence of the SDK.
 * @param appContext [Context]
 */
public class StreamStatePluginFactory(
    private val config: StatePluginConfig,
    private val appContext: Context,
) : PluginFactory {
    private val logger by taggedLogger("Chat:StatePluginFactory")

    override fun <T : Any> resolveDependency(klass: KClass<T>): T? {
        return when (klass) {
            StatePluginConfig::class -> config as T
            else -> null
        }
    }

    /**
     * Creates a [Plugin]
     *
     * @return The [Plugin] instance.
     */
    override fun get(user: User): Plugin {
        logger.d { "[get] user.id: ${user.id}" }
        return createStatePlugin(user)
    }

    private fun createStatePlugin(user: User): StatePlugin {
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            StreamLog.e("StreamStatePlugin", throwable) {
                "[uncaughtCoroutineException] throwable: $throwable, context: $context"
            }
        }
        val scope = ErmisClient.instance().inheritScope { parentJob ->
            SupervisorJob(parentJob) + DispatcherProvider.IO + exceptionHandler
        }
        return createStatePlugin(user, scope, MutableGlobalState())
    }

    @SuppressWarnings("LongMethod")
    private fun createStatePlugin(
        user: User,
        scope: CoroutineScope,
        mutableGlobalState: MutableGlobalState,
    ): StatePlugin {
        logger.v { "[createStatePlugin] user.id: ${user.id}" }
        val chatClient = ErmisClient.instance()
        val repositoryFacade = chatClient.repositoryFacade
        val clientState = chatClient.clientState

        val stateRegistry = StateRegistry(
            clientState.user,
            repositoryFacade.observeLatestUsers(),
            scope.coroutineContext.job,
            scope,
        )

        val isQueryingFree = MutableStateFlow(true)

        val logic = LogicRegistry(
            stateRegistry = stateRegistry,
            clientState = clientState,
            mutableGlobalState = mutableGlobalState,
            userPresence = config.userPresence,
            repos = repositoryFacade,
            client = chatClient,
            coroutineScope = scope,
            queryingChannelsFree = isQueryingFree,
        )

        chatClient.logicRegistry = logic

        val syncManager = SyncManager(
            currentUserId = user.id,
            scope = scope,
            chatClient = chatClient,
            clientState = clientState,
            repos = repositoryFacade,
            logicRegistry = logic,
            stateRegistry = stateRegistry,
            userPresence = config.userPresence,
            syncMaxThreshold = config.syncMaxThreshold,
            now = { System.currentTimeMillis() },
        )

        val eventHandler: EventHandler = createEventHandler(
            user = user,
            scope = scope,
            client = chatClient,
            logicRegistry = logic,
            stateRegistry = stateRegistry,
            clientState = clientState,
            mutableGlobalState = mutableGlobalState,
            repos = repositoryFacade,
            syncedEvents = syncManager.syncedEvents,
            sideEffect = syncManager::awaitSyncing,
        )

        if (config.backgroundSyncEnabled) {
            chatClient.setPushNotificationReceivedListener { channelType, channelId ->
                OfflineSyncFirebaseMessagingHandler().syncMessages(appContext, "$channelType:$channelId")
            }
        }

        val stateErrorHandlerFactory = StateErrorHandlerFactory(
            scope = scope,
            logicRegistry = logic,
            clientState = clientState,
            repositoryFacade = repositoryFacade,
        )

        return StatePlugin(
            errorHandlerFactory = stateErrorHandlerFactory,
            logic = logic,
            repositoryFacade = repositoryFacade,
            clientState = clientState,
            stateRegistry = stateRegistry,
            syncManager = syncManager,
            eventHandler = eventHandler,
            globalState = mutableGlobalState,
            queryingChannelsFree = isQueryingFree,
            statePluginConfig = config,
        )
    }

    @Suppress("LongMethod", "LongParameterList")
    private fun createEventHandler(
        user: User,
        scope: CoroutineScope,
        client: ErmisClient,
        logicRegistry: LogicRegistry,
        stateRegistry: StateRegistry,
        clientState: ClientState,
        mutableGlobalState: MutableGlobalState,
        repos: RepositoryFacade,
        sideEffect: suspend () -> Unit,
        syncedEvents: Flow<List<ChatEvent>>,
    ): EventHandler {
        return EventHandlerSequential(
            scope = scope,
            currentUserId = user.id,
            subscribeForEvents = { listener -> client.subscribe(listener) },
            logicRegistry = logicRegistry,
            stateRegistry = stateRegistry,
            clientState = clientState,
            mutableGlobalState = mutableGlobalState,
            repos = repos,
            syncedEvents = syncedEvents,
            sideEffect = sideEffect,
        )
    }
}
