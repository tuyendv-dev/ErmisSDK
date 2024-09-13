
package network.ermis.ui.view.channels.adapter.viewholder

import network.ermis.ui.view.ChannelListView.ChannelOptionIconProvider

public sealed interface ChannelListIconProviderContainer {
    public val getMoreOptionsIcon: ChannelOptionIconProvider
    public val getDeleteOptionIcon: ChannelOptionIconProvider
}
