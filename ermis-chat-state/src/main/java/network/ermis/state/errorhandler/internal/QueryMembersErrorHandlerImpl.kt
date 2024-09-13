package network.ermis.state.errorhandler.internal

import network.ermis.client.errorhandler.QueryMembersErrorHandler
import network.ermis.client.utils.extensions.internal.toCid
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.setup.ClientState
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall
import io.getstream.result.call.onErrorReturn
import kotlinx.coroutines.CoroutineScope

/**
 * Checks if the change was done offline and can be synced.
 *
 * @param scope [CoroutineScope]
 */
internal class QueryMembersErrorHandlerImpl(
    private val scope: CoroutineScope,
    private val clientState: ClientState,
    private val channelRepository: ChannelRepository,
) : QueryMembersErrorHandler {

    private val logger by taggedLogger("QueryMembersError")

    override fun onQueryMembersError(
        originalCall: Call<List<Member>>,
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    ): ReturnOnErrorCall<List<Member>> {
        return originalCall.onErrorReturn(scope) { originalError ->
            logger.d {
                "An error happened while wuery members. " +
                    "Error message: ${originalError.message}. Full error: $originalCall"
            }

            if (clientState.isOnline) {
                Result.Failure(originalError)
            } else {
                // retrieve from database
                val clampedOffset = offset.coerceAtLeast(0)
                val clampedLimit = limit.coerceAtLeast(0)
                val membersFromDatabase = channelRepository
                    .selectMembersForChannel(Pair(channelType, channelId).toCid())
                    .sortedWith(sort.comparator)
                    .drop(clampedOffset)
                    .let { members ->
                        if (clampedLimit > 0) {
                            members.take(clampedLimit)
                        } else {
                            members
                        }
                    }
                Result.Success(membersFromDatabase)
            }
        }
    }
}
