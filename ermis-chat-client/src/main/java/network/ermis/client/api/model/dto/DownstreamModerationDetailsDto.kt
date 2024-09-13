package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass
import network.ermis.core.internal.StreamHandsOff

@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class DownstreamModerationDetailsDto(
    val original_text: String?,
    val action: String?,
    val error_msg: String? = null,
    val extraData: Map<String, Any>,
)
