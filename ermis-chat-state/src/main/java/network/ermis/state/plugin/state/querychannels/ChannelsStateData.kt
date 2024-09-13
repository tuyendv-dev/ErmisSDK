package network.ermis.state.plugin.state.querychannels

import network.ermis.core.models.Channel

public sealed class ChannelsStateData {
    /** No query is currently running.
     * If you know that a query will be started you typically want to display a loading icon.
     */
    public object NoQueryActive : ChannelsStateData() {
        override fun toString(): String = "ChannelsStateData.NoQueryActive"
    }

    /** Indicates we are loading the first page of results.
     * We are in this state if QueryChannelsState.loading is true
     * For seeing if we're loading more results have a look at QueryChannelsState.loadingMore
     *
     * @see QueryChannelsState.loadingMore
     * @see QueryChannelsState.loading
     */
    public object Loading : ChannelsStateData() {
        override fun toString(): String = "ChannelsStateData.Loading"
    }

    /** If we are offline and don't have channels stored in offline storage, typically displayed as an error condition. */
    public object OfflineNoResults : ChannelsStateData() {
        override fun toString(): String = "ChannelsStateData.OfflineNoResults"
    }

    /** The list of channels, loaded either from offline storage or an API call.
     * Observe chatDomain.online to know if results are currently up to date
     */
    public data class Result(val channels: List<Channel>) : ChannelsStateData() {
        override fun toString(): String {
            return "ChannelsStateData.Result(channels.size=${channels.size})"
        }
    }
}
