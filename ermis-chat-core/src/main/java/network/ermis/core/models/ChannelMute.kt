package network.ermis.core.models

import androidx.compose.runtime.Immutable
import java.util.Date

/**
 * Represents a channel mute.
 *
 * @param user The owner of the channel mute.
 * @param channel The muted channel.
 * @param createdAt Date/time of creation.
 * @param updatedAt Date/time of the last update.
 * @param expires Date/time of mute expiration.
 */
@Immutable
public data class ChannelMute(
    val user: User,
    val channel: Channel,
    val createdAt: Date,
    val updatedAt: Date,
    val expires: Date?,
)
