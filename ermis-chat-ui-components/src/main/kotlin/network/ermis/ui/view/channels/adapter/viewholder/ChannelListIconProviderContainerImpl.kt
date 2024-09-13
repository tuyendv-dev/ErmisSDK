
package network.ermis.ui.view.channels.adapter.viewholder

import network.ermis.ui.view.ChannelListView.ChannelOptionIconProvider
import network.ermis.ui.utils.ListenerDelegate

internal class ChannelListIconProviderContainerImpl(
    getMoreOptionsIcon: ChannelOptionIconProvider = ChannelOptionIconProvider.DEFAULT,
    getDeleteOptionIcon: ChannelOptionIconProvider = ChannelOptionIconProvider.DEFAULT,
) : ChannelListIconProviderContainer {

    override var getMoreOptionsIcon: ChannelOptionIconProvider by ListenerDelegate(getMoreOptionsIcon) { realPredicate ->
        ChannelOptionIconProvider { channel ->
            realPredicate().invoke(channel)
        }
    }

    override var getDeleteOptionIcon: ChannelOptionIconProvider by ListenerDelegate(getDeleteOptionIcon) { realPredicate ->
        ChannelOptionIconProvider { channel ->
            realPredicate().invoke(channel)
        }
    }
}
