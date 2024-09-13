package network.ermis.offline.repository.database.converter

import androidx.room.TypeConverter
import com.squareup.moshi.adapter
import network.ermis.offline.repository.domain.message.internal.ModerationDetailsEntity

internal class ModerationDetailsConverter {

    @OptIn(ExperimentalStdlibApi::class)
    private val entityAdapter = moshi.adapter<ModerationDetailsEntity>()

    @TypeConverter
    fun stringToModerationDetails(data: String?): ModerationDetailsEntity? {
        return data?.let {
            entityAdapter.fromJson(it)
        }
    }

    @TypeConverter
    fun moderationDetailsToString(entity: ModerationDetailsEntity?): String? {
        return entity?.let {
            entityAdapter.toJson(it)
        }
    }
}
