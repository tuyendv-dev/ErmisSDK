package network.ermis.client.api.model.dto

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class UpstreamChannelUserRead(
    val user: UpstreamUserDto,
    val last_read: Date,
    val unread_messages: Int,
)

@JsonClass(generateAdapter = true)
internal data class DownstreamChannelUserRead(
    val user: DownstreamUserDto,
    val last_read: Date?,
    val unread_messages: Int,
    val last_read_message_id: String?,
)
