package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.model.dto.UpstreamMessageDto

/**
 * Represents the body part of the truncate channel request.
 *
 * @param message The system message that will be shown in the channel.
 */
@JsonClass(generateAdapter = true)
internal data class TruncateChannelRequest(
    val message: UpstreamMessageDto?,
)
