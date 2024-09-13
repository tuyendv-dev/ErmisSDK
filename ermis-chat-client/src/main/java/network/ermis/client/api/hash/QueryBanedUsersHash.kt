package network.ermis.client.api.hash

import network.ermis.core.models.BannedUsersSort
import network.ermis.core.models.FilterObject
import network.ermis.core.models.querysort.QuerySorter
import java.util.Date

internal data class QueryBanedUsersHash(
    val filter: FilterObject,
    val sort: QuerySorter<BannedUsersSort>,
    val offset: Int?,
    val limit: Int?,
    val createdAtAfter: Date?,
    val createdAtAfterOrEqual: Date?,
    val createdAtBefore: Date?,
    val createdAtBeforeOrEqual: Date?,
)
