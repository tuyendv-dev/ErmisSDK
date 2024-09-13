package network.ermis.client.api.model.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class WalletSigninRequest(
    @Json(name = "signature") val signature: String,
    @Json(name = "address") val address: String,
    @Json(name = "nonce") val nonce: String,
    @Json(name = "api_key") val apiKey: String,
)
