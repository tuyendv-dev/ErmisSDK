package network.ermis.ui.common.feature.messages.composer.mention

import network.ermis.core.models.User
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Java compatibility interface for user lookup handler.
 */
public fun interface CompatUserLookupHandler {

    /**
     * Handles user lookup by given [query] in suspend way.
     * It's executed on background, so it can perform heavy operations.
     *
     * @param query String as user input for lookup algorithm.
     * @param callback The callback to be invoked when the user lookup is completed.
     * @return The cancel function to be invoked when the user lookup should be cancelled.
     */
    public fun handleCompatUserLookup(query: String, callback: (List<User>) -> Unit): () -> Unit
}

/**
 * Converts [CompatUserLookupHandler] to [UserLookupHandler].
 */
public fun CompatUserLookupHandler.toUserLookupHandler(): UserLookupHandler {
    return UserLookupHandler { query ->
        suspendCancellableCoroutine { cont ->
            val cancelable = handleCompatUserLookup(query) { users ->
                cont.resume(users)
            }
            cont.invokeOnCancellation {
                cancelable.invoke()
            }
        }
    }
}

public fun UserLookupHandler.toJavaCompatUserLookupHandler(): CompatUserLookupHandler {
    return CompatUserLookupHandler { query, callback ->
        runBlocking {
            val users = handleUserLookup(query)
            callback(users)
        }

        return@CompatUserLookupHandler {
        }
    }
}
