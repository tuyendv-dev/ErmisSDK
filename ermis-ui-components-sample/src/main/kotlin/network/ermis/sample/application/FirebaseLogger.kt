
package network.ermis.sample.application

import com.google.firebase.crashlytics.FirebaseCrashlytics
import network.ermis.client.logger.ChatLoggerHandler
import io.getstream.log.taggedLogger

object FirebaseLogger : ChatLoggerHandler {
    private const val INTERNAL_LOG_PREFIX = "Chat"
    private val logger by taggedLogger("Chat:FirebaseLogger")
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    var userId: String? = null
        set(value) {
            field = value
            value?.let(crashlytics::setUserId)
        }

    private fun log(tag: Any? = null, message: String? = null, error: Throwable? = null) {
        if (tag is String && tag.startsWith(INTERNAL_LOG_PREFIX)) {
            return
        }

        if (tag == null && message == null && error == null) {
            logger.d { "No data provided; skipping Crashlytics logging" }
            return
        }

        val logTag = tag ?: ""
        val logMsg = message ?: ""

        crashlytics.log("[$logTag] $logMsg")
        error?.let(crashlytics::recordException)
    }

    override fun logD(tag: Any, message: String) {
        log(tag, message)
    }

    override fun logE(tag: Any, message: String) {
        log(tag, message)
    }

    override fun logE(tag: Any, message: String, throwable: Throwable) {
        log(tag, message, throwable)
    }

    override fun logI(tag: Any, message: String) {
        log(tag, message)
    }

    override fun logT(tag: Any, throwable: Throwable) {
        log(tag, error = throwable)
    }

    override fun logT(throwable: Throwable) {
        log(error = throwable)
    }

    override fun logW(tag: Any, message: String) {
        log(tag, message)
    }
}
