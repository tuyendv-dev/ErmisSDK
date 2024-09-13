package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass
import network.ermis.core.internal.StreamHandsOff
import java.util.Date

/**
 * See [io.getstream.chat.android.client.parser2.adapters.UpstreamReactionDtoAdapter] for
 * special [extraData] handling.
 */
@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class UpstreamReactionDto(
    val created_at: Date?,
    val message_id: String,
    val score: Int?,
    val type: String,
    val updated_at: Date?,
    val user: UpstreamUserDto?,
    val user_id: String,

    val extraData: Map<String, Any>,
) : ExtraDataDto

/**
 * See [io.getstream.chat.android.client.parser2.adapters.DownstreamReactionDtoAdapter] for
 * special [extraData] handling.
 */
@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class DownstreamReactionDto(
    val created_at: Date?,
    val message_id: String,
    val score: Int?,
    val type: String,
    val updated_at: Date?,
    val user: DownstreamUserDto?,
    val user_id: String,

    val extraData: Map<String, Any>,
) : ExtraDataDto
