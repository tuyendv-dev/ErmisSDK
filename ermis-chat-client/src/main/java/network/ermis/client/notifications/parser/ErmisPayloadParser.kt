package network.ermis.client.notifications.parser

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.getstream.log.StreamLog

/**
 * Helper class for parsing the payload of a push message.
 */
public object ErmisPayloadParser {

    private const val TAG = "ErmisPayloadParser"

    private val mapAdapter: JsonAdapter<MutableMap<String, Any?>> by lazy {
        Moshi.Builder()
            .build()
            .adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
    }

    /**
     * Parses the [json] string into a [Map] of [String] to [Any].
     */
    public fun parse(json: String?): Map<String, Any?> {
        return try {
            json?.takeIf { it.isNotBlank() }?.let { mapAdapter.fromJson(it) } ?: emptyMap()
        } catch (e: Throwable) {
            StreamLog.e(TAG, e) { "[parse] failed: $json" }
            emptyMap()
        }
    }
}
