package network.ermis.client.plugin.listeners

import network.ermis.core.models.User
import io.getstream.result.Result

/**
 * Listener used when fetching the current user from the backend.
 */
public interface FetchCurrentUserListener {

    /**
     * Called when the current user is fetched from the backend.
     */
    public suspend fun onFetchCurrentUserResult(result: Result<User>)
}
