
package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UpdateChannelPartialRequest(
    val set: Map<String, Any>,
    val unset: List<String>,
)
