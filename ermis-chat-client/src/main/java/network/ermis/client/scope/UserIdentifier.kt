package network.ermis.client.scope

import network.ermis.core.models.User
import network.ermis.core.models.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * The [CoroutineContext.Element] which holds the current [UserId].
 */
internal class UserIdentifier : AbstractCoroutineContextElement(UserIdentifier) {

    private val _value = MutableStateFlow<UserId?>(null)

    /**
     * Key for [UserIdentifier] instance in the coroutine context.
     */
    companion object Key : CoroutineContext.Key<UserIdentifier> {
        private const val DEFAULT_TIMEOUT_IN_MS = 10_000L
    }

    /**
     * Represents [User.id] String
     */
    var value: UserId?
        get() = _value.value
        set(value) {
            _value.value = value
        }

    /**
     * Awaits for the specified [userId] being set.
     *
     * @param userId Required user_id to wait for.
     * @param timeoutInMs A timeout in milliseconds when the process will be cancelled.
     */
    suspend fun awaitFor(userId: UserId, timeoutInMs: Long = DEFAULT_TIMEOUT_IN_MS) = runCatching {
        withTimeout(timeoutInMs) {
            _value.first { it == userId }
        }
    }

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String = "UserIdentifier($value)"
}
