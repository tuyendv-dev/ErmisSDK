@file:OptIn(InternalCoroutinesApi::class)
@file:Suppress("DEPRECATION_ERROR")

package network.ermis.client.scope

import network.ermis.core.models.UserId
import io.getstream.log.taggedLogger
import kotlinx.coroutines.ChildHandle
import kotlinx.coroutines.ChildJob
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * A user aware implementation of [Job].
 */
internal interface UserJob : CompletableJob {
    /**
     * Cancels all children of the [UserJob] in this context connected to the specified [userId].
     */
    fun cancelChildren(userId: UserId? = null)
}

/**
 * Creates a user aware job object in an active state.
 */
internal fun UserJob(parent: Job? = null, getUserId: () -> UserId?): UserJob = UserJobImpl(
    SupervisorJob(parent),
    getUserId,
)

/**
 * A user aware implementation of [Job]. It is optionally a child to a parent job.
 */
private class UserJobImpl(
    private val delegate: CompletableJob,
    private val getUserId: () -> UserId?,
) : CompletableJob by delegate, UserJob {

    private val logger by taggedLogger("Chat:UserJob")

    /**
     * Wraps child job with [UserChildJob] and attaches it so that [UserJob] job becomes its parent.
     *
     * WARNING! This function is marked as [InternalCoroutinesApi].
     */
    override fun attachChild(child: ChildJob): ChildHandle {
        val userId = getUserId()
        return delegate.attachChild(UserChildJob(userId, child))
    }

    /**
     * Cancels all children of the [UserJob] in this context connected to the specified [userId].
     */
    override fun cancelChildren(userId: UserId?) {
        logger.d { "[cancelChildren] userId: '$userId'" }
        for (child in children) {
            if (child is UserChildJob && child.userId != userId && userId != null) {
                logger.v { "[cancelChildren] skip child: $child)" }
                continue
            }
            logger.v { "[cancelChildren] cancel child: $child)" }
            child.cancel()
        }
    }

    /**
     * Returns the element with the given [key] from this context or `null`.
     *
     * Intentionally overridden to use [super] and not a [delegate] one.
     */
    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R {
        return super<UserJob>.fold(initial, operation)
    }

    /**
     * Returns the element with the given [key] from this context or `null`.
     *
     * Intentionally overridden to use [super] and not a [delegate] one.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? {
        return super<UserJob>.get(key)
    }

    /**
     * Returns a context containing elements from this context, but without an element with
     * the specified [key].
     *
     * Intentionally overridden to use [super] and not a [delegate] one.
     */
    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext {
        return super<UserJob>.minusKey(key)
    }

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String = "UserJob(userId=${getUserId()})"
}

/**
 * Combines [ChildJob] with specified [userId].
 */
private class UserChildJob(
    val userId: UserId?,
    private val delegate: ChildJob,
) : ChildJob by delegate {
    override fun toString(): String = "UserChildJob(userId='$userId')"
}
