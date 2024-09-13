package network.ermis.client.socket

public data class ErrorDetail(
    public val code: Int,
    public val messages: List<String>,
)
