package network.ermis.state.model

import network.ermis.client.api.models.Pagination
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.WatchChannelRequest
import network.ermis.client.query.pagination.AnyChannelPaginationRequest

internal class QueryChannelPaginationRequest(var messageLimit: Int = 30) : QueryChannelRequest() {

    var messageFilterDirection: Pagination? = null
    var messageFilterValue: String = ""

    var memberLimit: Int = 30
    var memberOffset: Int = 0

    var watcherLimit: Int = 30
    var watcherOffset: Int = 0

    fun hasFilter(): Boolean {
        return messageFilterDirection != null
    }

    fun isFirstPage(): Boolean {
        return messageFilterDirection == null
    }

    internal fun toAnyChannelPaginationRequest(): AnyChannelPaginationRequest {
        val originalRequest = this
        return AnyChannelPaginationRequest().apply {
            this.messageLimit = originalRequest.messageLimit
            this.messageFilterDirection = originalRequest.messageFilterDirection
            this.memberLimit = originalRequest.memberLimit
            this.memberOffset = originalRequest.memberOffset
            this.watcherLimit = originalRequest.watcherLimit
            this.watcherOffset = originalRequest.watcherOffset
            this.channelLimit = 1
        }
    }

    fun toWatchChannelRequest(userPresence: Boolean): WatchChannelRequest = WatchChannelRequest().apply {
        withMessages(messageLimit)
        if (userPresence) {
            withPresence()
        }
        if (hasFilter()) {
            withMessages(messageFilterDirection!!, messageFilterValue, messageLimit)
        }
        withMembers(memberLimit, memberOffset)
        withWatchers(watcherLimit, watcherOffset)
    }
}
