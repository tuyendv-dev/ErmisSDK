package network.ermis.core.models.querysort

import network.ermis.core.models.querysort.internal.SortSpecification

/**
 * Interface for the sorter of the SDK. Its implementations must provide a comparator to be used for
 * sorting collections of data in the SDK.
 */
public interface QuerySorter<T : Any> {

    /**
     * Sort specifications that compose this QuerySorter
     */
    public var sortSpecifications: List<SortSpecification<T>>

    /**
     * Comparator class.
     */
    public val comparator: Comparator<in T>

    public fun toDto(): List<Map<String, Any>>

    public companion object {
        public const val KEY_DIRECTION: String = "direction"
        public const val KEY_FIELD_NAME: String = "field"
        public const val MORE_ON_COMPARISON: Int = 1
        public const val EQUAL_ON_COMPARISON: Int = 0
        public const val LESS_ON_COMPARISON: Int = -1
    }
}
