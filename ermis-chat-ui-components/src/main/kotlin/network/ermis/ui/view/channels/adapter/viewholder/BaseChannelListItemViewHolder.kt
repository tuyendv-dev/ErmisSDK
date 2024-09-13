
package network.ermis.ui.view.channels.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import network.ermis.ui.view.channels.adapter.ChannelListItem
import network.ermis.ui.view.channels.adapter.ChannelListPayloadDiff

public abstract class BaseChannelListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    public abstract fun bind(channelItem: ChannelListItem.ChannelItem, diff: ChannelListPayloadDiff)
}
