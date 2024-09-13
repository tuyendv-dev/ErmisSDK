package network.ermis.client.api.hash

import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.querysort.QuerySorter

internal data class QueryMembersHash(
    val channelType: String,
    val channelId: String,
    val offset: Int,
    val limit: Int,
    val filter: FilterObject,
    val sort: QuerySorter<Member>,
    val members: List<Member>,
)
