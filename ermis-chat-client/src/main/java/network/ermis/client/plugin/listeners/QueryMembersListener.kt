package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.User
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.result.Result

/**
 * Listener of [ErmisClient.queryMembers] requests.
 */
public interface QueryMembersListener {

    /**
     * Runs this function on the [Result] of this request.
     *
     * @param result Result of this request.
     * @param channelType The type of channel.
     * @param channelId The id of the channel.
     * @param offset Offset limit.
     * @param limit Number of members to fetch.
     * @param filter [FilterObject] to filter members of certain type.
     * @param sort Sort the list of members.
     * @param members List of members.
     */
    @Suppress("LongParameterList")
    public suspend fun onQueryMembersResult(
        result: Result<List<Member>>,
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    )

    @Suppress("LongParameterList")
    public suspend fun onQueryUserListResult(
        result: Result<List<User>>
    )

    public suspend fun onGetInfoCurrentUserResult(result: Result<User>)

    public suspend fun getUserDB(userId: String): Result<User>
}
