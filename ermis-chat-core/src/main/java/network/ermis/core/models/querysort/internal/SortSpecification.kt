package network.ermis.core.models.querysort.internal

import network.ermis.core.models.querysort.SortDirection

public data class SortSpecification<T>(
    val sortAttribute: SortAttribute<T>,
    val sortDirection: SortDirection,
)
