
package network.ermis.ui.view.channels.adapter.viewholder.internal

import android.view.ViewGroup
import network.ermis.ui.view.channels.ChannelListViewStyle
import network.ermis.ui.view.channels.adapter.ChannelListItem
import network.ermis.ui.view.channels.adapter.ChannelListPayloadDiff
import network.ermis.ui.view.channels.adapter.viewholder.BaseChannelListItemViewHolder
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class ChannelListLoadingMoreViewHolder(
    parent: ViewGroup,
    style: ChannelListViewStyle,
) : BaseChannelListItemViewHolder(parent.streamThemeInflater.inflate(style.loadingMoreView, parent, false)) {

    override fun bind(channelItem: ChannelListItem.ChannelItem, diff: ChannelListPayloadDiff): Unit = Unit
}
