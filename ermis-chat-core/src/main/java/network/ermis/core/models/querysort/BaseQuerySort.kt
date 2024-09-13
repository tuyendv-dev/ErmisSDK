package network.ermis.core.models.querysort

import network.ermis.core.models.querysort.internal.CompositeComparator
import network.ermis.core.models.querysort.internal.SortAttribute
import network.ermis.core.models.querysort.internal.SortSpecification

/**
 * Base class for implementing [QuerySorter]. This class holds common code for [QuerySortByField] and
 * [QuerySortByReflection].
 */
public abstract class BaseQuerySort<T : Any> : QuerySorter<T> {

    override var sortSpecifications: List<SortSpecification<T>> = emptyList()

    /**
     * Comparator class that will be generator by the sort specifications.
     */
    override val comparator: Comparator<in T>
        get() = CompositeComparator(sortSpecifications.map { it.comparator })

    private val SortSpecification<T>.comparator: Comparator<T>
        get() {
            return when (this.sortAttribute) {
                is SortAttribute.FieldSortAttribute<T> -> comparatorFromFieldSort(this.sortAttribute, sortDirection)

                is SortAttribute.FieldNameSortAttribute<T> ->
                    comparatorFromNameAttribute(this.sortAttribute, sortDirection)
            }
        }

    /**
     * Comparator from [SortAttribute.FieldSortAttribute]
     */
    public abstract fun comparatorFromFieldSort(
        firstSort: SortAttribute.FieldSortAttribute<T>,
        sortDirection: SortDirection,
    ): Comparator<T>

    /**
     * Comparator from [SortAttribute.FieldNameSortAttribute]
     */
    public abstract fun comparatorFromNameAttribute(
        name: SortAttribute.FieldNameSortAttribute<T>,
        sortDirection: SortDirection,
    ): Comparator<T>

    public override fun toDto(): List<Map<String, Any>> = sortSpecifications.map { sortSpec ->
        listOf(
            QuerySorter.KEY_FIELD_NAME to sortSpec.sortAttribute.name,
            QuerySorter.KEY_DIRECTION to sortSpec.sortDirection.value,
        ).toMap()
    }

    override fun hashCode(): Int = sortSpecifications.hashCode()

    override fun toString(): String = sortSpecifications.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseQuerySort<*>

        if (sortSpecifications != other.sortSpecifications) return false

        return true
    }
}
