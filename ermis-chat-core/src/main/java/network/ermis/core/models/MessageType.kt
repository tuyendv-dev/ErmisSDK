package network.ermis.core.models

/**
 * Represents types of messages.
 */
public object MessageType {
    public const val REGULAR: String = "regular"
    public const val EPHEMERAL: String = "ephemeral"
    public const val ERROR: String = "error"
    public const val FAILED: String = "failed"
    public const val REPLY: String = "reply"
    public const val SYSTEM: String = "system"
}