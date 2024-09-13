package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class SearchMessage(
    @Json(name = "cid") val cid: String,
    @Json(name = "search_term") val search_term: String,
    @Json(name = "limit") val limit: Int,
    @Json(name = "offset") val offset: Int,
)
