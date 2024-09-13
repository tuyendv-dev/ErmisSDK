package network.ermis.client.api.models

public data class QueryUsersRequest @JvmOverloads constructor(
    val name: String ="",
    val page: Int,
    val limit: Int,
)