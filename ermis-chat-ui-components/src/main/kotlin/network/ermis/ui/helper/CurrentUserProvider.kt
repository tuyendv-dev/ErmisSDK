
package network.ermis.ui.helper

import network.ermis.client.ErmisClient
import network.ermis.core.models.User
import network.ermis.state.plugin.state.global.GlobalState

/**
 * Provides the currently logged in user.
 */
public fun interface CurrentUserProvider {

    /**
     * Returns the currently logged in user.
     *
     *  @return The currently logged in user.
     */
    public fun getCurrentUser(): User?

    public companion object {
        /**
         * Builds the default current user provider.
         */
        public fun defaultCurrentUserProvider(): CurrentUserProvider {
            return DefaultCurrentUserProvider()
        }
    }
}

/**
 * The default implementation of [CurrentUserProvider] that returns a user
 * from [GlobalState] object.
 */
private class DefaultCurrentUserProvider : CurrentUserProvider {

    /**
     * Returns the currently logged in user.
     *
     *  @return The currently logged in user.
     */
    override fun getCurrentUser(): User? {
        return ErmisClient.instance().getCurrentUser()
    }
}
