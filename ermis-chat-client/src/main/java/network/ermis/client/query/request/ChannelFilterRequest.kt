package network.ermis.client.query.request

import network.ermis.client.ErmisClient
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import io.getstream.result.Result

/* Default filter to include FilterObject in a channel by its cid
*
* @param filter - the filter to be included.
* @param offset - the offset to be included with the filter.
* @param limit - the filter to be included with the filter.
*/
public object ChannelFilterRequest {
    public suspend fun ErmisClient.filterWithOffset(
        filter: FilterObject,
        offset: Int,
        limit: Int,
    ): List<Channel> {
        val request = QueryChannelsRequest(
            filter = filter,
            offset = offset,
            limit = limit,
            messageLimit = 0,
            memberLimit = 0,
        )
        return queryChannelsInternal(request).await().let { result ->
            when (result) {
                is Result.Success -> result.value
                is Result.Failure -> emptyList()
            }
        }
    }
}
