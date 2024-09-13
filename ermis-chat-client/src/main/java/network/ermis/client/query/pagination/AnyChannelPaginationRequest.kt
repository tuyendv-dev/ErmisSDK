package network.ermis.client.query.pagination

import network.ermis.client.api.models.Pagination
import network.ermis.core.models.Channel
import network.ermis.core.models.querysort.QuerySortByField
import network.ermis.core.models.querysort.QuerySorter

private const val MESSAGE_LIMIT = 30
private const val CHANNEL_LIMIT = 30
private const val MEMBER_LIMIT = 30
private const val WATCHER_LIMIT = 30

public class AnyChannelPaginationRequest(public var messageLimit: Int = MESSAGE_LIMIT) {
    public var messageFilterDirection: Pagination? = null
    public var messageFilterValue: String = ""
    public var sort: QuerySorter<Channel> = QuerySortByField()

    public var channelLimit: Int = CHANNEL_LIMIT
    public var channelOffset: Int = 0

    public var memberLimit: Int = MEMBER_LIMIT
    public var memberOffset: Int = 0

    public var watcherLimit: Int = WATCHER_LIMIT
    public var watcherOffset: Int = 0
}

internal fun AnyChannelPaginationRequest.isFirstPage(): Boolean {
    return channelOffset == 0
}

public fun AnyChannelPaginationRequest.isRequestingMoreThanLastMessage(): Boolean {
    return (isFirstPage() && messageLimit > 1) || (isNotFirstPage() && messageLimit > 0)
}

internal fun AnyChannelPaginationRequest.isNotFirstPage(): Boolean = isFirstPage().not()
