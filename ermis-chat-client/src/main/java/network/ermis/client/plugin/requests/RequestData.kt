package network.ermis.client.plugin.requests

import java.util.Date

internal data class RequestData(
    val name: String,
    val time: Date,
    val extraData: Map<String, String>,
)
