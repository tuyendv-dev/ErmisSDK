package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass
import network.ermis.core.internal.StreamHandsOff
import java.util.Date

/**
 * See [io.getstream.chat.android.client.parser2.adapters.UpstreamUserDtoAdapter] for
 * special [extraData] handling.
 */
@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class UpstreamUserDto(
    val banned: Boolean,
    val id: String,
    val name: String,
    val image: String,
    val invisible: Boolean,
    val language: String,
    val role: String,
    val devices: List<DeviceDto>,
    val teams: List<String>,

    val extraData: Map<String, Any>,
) : ExtraDataDto

/**
 * See [io.getstream.chat.android.client.parser2.adapters.DownstreamUserDtoAdapter] for
 * special [extraData] handling.
 */
@StreamHandsOff(
    reason = "Field names can't be changed because [CustomObjectDtoAdapter] class uses reflections to add/remove " +
        "content of [extraData] map",
)
@JsonClass(generateAdapter = true)
internal data class DownstreamUserDto(
    val id: String,
    val name: String?,
    val image: String?,
    val avatar: String?,
    val role: String = "",
    val invisible: Boolean = false,
    val language: String?,
    val banned: Boolean = false,
    val devices: List<DeviceDto>?,
    val online: Boolean = false,
    val created_at: Date?,
    val deactivated_at: Date?,
    val updated_at: Date?,
    val last_active: Date?,
    val total_unread_count: Int = 0,
    val unread_channels: Int = 0,
    val unread_count: Int = 0,
    val mutes: List<DownstreamMuteDto>?,
    val teams: List<String> = emptyList(),
    val channel_mutes: List<DownstreamChannelMuteDto>?,

    val extraData: Map<String, Any>,
) : ExtraDataDto

@JsonClass(generateAdapter = true)
internal data class PartialUpdateUserDto(
    val id: String,
    val set: Map<String, Any>,
    val unset: List<String>,
)
