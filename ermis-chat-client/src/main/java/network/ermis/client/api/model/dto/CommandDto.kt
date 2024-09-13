package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class CommandDto(
    val name: String,
    val description: String,
    val args: String,
    val set: String,
)
