
package network.ermis.sample.feature.chat.info.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import network.ermis.client.ErmisClient
import network.ermis.core.models.Channel
import network.ermis.ui.ChatUI
import network.ermis.ui.view.ChannelListView
import network.ermis.ui.view.channels.adapter.ChannelListItem
import network.ermis.ui.view.channels.adapter.ChannelListPayloadDiff
import network.ermis.ui.view.channels.adapter.viewholder.BaseChannelListItemViewHolder
import network.ermis.ui.view.channels.adapter.viewholder.ChannelListItemViewHolderFactory
import network.ermis.chat.ui.sample.R
import network.ermis.chat.ui.sample.databinding.ChatInfoSharedGroupsItemBinding

class ChatInfoSharedGroupsViewHolderFactory : ChannelListItemViewHolderFactory() {

    override fun createChannelViewHolder(parentView: ViewGroup): BaseChannelListItemViewHolder {
        return ChatInfoSharedGroupsViewHolder(parentView, listenerContainer.channelClickListener)
    }
}

class ChatInfoSharedGroupsViewHolder(
    parent: ViewGroup,
    private val channelClickListener: ChannelListView.ChannelClickListener,
    private val binding: ChatInfoSharedGroupsItemBinding = ChatInfoSharedGroupsItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false,
    ),
) : BaseChannelListItemViewHolder(binding.root) {

    private lateinit var channel: Channel

    init {
        binding.root.setOnClickListener { channelClickListener.onClick(channel) }
    }

    override fun bind(channelItem: ChannelListItem.ChannelItem, diff: ChannelListPayloadDiff) {
        this.channel = channelItem.channel

        binding.apply {
            channelAvatarView.setChannel(channel)
            nameTextView.text = ChatUI.channelNameFormatter.formatChannelName(
                channel = channel,
                currentUser = ErmisClient.instance().clientState.user.value,
            )
            membersCountTextView.text = itemView.context.resources.getQuantityString(
                R.plurals.members_count_title,
                channel.members.size,
                channel.members.size,
            )
        }
    }
}
