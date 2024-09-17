package network.ermis.offline.plugin.factory

import android.content.Context
import io.getstream.log.taggedLogger
import kotlinx.coroutines.SupervisorJob
import network.ermis.client.ErmisClient
import network.ermis.client.persistance.factory.RepositoryFactory
import network.ermis.client.plugin.Plugin
import network.ermis.client.plugin.factory.PluginFactory
import network.ermis.core.models.User
import network.ermis.offline.plugin.internal.OfflinePlugin
import network.ermis.offline.plugin.listener.CreateChannelListenerDatabase
import network.ermis.offline.plugin.listener.DeleteChannelListenerDatabase
import network.ermis.offline.plugin.listener.DeleteMessageListenerDatabase
import network.ermis.offline.plugin.listener.DeleteReactionListenerDatabase
import network.ermis.offline.plugin.listener.EditMessageListenerDatabase
import network.ermis.offline.plugin.listener.FetchCurrentUserListenerDatabase
import network.ermis.offline.plugin.listener.GetMessageListenerDatabase
import network.ermis.offline.plugin.listener.HideChannelListenerDatabase
import network.ermis.offline.plugin.listener.QueryChannelListenerDatabase
import network.ermis.offline.plugin.listener.QueryMembersListenerDatabase
import network.ermis.offline.plugin.listener.SendAttachmentsListenerDatabase
import network.ermis.offline.plugin.listener.SendMessageListenerDatabase
import network.ermis.offline.plugin.listener.SendReactionListenerDatabase
import network.ermis.offline.plugin.listener.ShuffleGiphyListenerDatabase
import network.ermis.offline.plugin.listener.ThreadQueryListenerDatabase
import network.ermis.offline.repository.database.ChatDatabase
import network.ermis.offline.repository.factory.DatabaseRepositoryFactory
import kotlin.reflect.KClass

/**
 * Implementation of [PluginFactory] that provides [OfflinePlugin].
 *
 * @param appContext [Context]
 */
public class ErmisOfflinePluginFactory(private val appContext: Context) : PluginFactory, RepositoryFactory.Provider {

    private val logger by taggedLogger("Chat:OfflinePluginFactory")

    override fun <T : Any> resolveDependency(klass: KClass<T>): T? {
        return when (klass) {
            else -> null
        }
    }

    override fun createRepositoryFactory(user: User): RepositoryFactory {
        logger.d { "[createRepositoryFactory] user.id: '${user.id}'" }
        return DatabaseRepositoryFactory(
            database = createDatabase(appContext, user),
            currentUser = user,
            scope = ErmisClient.instance().inheritScope { SupervisorJob(it) },
        )
    }

    /**
     * Creates a [Plugin]
     *
     * @return The [Plugin] instance.
     */
    override fun get(user: User): Plugin {
        logger.d { "[get] user.id: ${user.id}" }
        return createOfflinePlugin(user)
    }

    /**
     * Tries to get cached [OfflinePlugin] instance for the user if it exists or
     * creates the new [OfflinePlugin] and initialized its dependencies.
     *
     * This method must be called after the user is set in the SDK.
     */
    @Suppress("LongMethod")
    private fun createOfflinePlugin(user: User): OfflinePlugin {
        logger.v { "[createOfflinePlugin] user.id: ${user.id}" }
        ErmisClient.OFFLINE_SUPPORT_ENABLED = true

        val chatClient = ErmisClient.instance()
        val clientState = chatClient.clientState
        val repositoryFacade = chatClient.repositoryFacade

        val queryChannelListener = QueryChannelListenerDatabase(repositoryFacade)

        val threadQueryListener = ThreadQueryListenerDatabase(repositoryFacade, repositoryFacade)

        val editMessageListener = EditMessageListenerDatabase(
            userRepository = repositoryFacade,
            messageRepository = repositoryFacade,
            clientState = clientState,
        )

        val hideChannelListener: network.ermis.client.plugin.listeners.HideChannelListener = HideChannelListenerDatabase(
            channelRepository = repositoryFacade,
            messageRepository = repositoryFacade,
        )

        val deleteReactionListener: network.ermis.client.plugin.listeners.DeleteReactionListener = DeleteReactionListenerDatabase(
            clientState = clientState,
            reactionsRepository = repositoryFacade,
            messageRepository = repositoryFacade,
        )

        val sendReactionListener = SendReactionListenerDatabase(
            clientState = clientState,
            messageRepository = repositoryFacade,
            reactionsRepository = repositoryFacade,
            userRepository = repositoryFacade,
        )

        val deleteMessageListener: network.ermis.client.plugin.listeners.DeleteMessageListener = DeleteMessageListenerDatabase(
            clientState = clientState,
            messageRepository = repositoryFacade,
            userRepository = repositoryFacade,
        )

        val sendMessageListener: network.ermis.client.plugin.listeners.SendMessageListener = SendMessageListenerDatabase(
            repositoryFacade,
            repositoryFacade,
        )

        val sendAttachmentListener: network.ermis.client.plugin.listeners.SendAttachmentListener = SendAttachmentsListenerDatabase(
            repositoryFacade,
            repositoryFacade,
        )

        val shuffleGiphyListener: network.ermis.client.plugin.listeners.ShuffleGiphyListener = ShuffleGiphyListenerDatabase(
            userRepository = repositoryFacade,
            messageRepository = repositoryFacade,
        )

        val queryMembersListener: network.ermis.client.plugin.listeners.QueryMembersListener = QueryMembersListenerDatabase(
            repositoryFacade,
            repositoryFacade,
        )

        val createChannelListener: network.ermis.client.plugin.listeners.CreateChannelListener = CreateChannelListenerDatabase(
            clientState = clientState,
            channelRepository = repositoryFacade,
            userRepository = repositoryFacade,
        )

        val deleteChannelListener: network.ermis.client.plugin.listeners.DeleteChannelListener = DeleteChannelListenerDatabase(
            clientState = clientState,
            channelRepository = repositoryFacade,
            userRepository = repositoryFacade,
        )

        val getMessageListener: network.ermis.client.plugin.listeners.GetMessageListener = GetMessageListenerDatabase(
            repositoryFacade = repositoryFacade,
        )

        val fetchCurrentUserListener = FetchCurrentUserListenerDatabase(
            userRepository = repositoryFacade,
        )

        return OfflinePlugin(
            activeUser = user,
            queryChannelListener = queryChannelListener,
            threadQueryListener = threadQueryListener,
            editMessageListener = editMessageListener,
            hideChannelListener = hideChannelListener,
            deleteReactionListener = deleteReactionListener,
            sendReactionListener = sendReactionListener,
            deleteMessageListener = deleteMessageListener,
            sendMessageListener = sendMessageListener,
            sendAttachmentListener = sendAttachmentListener,
            shuffleGiphyListener = shuffleGiphyListener,
            queryMembersListener = queryMembersListener,
            createChannelListener = createChannelListener,
            deleteChannelListener = deleteChannelListener,
            getMessageListener = getMessageListener,
            fetchCurrentUserListener = fetchCurrentUserListener,
        )
    }

    private fun createDatabase(
        context: Context,
        user: User,
    ): ChatDatabase {
        return ChatDatabase.getDatabase(context, user.id)
    }
}
