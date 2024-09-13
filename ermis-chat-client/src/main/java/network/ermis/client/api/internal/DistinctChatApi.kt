package network.ermis.client.api.internal

import network.ermis.client.api.ChatApi
import network.ermis.client.api.models.PinnedMessagesPagination
import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.client.api.models.QueryChannelsRequest
import network.ermis.client.api.hash.ChannelQueryKey
import network.ermis.client.api.hash.GetPinnedMessagesHash
import network.ermis.client.api.hash.GetReactionsHash
import network.ermis.client.api.hash.GetRepliesHash
import network.ermis.client.api.hash.QueryBanedUsersHash
import network.ermis.client.api.hash.QueryMembersHash
import network.ermis.core.models.BannedUser
import network.ermis.core.models.BannedUsersSort
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.log.StreamLog
import io.getstream.result.call.Call
import io.getstream.result.call.DistinctCall
import kotlinx.coroutines.CoroutineScope
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

/**
 * Prevents simultaneous network calls of the same request.
 */
@Suppress("UNCHECKED_CAST")
internal class DistinctChatApi(
    private val scope: CoroutineScope,
    internal val delegate: ChatApi,
) : ChatApi by delegate {

    private val distinctCalls = ConcurrentHashMap<Int, DistinctCall<out Any>>()

    override fun queryChannel(channelType: String, channelId: String, query: QueryChannelRequest): Call<Channel> {
        val uniqueKey = ChannelQueryKey.from(channelType, channelId, query).hashCode()
        StreamLog.d(TAG) { "[queryChannel] channelType: $channelType, channelId: $channelId, uniqueKey: $uniqueKey" }
        return getOrCreate(uniqueKey) {
            delegate.queryChannel(channelType, channelId, query)
        }
    }

    override fun getRepliesMore(messageId: String, firstId: String, limit: Int): Call<List<Message>> {
        val uniqueKey = GetRepliesHash(messageId, firstId, limit).hashCode()
        StreamLog.d(TAG) {
            "[getRepliesMore] messageId: $messageId, firstId: $firstId, limit: $limit, uniqueKey: $uniqueKey"
        }
        return getOrCreate(uniqueKey) {
            delegate.getRepliesMore(messageId, firstId, limit)
        }
    }

    override fun getReplies(messageId: String, limit: Int): Call<List<Message>> {
        val uniqueKey = GetRepliesHash(messageId, null, limit).hashCode()
        StreamLog.d(TAG) { "[getReplies] messageId: $messageId, limit: $limit, uniqueKey: $uniqueKey" }
        return getOrCreate(uniqueKey) {
            delegate.getReplies(messageId, limit)
        }
    }

    override fun getReactions(messageId: String, offset: Int, limit: Int): Call<List<Reaction>> {
        val uniqueKey = GetReactionsHash(messageId, offset, limit).hashCode()
        StreamLog.d(TAG) {
            "[getReactions] messageId: $messageId, offset: $offset, limit: $limit, uniqueKey: $uniqueKey"
        }
        return getOrCreate(uniqueKey) {
            delegate.getReactions(messageId, offset, limit)
        }
    }

    override fun getMessage(messageId: String): Call<Message> {
        val uniqueKey = messageId.hashCode()
        StreamLog.d(TAG) { "[getMessage] messageId: $messageId, uniqueKey: $uniqueKey" }
        return getOrCreate(uniqueKey) {
            delegate.getMessage(messageId)
        }
    }

    override fun getPinnedMessages(
        channelType: String,
        channelId: String,
        limit: Int,
        sort: QuerySorter<Message>,
        pagination: PinnedMessagesPagination,
    ): Call<List<Message>> {
        val uniqueKey = GetPinnedMessagesHash(channelType, channelId, limit, sort, pagination).hashCode()
        StreamLog.d(TAG) {
            "[getPinnedMessages] channelType: $channelType, channelId: $channelId, " +
                "limit: $limit, sort: $sort, pagination: $pagination, uniqueKey: $uniqueKey"
        }
        return getOrCreate(uniqueKey) {
            delegate.getPinnedMessages(channelType, channelId, limit, sort, pagination)
        }
    }

    override fun queryChannels(query: QueryChannelsRequest): Call<List<Channel>> {
        val uniqueKey = query.hashCode()
        StreamLog.d(TAG) { "[queryChannels] query: $query, uniqueKey: $uniqueKey" }
        return getOrCreate(uniqueKey) {
            delegate.queryChannels(query)
        }
    }

    override fun queryBannedUsers(
        filter: FilterObject,
        sort: QuerySorter<BannedUsersSort>,
        offset: Int?,
        limit: Int?,
        createdAtAfter: Date?,
        createdAtAfterOrEqual: Date?,
        createdAtBefore: Date?,
        createdAtBeforeOrEqual: Date?,
    ): Call<List<BannedUser>> {
        val uniqueKey = QueryBanedUsersHash(
            filter,
            sort,
            offset,
            limit,
            createdAtAfter,
            createdAtAfterOrEqual,
            createdAtBefore,
            createdAtBeforeOrEqual,
        ).hashCode()

        StreamLog.d(TAG) { "[queryBannedUsers] uniqueKey: $uniqueKey" }

        return getOrCreate(uniqueKey) {
            delegate.queryBannedUsers(
                filter,
                sort,
                offset,
                limit,
                createdAtAfter,
                createdAtAfterOrEqual,
                createdAtBefore,
                createdAtBeforeOrEqual,
            )
        }
    }

    override fun queryMembers(
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    ): Call<List<Member>> {
        val uniqueKey = QueryMembersHash(channelType, channelId, offset, limit, filter, sort, members)
            .hashCode()

        StreamLog.d(TAG) { "[queryMembers] uniqueKey: $uniqueKey" }

        return getOrCreate(uniqueKey) {
            delegate.queryMembers(channelType, channelId, offset, limit, filter, sort, members)
        }
    }

    private fun <T : Any> getOrCreate(
        uniqueKey: Int,
        callBuilder: () -> Call<T>,
    ): Call<T> {
        return distinctCalls[uniqueKey] as? DistinctCall<T>
            ?: DistinctCall(scope = scope, callBuilder = callBuilder) {
                distinctCalls.remove(uniqueKey)
            }.also {
                distinctCalls[uniqueKey] = it
            }
    }

    private companion object {
        private const val TAG = "Chat:DistinctApi"
    }
}
