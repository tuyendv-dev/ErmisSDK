package network.ermis.ui.utils.extension

/**
 * Checks if a message contains a link using a
 * regular expression.
 */
public fun String.containsLinks(): Boolean {
    val regex =
        """(?:\s|^)((?:https?:)?(?:[a-z|A-Z0-9-]+(?:\.[a-z|A-Z0-9-]+)+)(?::[0-9]+)?(?:(?:[^\s]+)?)?)""".toRegex()
    return this.contains(regex = regex)
}

/**
 * Small extension function designed to add a
 * scheme to URLs that do not have one so that
 * they can be opened using [android.content.Intent.ACTION_VIEW]
 */
public fun String.addSchemeToUrlIfNeeded(): String = when {
    this.startsWith("mailto:") -> this
    this.startsWith("http://") -> this
    this.startsWith("https://") -> this
    else -> "http://$this"
}
