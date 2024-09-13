
package network.ermis.ui.view.channels.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import network.ermis.core.models.Channel
import network.ermis.ui.view.channels.adapter.viewholder.BaseChannelListItemViewHolder
import network.ermis.ui.view.channels.adapter.viewholder.ChannelListItemViewHolderFactory

internal class ChannelListItemAdapter(
    private val viewHolderFactory: ChannelListItemViewHolderFactory,
) : ListAdapter<ChannelListItem, BaseChannelListItemViewHolder>(ChannelListItemDiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return viewHolderFactory.getItemViewType(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseChannelListItemViewHolder {
        return viewHolderFactory.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseChannelListItemViewHolder, position: Int) {
        bind(position, holder, FULL_CHANNEL_LIST_ITEM_PAYLOAD_DIFF)
    }

    override fun onBindViewHolder(holder: BaseChannelListItemViewHolder, position: Int, payloads: MutableList<Any>) {
        val diff = (
            payloads
                .filterIsInstance<ChannelListPayloadDiff>()
                .takeIf { it.isNotEmpty() }
                ?: listOf(FULL_CHANNEL_LIST_ITEM_PAYLOAD_DIFF)
            ).fold(EMPTY_CHANNEL_LIST_ITEM_PAYLOAD_DIFF, ChannelListPayloadDiff::plus)

        bind(position, holder, diff)
    }

    private fun bind(position: Int, holder: BaseChannelListItemViewHolder, payload: ChannelListPayloadDiff) {
        when (val channelItem = getItem(position)) {
            is ChannelListItem.LoadingMoreItem -> Unit
            is ChannelListItem.ChannelItem -> holder.bind(channelItem, payload)
        }
    }

    internal fun getChannel(cid: String): Channel {
        return currentList
            .asSequence()
            .filterIsInstance<ChannelListItem.ChannelItem>()
            .first { it.channel.cid == cid }
            .channel
    }

    companion object {
        private val FULL_CHANNEL_LIST_ITEM_PAYLOAD_DIFF: ChannelListPayloadDiff = ChannelListPayloadDiff(
            nameChanged = true,
            avatarViewChanged = true,
            usersChanged = true,
            lastMessageChanged = true,
            readStateChanged = true,
            unreadCountChanged = true,
            extraDataChanged = true,
            typingUsersChanged = true,
        )

        val EMPTY_CHANNEL_LIST_ITEM_PAYLOAD_DIFF: ChannelListPayloadDiff = ChannelListPayloadDiff(
            nameChanged = false,
            avatarViewChanged = false,
            usersChanged = false,
            lastMessageChanged = false,
            readStateChanged = false,
            unreadCountChanged = false,
            extraDataChanged = false,
            typingUsersChanged = false,
        )
    }
}
