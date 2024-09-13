
package network.ermis.ui.view.channels.adapter.viewholder

import network.ermis.ui.view.ChannelListView.ChannelOptionVisibilityPredicate

public sealed interface ChannelListVisibilityContainer {
    public val isMoreOptionsVisible: ChannelOptionVisibilityPredicate
    public val isDeleteOptionVisible: ChannelOptionVisibilityPredicate
}
