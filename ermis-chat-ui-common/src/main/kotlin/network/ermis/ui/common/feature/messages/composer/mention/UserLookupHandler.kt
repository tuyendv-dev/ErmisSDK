package network.ermis.ui.common.feature.messages.composer.mention

import network.ermis.core.models.User
import network.ermis.ui.common.feature.messages.query.formatter.QueryFormatter

/**
 * Users lookup functional interface. Used to create custom users lookup algorithm.
 */
public fun interface UserLookupHandler {
    /**
     * Performs users lookup by given [query] in suspend way.
     * It's executed on background, so it can perform heavy operations.
     *
     * @param query String as user input for lookup algorithm.
     * @return List of users as result of lookup.
     */
    public suspend fun handleUserLookup(query: String): List<User>
}

/**
 * Wraps the current [UserLookupHandler] with the provided [queryFormatter].
 *
 * @param queryFormatter The query formatter to be used.
 */
public fun UserLookupHandler.withQueryFormatter(
    queryFormatter: QueryFormatter,
): UserLookupHandler {
    val delegate = this
    return UserLookupHandler { query ->
        val updatedQuery = queryFormatter.format(query)
        delegate.handleUserLookup(updatedQuery)
    }
}
