package network.ermis.offline.repository.database.converter

import network.ermis.core.models.querysort.ComparableFieldProvider
import network.ermis.core.models.querysort.QuerySortByField
import network.ermis.core.models.querysort.QuerySorter
import network.ermis.core.models.querysort.SortDirection
import network.ermis.core.models.querysort.internal.SortSpecification

/**
 * A parser to create [QuerySorter] from different ways.
 */
public class QuerySortParser<T : ComparableFieldProvider> {

    /**
     * Creates a query sort from a list of information.
     */
    internal fun fromRawInfo(
        specs: List<Map<String, Any>>,
    ): QuerySorter<T> {
        return specs.fold(QuerySortByField()) { sort, sortSpecMap ->
            val fieldName = sortSpecMap[QuerySorter.KEY_FIELD_NAME] as? String
                ?: error("Cannot parse sortSpec to query sort\n$sortSpecMap")
            val direction = (sortSpecMap[QuerySorter.KEY_DIRECTION] as? Number)?.toInt()
                ?: error("Cannot parse sortSpec to query sort\n$sortSpecMap")

            createQuerySort(sort, fieldName, direction)
                ?: error("Cannot parse sortSpec to query sort: $sortSpecMap")
        }
    }

    /**
     * Creates the query sort for a list of sort specifications.
     */
    public fun fromSpecifications(specs: List<SortSpecification<T>>): QuerySorter<T> {
        return specs.fold(QuerySortByField()) { querySort, sortSpec ->
            val fieldName = sortSpec.sortAttribute.name
            val direction = sortSpec.sortDirection.value

            createQuerySort(querySort, fieldName, direction)
                ?: error("Cannot parse sortSpec to query sort: $sortSpec")
        }
    }

    private fun createQuerySort(
        querySort: QuerySortByField<T>,
        fieldName: String,
        direction: Int,
    ): QuerySortByField<T>? {
        return when (direction) {
            SortDirection.ASC.value -> querySort.asc(fieldName)
            SortDirection.DESC.value -> querySort.desc(fieldName)
            else -> null
        }
    }
}
