package network.ermis.client.token

import androidx.annotation.WorkerThread

/**
 * Provides a token used to authenticate the user with Stream Chat API.
 * The SDK doesn't refresh the token internally and will call
 * [loadToken] function once the previous one has expired.
 */
public interface TokenProvider {

    /**
     * Loads the token for the current user.
     * The token will be loaded only if the token was not loaded yet or existing one has expired.
     *
     * @return The valid JWT token.
     */
    @WorkerThread
    public fun loadToken(): String
}
