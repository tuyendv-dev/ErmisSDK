package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ChannelInfoDto(
    val cid: String?,
    val id: String?,
    val member_count: Int = 0,
    val name: String?,
    val type: String?,
    val image: String?,
)
