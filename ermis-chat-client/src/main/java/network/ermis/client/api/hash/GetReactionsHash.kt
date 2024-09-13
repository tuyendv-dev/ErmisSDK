package network.ermis.client.api.hash

internal data class GetReactionsHash(
    val messageId: String,
    val offset: Int,
    val limit: Int,
)
