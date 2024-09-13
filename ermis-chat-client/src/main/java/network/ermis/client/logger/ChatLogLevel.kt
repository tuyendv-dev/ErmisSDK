package network.ermis.client.logger

private const val ALL_SEVERITY = 0
private const val DEBUG_SEVERITY = 1
private const val WARN_SEVERITY = 2
private const val ERROR_SEVERITY = 3
private const val NOTHING_SEVERITY = 4

public enum class ChatLogLevel(private val severity: Int) {
    /**
     * Show all Logs.
     */
    ALL(ALL_SEVERITY),

    /**
     * Show DEBUG, WARNING, ERROR logs
     */
    DEBUG(DEBUG_SEVERITY),

    /**
     * Show WARNING and ERROR logs
     */
    WARN(WARN_SEVERITY),

    /**
     * Show ERRORs only
     */
    ERROR(ERROR_SEVERITY),

    /**
     * Don't show any Logs.
     */
    NOTHING(NOTHING_SEVERITY),
    ;

    internal fun isMoreOrEqualsThan(level: ChatLogLevel): Boolean {
        return level.severity >= severity
    }
}
