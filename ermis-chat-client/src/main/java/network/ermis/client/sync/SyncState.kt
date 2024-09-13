package network.ermis.client.sync

import java.util.Date

public data class SyncState(
    val userId: String,
    val activeChannelIds: List<String> = emptyList(),
    val lastSyncedAt: Date? = null,
    val rawLastSyncedAt: String? = null,
    val markedAllReadAt: Date? = null,
)

public fun SyncState.stringify(): String {
    return "SyncState(userId='$userId', activeChannelIds.size=${activeChannelIds.size}, " +
        "lastSyncedAt=$lastSyncedAt, rawLastSyncedAt=$rawLastSyncedAt, markedAllReadAt=$markedAllReadAt)"
}
