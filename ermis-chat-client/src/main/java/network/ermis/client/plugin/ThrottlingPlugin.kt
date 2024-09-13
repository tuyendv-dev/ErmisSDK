package network.ermis.client.plugin

import network.ermis.core.models.User
import io.getstream.log.StreamLog
import io.getstream.result.Error
import io.getstream.result.Result
import kotlin.reflect.KClass

internal class ThrottlingPlugin : Plugin {
    override val errorHandler = null
    private val lastMarkReadMap: MutableMap<String, Long> = mutableMapOf()

    override suspend fun onChannelMarkReadPrecondition(channelType: String, channelId: String): Result<Unit> {
        val now = System.currentTimeMillis()
        val deltaLastMarkReadAt = now - (lastMarkReadMap[channelId] ?: 0)
        return when {
            deltaLastMarkReadAt > MARK_READ_THROTTLE_MS -> Result.Success(Unit)
                .also { lastMarkReadMap[channelId] = now }
            else -> Result.Failure(Error.GenericError("Mark read throttled")).also {
                StreamLog.w("ThrottlingPlugin") { "[onChannelMarkReadPrecondition] read is ignored ($channelId)" }
            }
        }
    }

    override fun <T : Any> resolveDependency(klass: KClass<T>): T? = null
    override fun onUserSet(user: User) { /* No-op */ }
    override fun onUserDisconnected() {
        lastMarkReadMap.clear()
    }

    companion object {
        const val MARK_READ_THROTTLE_MS = 3000L
    }
}
