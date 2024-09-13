
package network.ermis.ui.view.channels.adapter.viewholder

import network.ermis.ui.view.ChannelListView

public sealed interface ChannelListListenerContainer {
    public val channelClickListener: ChannelListView.ChannelClickListener
    public val channelLongClickListener: ChannelListView.ChannelLongClickListener
    public val deleteClickListener: ChannelListView.ChannelClickListener
    public val moreOptionsClickListener: ChannelListView.ChannelClickListener
    public val userClickListener: ChannelListView.UserClickListener
    public val swipeListener: ChannelListView.SwipeListener
}
