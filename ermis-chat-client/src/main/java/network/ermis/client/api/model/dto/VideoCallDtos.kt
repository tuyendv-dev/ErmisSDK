package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SignalWebrtcDto(
    val type: String,
    val sdp: String,
)
