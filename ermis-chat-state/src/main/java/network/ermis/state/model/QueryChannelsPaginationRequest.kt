package network.ermis.state.model

import network.ermis.core.models.Channel
import network.ermis.core.models.querysort.QuerySorter

internal data class QueryChannelsPaginationRequest(
    val sort: QuerySorter<Channel>,
    val channelOffset: Int = 0,
    val channelLimit: Int = 30,
    val messageLimit: Int = 10,
    val memberLimit: Int,
) {

    val isFirstPage: Boolean
        get() = channelOffset == 0
}
