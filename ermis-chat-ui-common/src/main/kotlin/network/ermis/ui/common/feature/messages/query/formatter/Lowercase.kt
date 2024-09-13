package network.ermis.ui.common.feature.messages.query.formatter

/**
 * Lowercase the query string.
 */
internal class Lowercase : QueryFormatter {
    override fun format(query: String): String {
        if (query.isEmpty()) return query
        return query.lowercase()
    }
}
