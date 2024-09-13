
package network.ermis.ui.navigation

import network.ermis.ui.navigation.destinations.ChatDestination

@FunctionalInterface
public fun interface ChatNavigationHandler {
    /**
     * Attempt to navigate to the given [destination].
     *
     * @return True if navigation was successfully handled.
     */
    public fun navigate(destination: ChatDestination): Boolean
}
