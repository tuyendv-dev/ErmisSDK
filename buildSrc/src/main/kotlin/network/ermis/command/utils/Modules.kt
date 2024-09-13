package network.ermis.command.utils

fun <T> List<T>.generateGradleCommand(commandFunction: (T) -> String): String {
    val command = joinToString(separator = " ", transform = commandFunction)

    return "./gradlew $command"
}
