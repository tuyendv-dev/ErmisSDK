
package network.ermis.ui.view.channels.adapter

import network.ermis.core.models.Channel
import network.ermis.core.models.User

public sealed class ChannelListItem {
    public data class ChannelItem(val channel: Channel, val typingUsers: List<User>) : ChannelListItem()
    public object LoadingMoreItem : ChannelListItem() {
        override fun toString(): String = "LoadingMoreItem"
    }
}
