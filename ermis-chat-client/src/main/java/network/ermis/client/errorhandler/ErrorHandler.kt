package network.ermis.client.errorhandler

public interface ErrorHandler :
    DeleteReactionErrorHandler,
    CreateChannelErrorHandler,
    QueryMembersErrorHandler,
    SendReactionErrorHandler,
    Comparable<ErrorHandler> {

    /**
     * The priority of this [ErrorHandler]. Use it to run it before error handlers of the same type.
     */
    public val priority: Int

    override fun compareTo(other: ErrorHandler): Int {
        return this.priority.compareTo(other.priority)
    }

    public companion object {

        /**
         * Default priority
         */
        public const val DEFAULT_PRIORITY: Int = 1
    }
}
