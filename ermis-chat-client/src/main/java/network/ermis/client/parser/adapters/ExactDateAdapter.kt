package network.ermis.client.parser.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import network.ermis.client.api.model.dto.ExactDate
import network.ermis.client.parser.adapters.internal.StreamDateFormatter

internal class ExactDateAdapter : JsonAdapter<ExactDate>() {

    private val streamDateFormatter = StreamDateFormatter("ExactDateAdapter")

    @ToJson
    override fun toJson(writer: JsonWriter, value: ExactDate?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value.rawDate)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    @FromJson
    override fun fromJson(reader: JsonReader): ExactDate? {
        val nextValue = reader.peek()
        if (nextValue == JsonReader.Token.NULL) {
            reader.skipValue()
            return null
        }

        val rawValue = reader.nextString()
        return streamDateFormatter.parse(rawValue)?.let { date ->
            ExactDate(date, rawValue)
        }
    }
}
