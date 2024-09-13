package network.ermis.ui.common.feature.messages.composer.mention

import network.ermis.client.ErmisClient
import network.ermis.core.models.User
import network.ermis.ui.common.feature.messages.query.filter.DefaultQueryFilter
import network.ermis.ui.common.feature.messages.query.filter.QueryFilter
import io.getstream.log.taggedLogger

/**
 * Default implementation for [UserLookupHandler].
 *
 * @param localHandler The local user lookup handler.
 * @param remoteHandler The remote user lookup handler.
 */
public class DefaultUserLookupHandler(
    private val localHandler: UserLookupHandler,
    private val remoteHandler: UserLookupHandler,
) : UserLookupHandler {

    /**
     * Secondary constructor for [DefaultUserLookupHandler].
     *
     * @param chatClient Chat client used to query members.
     * @param channelCid The CID of the channel we are querying for members.
     * @param localFilter The filter used to filter the cached users during the local lookup.
     */
    @JvmOverloads
    public constructor(
        chatClient: ErmisClient,
        channelCid: String,
        localFilter: QueryFilter<User> = DefaultQueryFilter { it.name.ifBlank { it.id } },
    ) : this(
        localHandler = LocalUserLookupHandler(chatClient, channelCid, localFilter),
        remoteHandler = RemoteUserLookupHandler(chatClient, channelCid),
    )

    private val logger by taggedLogger("Chat:UserLookupHandler")

    override suspend fun handleUserLookup(query: String): List<User> {
        logger.d { "[handleUserLookup] query: \"$query\"" }
        return localHandler.handleUserLookup(query).ifEmpty {
            logger.v { "[handleUserLookup] no local results" }
            remoteHandler.handleUserLookup(query)
        }.also {
            logger.v { "[handleUserLookup] found ${it.size} users" }
        }
    }
}
