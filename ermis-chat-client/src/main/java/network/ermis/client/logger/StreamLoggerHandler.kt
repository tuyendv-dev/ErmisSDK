package network.ermis.client.logger

import io.getstream.log.Priority
import io.getstream.log.Priority.ASSERT
import io.getstream.log.Priority.DEBUG
import io.getstream.log.Priority.ERROR
import io.getstream.log.Priority.INFO
import io.getstream.log.Priority.VERBOSE
import io.getstream.log.Priority.WARN
import io.getstream.log.StreamLogger

/**
 * A connection layer between [StreamLogger] and [ChatLoggerHandler].
 */
internal class StreamLoggerHandler(
    private val handler: ChatLoggerHandler?,
) : StreamLogger {

    /**
     * Passes log messages to the specified [handler].
     */
    override fun log(priority: Priority, tag: String, message: String, throwable: Throwable?) {
        handler?.run {
            when (priority) {
                VERBOSE -> logV(tag, message)
                DEBUG -> logD(tag, message)
                INFO -> logI(tag, message)
                WARN -> logW(tag, message)
                ERROR, ASSERT -> when (throwable) {
                    null -> logE(tag, message)
                    else -> logE(tag, message, throwable)
                }
            }
        }
    }
}
