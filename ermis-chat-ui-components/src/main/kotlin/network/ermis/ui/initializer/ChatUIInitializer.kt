
package network.ermis.ui.initializer

import android.content.Context
import androidx.startup.Initializer
import network.ermis.client.ErmisClient
import network.ermis.client.header.VersionPrefixHeader
import network.ermis.ui.ChatUI

/**
 * Jetpack Startup Initializer for Stream's Chat UI Components.
 */
public class ChatUIInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        ErmisClient.VERSION_PREFIX_HEADER = VersionPrefixHeader.UiComponents
        ChatUI.appContext = context
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}
