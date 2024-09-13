package network.ermis.client.api.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class WalletConnectResponse(
    @Json(name = "challenge")
    val challenge: String = "",
)
