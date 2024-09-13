package network.ermis.ui.common.feature.messages.query.filter

/**
 * Filters the list of items based on the query string.
 */
public fun interface QueryFilter<T> {

    /**
     * Filters the given [items] based on the [query].
     */
    public fun filter(items: List<T>, query: String): List<T>
}
