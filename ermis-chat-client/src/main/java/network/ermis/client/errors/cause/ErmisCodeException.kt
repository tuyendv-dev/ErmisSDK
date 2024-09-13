package network.ermis.client.errors.cause

import network.ermis.core.errors.ChatErrorCode

/**
 * Exceptions hierarchy based on backend error codes.
 *
 * @see [ChatErrorCode] for the list of error codes.
 */
public sealed class ErmisCodeException : ErmisException {
    protected constructor() : super()
    protected constructor(message: String?) : super(message)
    protected constructor(message: String?, cause: Throwable?) : super(message, cause)
    protected constructor(cause: Throwable?) : super(cause)
}
