package network.ermis.state.plugin.state.querychannels

import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.querysort.QuerySorter
import network.ermis.state.event.chat.ChatEventHandler
import network.ermis.state.event.chat.factory.ChatEventHandlerFactory
import kotlinx.coroutines.flow.StateFlow

/**
 * Contains a state related to a single query channels request.
 */
public interface QueryChannelsState {
    /** If the channels need to be synced. */
    public val recoveryNeeded: StateFlow<Boolean>

    /** The filter is associated with this query channels state. */
    public val filter: FilterObject

    /** The sort object which requested for this query channels state. */
    public val sort: QuerySorter<Channel>

    /** The request for the current page. */
    public val currentRequest: StateFlow<QueryChannelsRequest?>

    /** The request for the next page, if there is a page. */
    public val nextPageRequest: StateFlow<QueryChannelsRequest?>

    /** If the current state is being loaded. */
    public val loading: StateFlow<Boolean>

    /** If the current state is loading more channels (a next page is being loaded). */
    public val loadingMore: StateFlow<Boolean>

    /** If the current state reached the final page. */
    public val endOfChannels: StateFlow<Boolean>

    /**
     *  The collection of channels loaded by the query channels request.
     *  The StateFlow is initialized with null which means that channels are not loaded yet.
     */
    public val channels: StateFlow<List<Channel>?>

    /** The channels loaded state. See [ChannelsStateData]. */
    public val channelsStateData: StateFlow<ChannelsStateData>

    /**
     * Factory that produces [ChatEventHandler], which decides whether the set of channels should be updated.
     */
    public var chatEventHandlerFactory: ChatEventHandlerFactory?
}
