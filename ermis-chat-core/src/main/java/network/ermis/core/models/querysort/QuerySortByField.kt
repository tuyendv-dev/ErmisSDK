package network.ermis.core.models.querysort

import network.ermis.core.models.querysort.internal.SortAttribute
import network.ermis.core.models.querysort.internal.SortSpecification
import network.ermis.core.models.querysort.internal.compare

/**
 * Implementation of QuerySorter for fields that implements `ComparableFieldProvider`. This QuerySorter doesn't
 * use reflection and it's more performant than `QuerySortByReflection`.
 */
public class QuerySortByField<T : ComparableFieldProvider> : BaseQuerySort<T>() {

    /**
     * Comparator from [SortAttribute.FieldSortAttribute]
     */
    override fun comparatorFromFieldSort(
        firstSort: SortAttribute.FieldSortAttribute<T>,
        sortDirection: SortDirection,
    ): Comparator<T> {
        throw IllegalArgumentException("FieldSortAttribute can't be used with QuerySortByField")
    }

    /**
     * Comparator from [SortAttribute.FieldNameSortAttribute]
     */
    override fun comparatorFromNameAttribute(
        name: SortAttribute.FieldNameSortAttribute<T>,
        sortDirection: SortDirection,
    ): Comparator<T> =
        name.name.comparator(sortDirection)

    private fun String.comparator(sortDirection: SortDirection): Comparator<T> =
        Comparator { o1, o2 ->
            val fieldName = this
            val fieldOne = o1.getComparableField(fieldName) as? Comparable<Any>
            val fieldTwo = o2.getComparableField(fieldName) as? Comparable<Any>

            compare(fieldOne, fieldTwo, sortDirection)
        }

    private fun add(sortSpecification: SortSpecification<T>): QuerySortByField<T> {
        sortSpecifications = sortSpecifications + sortSpecification
        return this
    }

    public fun asc(fieldName: String): QuerySortByField<T> {
        return add(SortSpecification(SortAttribute.FieldNameSortAttribute(fieldName), SortDirection.ASC))
    }

    public fun desc(fieldName: String): QuerySortByField<T> {
        return add(SortSpecification(SortAttribute.FieldNameSortAttribute(fieldName), SortDirection.DESC))
    }

    public companion object {
        /**
         * Adds a field to [QuerySortByField] using the name of field in the direction ASC.
         *
         * @param fieldName Field name.
         */
        @JvmStatic
        public fun <R : ComparableFieldProvider> ascByName(
            fieldName: String,
        ): QuerySortByField<R> = QuerySortByField<R>().asc(fieldName)

        /**
         * Adds a field to [QuerySortByField] using the name of field in the direction DESC.
         *
         * @param fieldName Field name.
         */
        @JvmStatic
        public fun <R : ComparableFieldProvider> descByName(
            fieldName: String,
        ): QuerySortByField<R> = QuerySortByField<R>().desc(fieldName)

        /**
         * Creates a [QuerySortByField] using the name of field in the direction ASC.
         *
         * @param fieldName Field name.
         */
        @JvmStatic
        public fun <R : ComparableFieldProvider> QuerySortByField<R>.ascByName(
            fieldName: String,
        ): QuerySortByField<R> = asc(fieldName)

        /**
         * Creates a [QuerySortByField] using the name of field in the direction DESC.
         *
         * @param fieldName Field name.
         */
        @JvmStatic
        public fun <R : ComparableFieldProvider> QuerySortByField<R>.descByName(
            fieldName: String,
        ): QuerySortByField<R> = desc(fieldName)
    }
}
