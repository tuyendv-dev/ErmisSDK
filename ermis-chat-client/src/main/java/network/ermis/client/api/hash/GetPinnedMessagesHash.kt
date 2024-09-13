package network.ermis.client.api.hash

import network.ermis.client.api.models.PinnedMessagesPagination
import network.ermis.core.models.Message
import network.ermis.core.models.querysort.QuerySorter

internal data class GetPinnedMessagesHash(
    val channelType: String,
    val channelId: String,
    val limit: Int,
    val sort: QuerySorter<Message>,
    val pagination: PinnedMessagesPagination,
)
