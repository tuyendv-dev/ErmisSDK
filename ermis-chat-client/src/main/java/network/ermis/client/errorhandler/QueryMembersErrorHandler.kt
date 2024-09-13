package network.ermis.client.errorhandler

import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall

public interface QueryMembersErrorHandler {

    /**
     * Returns a [Result] from this side effect when original request is failed.
     *
     * @param originalCall The original call.
     * @param cid The full channel id, i.e. "messaging:123".
     * @param messageId The id of the message to which reaction belongs.
     *
     * @return result The replacement for the original result.
     */
    @Suppress("LongParameterList")
    public fun onQueryMembersError(
        originalCall: Call<List<Member>>,
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    ): ReturnOnErrorCall<List<Member>>
}

@Suppress("LongParameterList")
internal fun Call<List<Member>>.onQueryMembersError(
    errorHandlers: List<QueryMembersErrorHandler>,
    channelType: String,
    channelId: String,
    offset: Int,
    limit: Int,
    filter: FilterObject,
    sort: QuerySorter<Member>,
    members: List<Member>,
): Call<List<Member>> {
    return errorHandlers.fold(this) { messageCall, errorHandler ->
        errorHandler.onQueryMembersError(messageCall, channelType, channelId, offset, limit, filter, sort, members)
    }
}
