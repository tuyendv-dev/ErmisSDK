package network.ermis.ui.common.feature.messages.query.formatter

import java.text.Normalizer

/**
 * Ignore diacritics in the query string.
 */
internal class IgnoreDiacritics : QueryFormatter {

    private val diacriticsRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    override fun format(query: String): String {
        return query.removeDiacritics()
    }

    private fun String.removeDiacritics(): String {
        if (this.isEmpty()) return this
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return normalized.replace(diacriticsRegex, "")
    }
}
