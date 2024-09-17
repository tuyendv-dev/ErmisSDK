package network.ermis.core.models

import androidx.compose.runtime.Immutable

@Immutable
public data class SearchMessagesResult(
    /**
     * Search results
     */
    val messages: List<Message> = emptyList(),

    /**
     * Value to pass to the next search query in order to paginate
     */
    val next: String? = null,

    /**
     * Value that points to the previous page. Pass as the next value in a search query
     * to paginate backwards.
     */
    val previous: String? = null,

    /**
     * Warning about the search results
     */
    val resultsWarning: SearchWarning? = null,
)
