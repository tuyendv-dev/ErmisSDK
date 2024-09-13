
package network.ermis.ui.navigation

import network.ermis.ui.navigation.destinations.ChatDestination

public class ChatNavigator(private val handler: ChatNavigationHandler = EMPTY_HANDLER) {
    public fun navigate(destination: ChatDestination) {
        val handled = handler.navigate(destination)
        if (!handled) {
            performDefaultNavigation(destination)
        }
    }

    private fun performDefaultNavigation(destination: ChatDestination) {
        destination.navigate()
    }

    public companion object {
        @JvmField
        public val EMPTY_HANDLER: ChatNavigationHandler = ChatNavigationHandler { false }
    }
}
