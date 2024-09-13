package network.ermis.client.query

import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.querysort.QuerySorter

public data class QueryChannelsSpec(
    val filter: FilterObject,
    val querySort: QuerySorter<Channel>,
) {
    var cids: Set<String> = emptySet()
}
