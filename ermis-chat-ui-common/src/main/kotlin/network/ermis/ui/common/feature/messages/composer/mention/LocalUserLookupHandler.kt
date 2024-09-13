package network.ermis.ui.common.feature.messages.composer.mention

import network.ermis.client.ErmisClient
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.core.models.User
import network.ermis.state.extensions.state
import network.ermis.ui.common.feature.messages.query.filter.DefaultQueryFilter
import network.ermis.ui.common.feature.messages.query.filter.QueryFilter
import io.getstream.log.taggedLogger

/**
 * Local user lookup handler. It uses the local state to search for users.
 *
 * @param chatClient Chat client used to query members.
 * @param channelCid The CID of the channel we are querying for members.
 * @param filter The filter used to filter the users.
 */
public class LocalUserLookupHandler @JvmOverloads constructor(
    private val chatClient: ErmisClient,
    private val channelCid: String,
    private val filter: QueryFilter<User> = DefaultQueryFilter { it.name.ifBlank { it.id } },
) : UserLookupHandler {

    private val logger by taggedLogger("Chat:UserLookupLocal")

    override suspend fun handleUserLookup(query: String): List<User> {
        try {
            if (DEBUG) logger.d { "[handleUserLookup] query: \"$query\"" }
            val (channelType, channelId) = channelCid.cidToTypeAndId()
            val channelState = chatClient.state.channel(channelType, channelId)
            val localUsers = channelState.members.value.map { it.user }
            val membersCount = channelState.membersCount.value
            return when (membersCount == localUsers.size) {
                true -> filter.filter(localUsers, query).also {
                    if (DEBUG) logger.v { "[handleUserLookup] found ${it.size} users" }
                }
                else -> {
                    if (DEBUG) logger.v { "[handleUserLookup] #empty; users: ${localUsers.size} out of $membersCount" }
                    emptyList()
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "[handleUserLookup] failed: $e" }
            return emptyList()
        }
    }

    private companion object {
        private const val DEBUG = false
    }
}
