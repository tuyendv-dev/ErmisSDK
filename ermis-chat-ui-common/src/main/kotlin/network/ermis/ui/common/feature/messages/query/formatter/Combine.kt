package network.ermis.ui.common.feature.messages.query.formatter

/**
 * Combines multiple [QueryFormatter]s into a single [QueryFormatter].
 */
public class Combine(
    private val transformers: List<QueryFormatter>,
) : QueryFormatter {

    public constructor(vararg transformers: QueryFormatter) : this(transformers.toList())

    override fun format(query: String): String {
        return transformers.fold(query) { acc, transformer -> transformer.format(acc) }
    }
}
