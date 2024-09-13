package network.ermis.state.extensions

import network.ermis.core.models.Attachment

/**
 * Uses substrings to extract the file name from the attachment URL.
 *
 * Useful in situations where no attachment name or title has been provided and
 * we need a name in order to download the file to storage.
 */
internal fun Attachment.parseAttachmentNameFromUrl(): String? {
    val url = when (this.type) {
        "image" -> this.imageUrl ?: this.assetUrl ?: this.thumbUrl
        else -> this.assetUrl ?: this.imageUrl ?: this.thumbUrl
    }

    return url?.substringAfterLast(
        delimiter = "/",
        missingDelimiterValue = "",
    )?.takeIf { it.isNotBlank() }
        ?.substringBefore("?")
}
