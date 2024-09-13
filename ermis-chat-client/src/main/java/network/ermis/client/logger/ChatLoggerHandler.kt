package network.ermis.client.logger

public interface ChatLoggerHandler {
    public fun logT(throwable: Throwable)

    public fun logT(tag: Any, throwable: Throwable)

    public fun logI(tag: Any, message: String)

    public fun logD(tag: Any, message: String)

    public fun logV(tag: Any, message: String) { /* no-op */ }

    public fun logW(tag: Any, message: String)

    public fun logE(tag: Any, message: String)

    public fun logE(tag: Any, message: String, throwable: Throwable)
}
