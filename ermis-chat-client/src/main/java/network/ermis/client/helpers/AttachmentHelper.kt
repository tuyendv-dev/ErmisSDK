package network.ermis.client.helpers

import network.ermis.client.utils.TimeProvider
import network.ermis.core.models.Attachment
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

public class AttachmentHelper(private val timeProvider: TimeProvider = TimeProvider) {

    @Suppress("ReturnCount")
    public fun hasValidImageUrl(attachment: Attachment): Boolean {
        val url = attachment.imageUrl?.toHttpUrlOrNull() ?: return false
        if (url.queryParameterNames.contains(QUERY_KEY_NAME_EXPIRES).not()) {
            return true
        }
        val timestamp = url.queryParameter(QUERY_KEY_NAME_EXPIRES)?.toLongOrNull() ?: return false
        return timestamp > timeProvider.provideCurrentTimeInSeconds()
    }

    private companion object {
        private const val QUERY_KEY_NAME_EXPIRES = "Expires"
    }
}
