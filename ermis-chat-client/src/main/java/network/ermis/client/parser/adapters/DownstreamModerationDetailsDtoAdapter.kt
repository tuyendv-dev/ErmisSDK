package network.ermis.client.parser.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import network.ermis.client.api.model.dto.DownstreamModerationDetailsDto

internal object DownstreamModerationDetailsDtoAdapter :
    CustomObjectDtoAdapter<DownstreamModerationDetailsDto>(DownstreamModerationDetailsDto::class) {

    @FromJson
    fun fromJson(
        jsonReader: JsonReader,
        mapAdapter: JsonAdapter<MutableMap<String, Any>>,
        messageAdapter: JsonAdapter<DownstreamModerationDetailsDto>,
    ): DownstreamModerationDetailsDto? = parseWithExtraData(jsonReader, mapAdapter, messageAdapter)

    @ToJson
    @Suppress("UNUSED_PARAMETER")
    fun toJson(
        jsonWriter: JsonWriter,
        value: DownstreamModerationDetailsDto,
    ): Unit = error("Can't convert this to Json")
}
