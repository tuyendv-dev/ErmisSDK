package network.ermis.client.api.model.requests

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UpdateCooldownRequest(
    val set: Map<String, Any>,
) {
    companion object {
        fun create(cooldownTimeInSeconds: Int): UpdateCooldownRequest {
            return UpdateCooldownRequest(mapOf("cooldown" to cooldownTimeInSeconds))
        }
    }
}
