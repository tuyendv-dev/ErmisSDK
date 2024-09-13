package network.ermis.core.models

import androidx.compose.runtime.Immutable

/**
 * Represents HMS room information that contains available room in a chat channel.
 *
 * @property roomId A new room id.
 * @property roomId A new room name.
 */
@Immutable
public data class HMSRoom(
    val roomId: String,
    val roomName: String,
)
