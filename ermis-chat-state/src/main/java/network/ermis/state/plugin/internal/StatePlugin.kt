package network.ermis.state.plugin.internal

import network.ermis.client.errorhandler.ErrorHandler
import network.ermis.client.errorhandler.factory.ErrorHandlerFactory
import network.ermis.client.persistance.RepositoryFacade
import network.ermis.client.plugin.Plugin
import network.ermis.client.setup.ClientState
import network.ermis.core.models.User
import network.ermis.state.event.handler.EventHandler
import network.ermis.state.plugin.config.StatePluginConfig
import network.ermis.state.listener.ChannelMarkReadListenerState
import network.ermis.state.listener.DeleteChannelListenerState
import network.ermis.state.listener.DeleteMessageListenerState
import network.ermis.state.listener.DeleteReactionListenerState
import network.ermis.state.listener.EditMessageListenerState
import network.ermis.state.listener.FetchCurrentUserListenerState
import network.ermis.state.listener.HideChannelListenerState
import network.ermis.state.listener.MarkAllReadListenerState
import network.ermis.state.listener.QueryChannelListenerState
import network.ermis.state.listener.QueryChannelsListenerState
import network.ermis.state.listener.SendAttachmentListenerState
import network.ermis.state.listener.SendGiphyListenerState
import network.ermis.state.listener.SendMessageListenerState
import network.ermis.state.listener.SendReactionListenerState
import network.ermis.state.listener.ShuffleGiphyListenerState
import network.ermis.state.listener.ThreadQueryListenerState
import network.ermis.state.listener.TypingEventListenerState
import network.ermis.state.plugin.logic.LogicRegistry
import network.ermis.state.plugin.state.StateRegistry
import network.ermis.state.plugin.state.global.GlobalState
import network.ermis.state.plugin.state.global.MutableGlobalState
import network.ermis.state.sync.SyncHistoryManager
import network.ermis.state.sync.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

/**
 * Implementation of [Plugin] that brings support for the offline feature. This class work as a delegator of calls for one
 * of its dependencies, so avoid to add logic here.
 *
 * @param logic [LogicRegistry]
 * @param repositoryFacade [RepositoryFacade]
 * @param clientState [ClientState]
 * @param stateRegistry [StateRegistry]
 * @param syncManager [SyncManager]
 * @param eventHandler [EventHandler]
 * @param globalState [GlobalState]
 */
@Suppress("LongParameterList")
public class StatePlugin internal constructor(
    private val errorHandlerFactory: ErrorHandlerFactory,
    private val logic: LogicRegistry,
    private val repositoryFacade: RepositoryFacade,
    private val clientState: ClientState,
    private val stateRegistry: StateRegistry,
    private val syncManager: SyncManager,
    private val eventHandler: EventHandler,
    private val globalState: MutableGlobalState,
    private val queryingChannelsFree: MutableStateFlow<Boolean>,
    private val statePluginConfig: StatePluginConfig,
) : Plugin,
    network.ermis.client.plugin.listeners.QueryChannelsListener by QueryChannelsListenerState(logic, queryingChannelsFree),
    network.ermis.client.plugin.listeners.QueryChannelListener by QueryChannelListenerState(logic),
    network.ermis.client.plugin.listeners.ThreadQueryListener by ThreadQueryListenerState(logic, repositoryFacade),
    network.ermis.client.plugin.listeners.ChannelMarkReadListener by ChannelMarkReadListenerState(stateRegistry),
    network.ermis.client.plugin.listeners.EditMessageListener by EditMessageListenerState(logic, clientState),
    network.ermis.client.plugin.listeners.HideChannelListener by HideChannelListenerState(logic),
    network.ermis.client.plugin.listeners.MarkAllReadListener by MarkAllReadListenerState(logic, stateRegistry),
    network.ermis.client.plugin.listeners.DeleteReactionListener by DeleteReactionListenerState(logic, clientState),
    network.ermis.client.plugin.listeners.DeleteChannelListener by DeleteChannelListenerState(logic, clientState),
    network.ermis.client.plugin.listeners.SendReactionListener by SendReactionListenerState(logic, clientState),
    network.ermis.client.plugin.listeners.DeleteMessageListener by DeleteMessageListenerState(logic, clientState, globalState),
    network.ermis.client.plugin.listeners.SendGiphyListener by SendGiphyListenerState(logic),
    network.ermis.client.plugin.listeners.ShuffleGiphyListener by ShuffleGiphyListenerState(logic),
    network.ermis.client.plugin.listeners.SendMessageListener by SendMessageListenerState(logic),
    network.ermis.client.plugin.listeners.TypingEventListener by TypingEventListenerState(stateRegistry),
    network.ermis.client.plugin.listeners.SendAttachmentListener by SendAttachmentListenerState(logic),
    network.ermis.client.plugin.listeners.FetchCurrentUserListener by FetchCurrentUserListenerState(clientState, globalState) {

    override var errorHandler: ErrorHandler = errorHandlerFactory.create()

    override fun onUserSet(user: User) {
        syncManager.start()
        eventHandler.startListening()
    }

    override fun onUserDisconnected() {
        stateRegistry.clear()
        logic.clear()
        syncManager.stop()
        eventHandler.stopListening()
    }

    @Suppress("UNCHECKED_CAST")
    public override fun <T : Any> resolveDependency(klass: KClass<T>): T? = when (klass) {
        SyncHistoryManager::class -> syncManager as T
        EventHandler::class -> eventHandler as T
        LogicRegistry::class -> logic as T
        StateRegistry::class -> stateRegistry as T
        GlobalState::class -> globalState as T
        StatePluginConfig::class -> statePluginConfig as T
        else -> null
    }
}
