package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass
import network.ermis.client.api.endpoint.MessageApi

/**
 * Used to form a partial message update request.
 * @see [MessageApi.partialUpdateMessage]
 *
 * @param set Sets new field values.
 * @param unset Array of field names to unset.
 * @param skip_enrich_url If the message should skip enriching the URL. If URl is not enriched, it will not be
 * displayed as a link attachment. False by default.
 */
@JsonClass(generateAdapter = true)
internal data class PartialUpdateMessageRequest(
    val set: Map<String, Any>,
    val unset: List<String>,
    val skip_enrich_url: Boolean = false,
)
