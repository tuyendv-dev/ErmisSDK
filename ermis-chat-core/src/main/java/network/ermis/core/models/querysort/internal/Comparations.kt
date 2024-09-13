package network.ermis.core.models.querysort.internal

import network.ermis.core.models.querysort.QuerySorter.Companion.EQUAL_ON_COMPARISON
import network.ermis.core.models.querysort.QuerySorter.Companion.LESS_ON_COMPARISON
import network.ermis.core.models.querysort.QuerySorter.Companion.MORE_ON_COMPARISON
import network.ermis.core.models.querysort.SortDirection

internal fun compare(
    first: Comparable<Any>?,
    second: Comparable<Any>?,
    sortDirection: SortDirection,
): Int {
    return when {
        first == null && second == null -> EQUAL_ON_COMPARISON
        first == null && second != null -> LESS_ON_COMPARISON * sortDirection.value
        first != null && second == null -> MORE_ON_COMPARISON * sortDirection.value
        first != null && second != null -> first.compareTo(second) * sortDirection.value
        else -> error("Impossible case!")
    }
}
