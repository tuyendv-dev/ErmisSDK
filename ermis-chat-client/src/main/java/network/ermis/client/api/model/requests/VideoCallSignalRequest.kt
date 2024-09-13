package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class VideoCallSignalRequest(
    val cid: String,
    val action: String,
    val signal: SignalCall? = null,
)
@JsonClass(generateAdapter = true)
internal data class SignalCall(
    val type: String,
    val sdp: String
)
