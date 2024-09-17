package network.ermis.ui.common.feature.messages.composer.mention

import network.ermis.core.models.User
import network.ermis.ui.common.feature.messages.composer.transliteration.DefaultStreamTransliterator
import network.ermis.ui.common.feature.messages.composer.transliteration.StreamTransliterator
import network.ermis.ui.common.feature.messages.query.filter.DefaultQueryFilter
import network.ermis.ui.common.feature.messages.query.filter.QueryFilter

/**
 * Default implementation of [QueryFilter] for [User] objects.
 *
 * This implementation of [QueryFilter] ignores upper case, diacritics
 * It uses levenshtein approximation so typos are included in the search.
 *
 * It is possible to choose a transliteration by providing a [transliterator].
 *
 * @param transliterator The transliterator to use for transliterating the query string.
 */
public class DefaultUserQueryFilter(
    transliterator: StreamTransliterator = DefaultStreamTransliterator(),
) : QueryFilter<User> {

    private val delegate = DefaultQueryFilter<User>(transliterator) { it.name.ifBlank { it.id } }

    override fun filter(items: List<User>, query: String): List<User> {
        return delegate.filter(items, query)
    }
}
