package network.ermis.ui.common.feature.messages.query.formatter

/**
 * Transforms the query string.
 */
public fun interface QueryFormatter {

    /**
     * Transforms the given [query].
     */
    public fun format(query: String): String
}
