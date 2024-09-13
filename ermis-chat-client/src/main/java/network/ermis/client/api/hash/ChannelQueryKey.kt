package network.ermis.client.api.hash

import network.ermis.client.api.model.requests.QueryChannelRequest

/**
 * A unique identifier of [QueryChannelRequest] per channel.
 */
internal data class ChannelQueryKey(
    val channelType: String,
    val channelId: String,
    val queryKey: QueryChannelRequest,
) {

    companion object {
        fun from(
            channelType: String,
            channelId: String,
            query: network.ermis.client.api.models.QueryChannelRequest,
        ): ChannelQueryKey {
            return ChannelQueryKey(
                channelType = channelType,
                channelId = channelId,
                queryKey = QueryChannelRequest(
                    state = query.state,
                    watch = query.watch,
                    presence = query.presence,
                    messages = query.messages,
                    watchers = query.watchers,
                    members = query.members,
                    data = query.data,
                ),
            )
        }
    }
}
