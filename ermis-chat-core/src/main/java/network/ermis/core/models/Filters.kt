package network.ermis.core.models

@Suppress("TooManyFunctions")
public object Filters {

    @JvmStatic
    public fun neutral(): FilterObject = NeutralFilterObject

    @JvmStatic
    public fun exists(fieldName: String): FilterObject = ExistsFilterObject(fieldName)

    @JvmStatic
    public fun notExists(fieldName: String): FilterObject = NotExistsFilterObject(fieldName)

    @JvmStatic
    public fun contains(fieldName: String, value: Any): FilterObject = ContainsFilterObject(fieldName, value)

    @JvmStatic
    public fun and(vararg filters: FilterObject): FilterObject = AndFilterObject(filters.toSet())

    @JvmStatic
    public fun or(vararg filters: FilterObject): FilterObject = OrFilterObject(filters.toSet())

    @JvmStatic
    public fun nor(vararg filters: FilterObject): FilterObject = NorFilterObject(filters.toSet())

    @JvmStatic
    public fun eq(fieldName: String, value: Any): FilterObject = EqualsFilterObject(fieldName, value)

    @JvmStatic
    public fun ne(fieldName: String, value: Any): FilterObject = NotEqualsFilterObject(fieldName, value)

    @JvmStatic
    public fun greaterThan(fieldName: String, value: Any): FilterObject = GreaterThanFilterObject(fieldName, value)

    @JvmStatic
    public fun greaterThanEquals(fieldName: String, value: Any): FilterObject =
        GreaterThanOrEqualsFilterObject(fieldName, value)

    @JvmStatic
    public fun lessThan(fieldName: String, value: Any): FilterObject = LessThanFilterObject(fieldName, value)

    @JvmStatic
    public fun lessThanEquals(fieldName: String, value: Any): FilterObject =
        LessThanOrEqualsFilterObject(fieldName, value)

    @JvmStatic
    public fun `in`(fieldName: String, vararg values: String): FilterObject = InFilterObject(fieldName, values.toSet())

    @JvmStatic
    public fun `in`(fieldName: String, values: List<Any>): FilterObject = InFilterObject(fieldName, values.toSet())

    @JvmStatic
    public fun `in`(fieldName: String, vararg values: Number): FilterObject = InFilterObject(fieldName, values.toSet())

    @JvmStatic
    public fun nin(fieldName: String, vararg values: String): FilterObject =
        NotInFilterObject(fieldName, values.toSet())

    @JvmStatic
    public fun nin(fieldName: String, values: List<Any>): FilterObject = NotInFilterObject(fieldName, values.toSet())

    @JvmStatic
    public fun nin(fieldName: String, vararg values: Number): FilterObject =
        NotInFilterObject(fieldName, values.toSet())

    @JvmStatic
    public fun autocomplete(fieldName: String, value: String): FilterObject = AutocompleteFilterObject(fieldName, value)

    @JvmStatic
    public fun distinct(memberIds: List<String>): FilterObject = DistinctFilterObject(memberIds.toSet())

    @JvmStatic
    public fun beloChannels(type: String?, memberIds: List<String>, roles: List<String> = listOf("member", "owner", "moder"), projectId: String? = null): FilterObject = BeloFilterChannelObject(type, memberIds, roles, projectId)

    @JvmStatic
    public fun joindChannels(memberId: String, projectId: String? = null): FilterObject = BeloFilterChannelObject(null, listOf(memberId), listOf("member", "owner", "moder"), projectId)

    @JvmStatic
    public fun invitedChannels(memberId: String, projectId: String? = null): FilterObject = BeloFilterChannelObject(null, listOf(memberId), listOf("pending"), projectId)
}
