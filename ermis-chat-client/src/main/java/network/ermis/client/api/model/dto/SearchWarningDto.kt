package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchWarningDto(
    val channel_search_cids: List<String>,
    val channel_search_count: Int,
    val warning_code: Int,
    val warning_description: String,
)
