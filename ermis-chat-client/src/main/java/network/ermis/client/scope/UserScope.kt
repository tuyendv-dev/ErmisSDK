package network.ermis.client.scope

import network.ermis.core.models.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.plus

/**
 * A user aware implementation of [CoroutineScope].
 */
internal interface UserScope : CoroutineScope {

    /**
     * Returns [UserIdentifier] context element.
     */
    val userId: UserIdentifier

    /**
     * Cancels all children of the [UserJob] in this scope's context connected to the specified [userId].
     */
    fun cancelChildren(userId: UserId? = null)
}

/**
 * Creates a user aware [CoroutineScope].
 */
internal fun UserScope(clientScope: ClientScope): UserScope = UserScopeImpl(clientScope)

/**
 * Inherits [ClientScope] and adds elements such as [UserIdentifier] and [UserJob].
 */
private class UserScopeImpl(
    clientScope: ClientScope,
    userIdentifier: UserIdentifier = UserIdentifier(),
) : UserScope,
    CoroutineScope by (
        clientScope + userIdentifier + UserJob(clientScope.coroutineContext.job) { userIdentifier.value }
        ) {

    /**
     * Returns [UserIdentifier] context element.
     */
    override val userId: UserIdentifier
        get() = coroutineContext[UserIdentifier] ?: error("no UserIdentifier found")

    /**
     * Cancels all children of the [UserJob] in this scope's context connected to the specified [userId].
     */
    override fun cancelChildren(userId: UserId?) {
        (coroutineContext[Job] as UserJob).cancelChildren(userId)
    }
}
