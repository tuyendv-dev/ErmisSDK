package network.ermis.offline.repository.database.converter

import androidx.room.TypeConverter
import com.squareup.moshi.adapter

internal class SetConverter {
    @OptIn(ExperimentalStdlibApi::class)
    private val adapter = moshi.adapter<Set<String>>()

    @TypeConverter
    fun stringToSortedSet(data: String?): Set<String> {
        if (data.isNullOrEmpty() || data == "null") {
            return setOf()
        }
        return adapter.fromJson(data) ?: emptySet()
    }

    /**
     * @return Nullable [String] to let KSP know this function cannot be used for "Nullable to NonNull" converting.
     *
     * An example of the incorrect behaviour:
     *     val stringifiedObject: String? = anotherConverter.objectToString(...)
     *     val stringList: Set<String> = SetConverter.stringToSortedSet(stringifiedObject)
     *     val nonNullStringifiedObject: String = SetConverter.sortedSetToString(stringifiedObject)
     *     // ... binding nonNullStringifiedObject to table's column
     *
     * The current behavior:
     *     val stringifiedObject: String? = anotherConverter.objectToString(...)
     *     // ... binding stringifiedObject to table's column
     */
    @TypeConverter
    fun sortedSetToString(someObjects: Set<String>?): String? {
        return adapter.toJson(someObjects)
    }
}
