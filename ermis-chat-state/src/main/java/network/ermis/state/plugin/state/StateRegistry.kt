package network.ermis.state.plugin.state

import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.StateFlow
import network.ermis.client.channel.state.ChannelState
import network.ermis.client.events.ChannelDeletedEvent
import network.ermis.client.events.NotificationChannelDeletedEvent
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.User
import network.ermis.core.models.querysort.QuerySorter
import network.ermis.state.event.handler.batch.BatchEvent
import network.ermis.state.plugin.state.channel.ChannelMutableState
import network.ermis.state.plugin.state.channel.thread.ThreadMutableState
import network.ermis.state.plugin.state.channel.thread.ThreadState
import network.ermis.state.plugin.state.querychannels.QueryChannelsMutableState
import network.ermis.state.plugin.state.querychannels.QueryChannelsState
import java.util.concurrent.ConcurrentHashMap

/**
 * Registry of all state objects exposed in the offline plugin. This class should have only one instance for the SDK.
 *
 * @param userStateFlow The state flow that provides the user once it is set.
 * @param latestUsers Latest users of the SDK.
 * @param job A background job cancelled after calling [clear].
 * @param scope A scope for new coroutines.
 */
public class StateRegistry constructor(
    private val userStateFlow: StateFlow<User?>,
    private var latestUsers: StateFlow<Map<String, User>>,
    private val job: Job,
    private val scope: CoroutineScope,
) {

    private val logger by taggedLogger("Chat:StateRegistry")

    private val queryChannels: ConcurrentHashMap<Pair<FilterObject, QuerySorter<Channel>>, QueryChannelsMutableState> =
        ConcurrentHashMap()
    private val channels: ConcurrentHashMap<Pair<String, String>, ChannelMutableState> = ConcurrentHashMap()
    private val threads: ConcurrentHashMap<String, ThreadMutableState> = ConcurrentHashMap()

    /**
     * Returns [QueryChannelsState] associated with particular [filter] and [sort].
     *
     * @param filter Filter used to query channels.
     * @param sort Sort specification used to query channels.
     *
     * @return [QueryChannelsState] object.
     */
    public fun queryChannels(filter: FilterObject, sort: QuerySorter<Channel>): QueryChannelsState {
        return queryChannels.getOrPut(filter to sort) {
            QueryChannelsMutableState(filter, sort, scope, latestUsers)
        }
    }

    /**
     * Returns [ChannelState] that represents a state of particular channel.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     *
     * @return [ChannelState] object.
     */
    public fun channel(channelType: String, channelId: String): ChannelState = mutableChannel(channelType, channelId)

    /**
     * Returns [ChannelMutableState] that represents a state of particular channel.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     *
     * @return [ChannelState] object.
     */
    internal fun mutableChannel(channelType: String, channelId: String): ChannelMutableState {
        return channels.getOrPut(channelType to channelId) {
            ChannelMutableState(channelType, channelId, userStateFlow, latestUsers)
        }
    }

    internal fun markChannelAsRead(channelType: String, channelId: String): Boolean =
        mutableChannel(channelType = channelType, channelId = channelId).markChannelAsRead()

    /**
     * Checks if the channel is already present in the state.
     * Should be used to prevent creating [ChannelState] objects without populated data.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     *
     * @return true if the channel is active.
     */
    internal fun isActiveChannel(channelType: String, channelId: String): Boolean {
        return channels.containsKey(channelType to channelId)
    }

    /**
     * Returns [ThreadState] of thread replies with parent message that has id equal to [messageId].
     *
     * @param messageId Thread's parent message id.
     *
     * @return [ThreadState] object.
     */
    public fun thread(messageId: String): ThreadState = mutableThread(messageId)

    /**
     * Returns [ThreadMutableState] of thread replies with parent message that has id equal to [messageId].
     *
     * @param messageId Thread's parent message id.
     *
     * @return [ThreadMutableState] object.
     */
    internal fun mutableThread(messageId: String): ThreadMutableState = threads.getOrPut(messageId) {
        ThreadMutableState(messageId, scope)
    }

    internal fun getActiveQueryChannelsStates(): List<QueryChannelsState> = queryChannels.values.toList()

    internal fun getActiveChannelStates(): List<ChannelState> = channels.values.toList()

    /**
     * Clear state of all state objects.
     */
    public fun clear() {
        job.cancelChildren()
        queryChannels.forEach { it.value.destroy() }
        queryChannels.clear()
        channels.forEach { it.value.destroy() }
        channels.clear()
        threads.forEach { it.value.destroy() }
        threads.clear()
    }

    internal fun handleBatchEvent(batchEvent: BatchEvent) {
        for (event in batchEvent.sortedEvents) {
            when (event) {
                is ChannelDeletedEvent -> {
                    removeChanel(event.channelType, event.channelId)
                }
                is NotificationChannelDeletedEvent -> {
                    removeChanel(event.channelType, event.channelId)
                }
                else -> continue
            }
        }
    }

    private fun removeChanel(channelType: String, channelId: String) {
        val removed = channels.remove(channelType to channelId)?.also {
            it.destroy()
        }
        logger.i { "[removeChanel] removed channel($channelType, $channelId): $removed" }
    }
}
