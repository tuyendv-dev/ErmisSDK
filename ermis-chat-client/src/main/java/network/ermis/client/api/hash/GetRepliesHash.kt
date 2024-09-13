package network.ermis.client.api.hash

internal data class GetRepliesHash(
    val messageId: String,
    val firstId: String?,
    val limit: Int,
)
