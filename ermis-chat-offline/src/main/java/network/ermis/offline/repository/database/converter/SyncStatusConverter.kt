package network.ermis.offline.repository.database.converter

import androidx.room.TypeConverter
import network.ermis.core.models.SyncStatus

internal class SyncStatusConverter {
    @TypeConverter
    fun stringToSyncStatus(data: Int): SyncStatus {
        return SyncStatus.fromInt(data)!!
    }

    @TypeConverter
    fun syncStatusToString(syncStatus: SyncStatus): Int {
        return syncStatus.status
    }
}
