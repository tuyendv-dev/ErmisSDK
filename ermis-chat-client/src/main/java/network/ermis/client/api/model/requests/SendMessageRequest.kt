package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.endpoint.MessageApi
import network.ermis.client.api.model.dto.UpstreamMessageDto

/**
 * Used to form a send message request.
 * @see [MessageApi.sendMessage]
 *
 * @param message The upstream version of the message.
 * @param skip_push If the message should skip triggering a push notification when sent. False by default.
 * @param skip_enrich_url If the message should skip enriching the URL. If URl is not enriched, it will not be
 * displayed as a link attachment. False by default.
 */
// REST documentation: https://getstream.io/chat/docs/rest/#messages-sendmessage
@JsonClass(generateAdapter = true)
internal data class SendMessageRequest(
    val message: UpstreamMessageDto,
    val skip_push: Boolean = false,
    val skip_enrich_url: Boolean = false,
)
