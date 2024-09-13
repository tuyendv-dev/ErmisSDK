package network.ermis.core.models.querysort

/** Sort order which can be ascending or descending. */
public enum class SortDirection(public val value: Int) {
    /** Descending sort order. */
    DESC(-1),

    /** Ascending sort order. */
    ASC(1),
    ;

    public companion object {
        /**
         * Returns the [SortDirection] from the number that represents the direction.
         *
         * @param value Int the number of the direction.
         */
        public fun fromNumber(value: Int): SortDirection =
            when (value) {
                1 -> ASC
                -1 -> DESC
                else -> throw IllegalArgumentException("Unsupported sort direction")
            }
    }
}
