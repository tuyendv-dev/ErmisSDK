package network.ermis.core.extensions

private val snakeRegex = "_[a-zA-Z]".toRegex()
private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

/**
 * Converts string written in snake case to String in camel case with the first symbol in lower case.
 * For example string "created_at_some_time" is converted to "createdAtSomeTime".
 */
internal fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) { matchResult ->
        matchResult.value.replace("_", "").uppercase()
    }
}

internal fun String.lowerCamelCaseToGetter(): String = "get${this[0].uppercase()}${this.substring(1)}"

/**
 * Converts String written in camel case to String in snake case.
 * For example string "createdAtSomeTime" is converted to "created_at_some_time".
 */
internal fun String.camelCaseToSnakeCase(): String {
    return camelRegex.replace(this) { "_${it.value}" }.lowercase()
}
