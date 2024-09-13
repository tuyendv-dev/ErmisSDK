package network.ermis.client.errors.cause

/**
 * Exceptions hierarchy for SDK internal usage only.
 */
public sealed class StreamSdkException : ErmisException {
    protected constructor() : super()
    protected constructor(message: String?) : super(message)
    protected constructor(message: String?, cause: Throwable?) : super(message, cause)
    protected constructor(cause: Throwable?) : super(cause)
}

/**
 * Identifies that message cannot be deleted, because it is in failed state due to the moderation violations.
 */
public class MessageModerationDeletedException(message: String?) : StreamSdkException(message)
