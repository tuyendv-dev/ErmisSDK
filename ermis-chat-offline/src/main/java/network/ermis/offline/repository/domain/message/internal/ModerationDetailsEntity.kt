package network.ermis.offline.repository.domain.message.internal

import com.squareup.moshi.JsonClass

/**
 * Moderation details embedded within a message.
 */
@JsonClass(generateAdapter = true)
internal data class ModerationDetailsEntity(
    val originalText: String,
    val action: String,
    val errorMsg: String,
)
