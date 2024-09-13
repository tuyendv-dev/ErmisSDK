package network.ermis.offline.repository.database.converter

import androidx.room.TypeConverter
import com.squareup.moshi.adapter
import network.ermis.offline.repository.domain.channel.member.MemberEntity

internal class MemberConverter {

    @OptIn(ExperimentalStdlibApi::class)
    private val memberEntityMapAdapter = moshi.adapter<MemberEntity>()

    @TypeConverter
    fun memberToString(member: MemberEntity?): String? {
        return memberEntityMapAdapter.toJson(member)
    }

    @TypeConverter
    fun stringToMemberMap(data: String?): MemberEntity? {
        if (data.isNullOrEmpty() || data == "null") {
            return null
        }
        return memberEntityMapAdapter.fromJson(data)
    }
}
