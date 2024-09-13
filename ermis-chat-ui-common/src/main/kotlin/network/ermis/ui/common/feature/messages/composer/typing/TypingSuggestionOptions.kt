package network.ermis.ui.common.feature.messages.composer.typing

/**
 * The options to configure the [TypingSuggester].
 *
 * @param symbol The symbol that typing suggester will use to recognise a suggestion.
 * @param shouldTriggerOnlyAtStart Whether the suggester should only be recognising at the start of the input.
 * @param minimumRequiredCharacters The minimum required characters for the suggester to start recognising a suggestion.
 */
internal data class TypingSuggestionOptions(
    val symbol: String,
    val shouldTriggerOnlyAtStart: Boolean = false,
    val minimumRequiredCharacters: Int = 0,
)
