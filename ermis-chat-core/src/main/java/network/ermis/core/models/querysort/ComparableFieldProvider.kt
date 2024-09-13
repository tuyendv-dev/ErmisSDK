package network.ermis.core.models.querysort

/**
 * Implement this interface to use [QuerySortByField]. Implementations of this interface must provide comparable
 * fields.
 */
public interface ComparableFieldProvider {

    /**
     * Gets a comparable fields from a name.
     *
     * @param fieldName The name of the field.
     */
    public fun getComparableField(fieldName: String): Comparable<*>?
}
