package network.ermis.ui.common.feature.messages.query.formatter

import network.ermis.ui.common.feature.messages.composer.transliteration.DefaultStreamTransliterator
import network.ermis.ui.common.feature.messages.composer.transliteration.StreamTransliterator

/**
 * Transliterate the query string.
 */
public class Transliterate(
    private val transliterator: StreamTransliterator = DefaultStreamTransliterator(),
) : QueryFormatter {
    override fun format(query: String): String {
        if (query.isEmpty()) return query
        return transliterator.transliterate(query)
    }
}
