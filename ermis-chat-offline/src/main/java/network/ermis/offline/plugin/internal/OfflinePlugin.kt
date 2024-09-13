package network.ermis.offline.plugin.internal

import network.ermis.client.errorhandler.ErrorHandler
import network.ermis.client.plugin.Plugin
import network.ermis.client.plugin.listeners.CreateChannelListener
import network.ermis.client.plugin.listeners.DeleteChannelListener
import network.ermis.client.plugin.listeners.DeleteMessageListener
import network.ermis.client.plugin.listeners.DeleteReactionListener
import network.ermis.client.plugin.listeners.EditMessageListener
import network.ermis.client.plugin.listeners.FetchCurrentUserListener
import network.ermis.client.plugin.listeners.GetMessageListener
import network.ermis.client.plugin.listeners.HideChannelListener
import network.ermis.client.plugin.listeners.QueryChannelListener
import network.ermis.client.plugin.listeners.QueryMembersListener
import network.ermis.client.plugin.listeners.SendMessageListener
import network.ermis.client.plugin.listeners.SendReactionListener
import network.ermis.client.plugin.listeners.ThreadQueryListener
import network.ermis.core.models.User
import kotlin.reflect.KClass

/**
 * Implementation of [Plugin] that brings support for the offline feature. This class work as a delegator of calls for one
 * of its dependencies, so avoid to add logic here.
 *
 * @param queryChannelListener [QueryChannelListener]
 * @param threadQueryListener [ThreadQueryListener]
 * @param editMessageListener [EditMessageListener]
 * @param hideChannelListener [HideChannelListener]
 * @param deleteReactionListener [DeleteReactionListener]
 * @param sendReactionListener [SendReactionListener]
 * @param deleteMessageListener [DeleteMessageListener]
 * @param sendMessageListener [SendMessageListener]
 * @param queryMembersListener [QueryMembersListener]
 * @param createChannelListener [CreateChannelListener]
 * @param deleteChannelListener [DeleteChannelListener]
 * @param getMessageListener [GetMessageListener]
 * @param fetchCurrentUserListener [FetchCurrentUserListener]
 * @param activeUser User associated with [OfflinePlugin] instance.
 * @param provideDependency Resolves dependency within [OfflinePlugin].
 */
@Suppress("LongParameterList")
internal class OfflinePlugin(
    internal val activeUser: User,
    private val queryChannelListener: network.ermis.client.plugin.listeners.QueryChannelListener,
    private val threadQueryListener: network.ermis.client.plugin.listeners.ThreadQueryListener,
    private val editMessageListener: network.ermis.client.plugin.listeners.EditMessageListener,
    private val hideChannelListener: network.ermis.client.plugin.listeners.HideChannelListener,
    private val deleteReactionListener: network.ermis.client.plugin.listeners.DeleteReactionListener,
    private val sendReactionListener: network.ermis.client.plugin.listeners.SendReactionListener,
    private val deleteMessageListener: network.ermis.client.plugin.listeners.DeleteMessageListener,
    private val shuffleGiphyListener: network.ermis.client.plugin.listeners.ShuffleGiphyListener,
    private val sendMessageListener: network.ermis.client.plugin.listeners.SendMessageListener,
    private val sendAttachmentListener: network.ermis.client.plugin.listeners.SendAttachmentListener,
    private val queryMembersListener: network.ermis.client.plugin.listeners.QueryMembersListener,
    private val createChannelListener: network.ermis.client.plugin.listeners.CreateChannelListener,
    private val deleteChannelListener: network.ermis.client.plugin.listeners.DeleteChannelListener,
    private val getMessageListener: network.ermis.client.plugin.listeners.GetMessageListener,
    private val fetchCurrentUserListener: network.ermis.client.plugin.listeners.FetchCurrentUserListener,
    private val provideDependency: (KClass<*>) -> Any? = { null },
) : Plugin,
    network.ermis.client.plugin.listeners.QueryChannelListener by queryChannelListener,
    network.ermis.client.plugin.listeners.ThreadQueryListener by threadQueryListener,
    network.ermis.client.plugin.listeners.EditMessageListener by editMessageListener,
    network.ermis.client.plugin.listeners.HideChannelListener by hideChannelListener,
    network.ermis.client.plugin.listeners.DeleteReactionListener by deleteReactionListener,
    network.ermis.client.plugin.listeners.SendReactionListener by sendReactionListener,
    network.ermis.client.plugin.listeners.DeleteMessageListener by deleteMessageListener,
    network.ermis.client.plugin.listeners.ShuffleGiphyListener by shuffleGiphyListener,
    network.ermis.client.plugin.listeners.SendMessageListener by sendMessageListener,
    network.ermis.client.plugin.listeners.QueryMembersListener by queryMembersListener,
    network.ermis.client.plugin.listeners.CreateChannelListener by createChannelListener,
    network.ermis.client.plugin.listeners.DeleteChannelListener by deleteChannelListener,
    network.ermis.client.plugin.listeners.SendAttachmentListener by sendAttachmentListener,
    network.ermis.client.plugin.listeners.GetMessageListener by getMessageListener,
    network.ermis.client.plugin.listeners.FetchCurrentUserListener by fetchCurrentUserListener {

    override val errorHandler: ErrorHandler? = null

    override fun onUserSet(user: User) {
        /* No-Op */
    }

    override fun onUserDisconnected() {
        /* No-Op */
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> resolveDependency(klass: KClass<T>): T? = provideDependency(klass) as? T
}
