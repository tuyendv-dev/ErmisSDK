package network.ermis.ui.view.invite

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.Channel
import network.ermis.ui.ChatUI
import network.ermis.ui.common.utils.extensions.isDirectMessaging
import network.ermis.ui.components.databinding.UiInvitePreviewItemBinding
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class InviteListAdapter : ListAdapter<Channel, InviteListAdapter.InviteViewHolder>(InviteDiffCallback) {

    private var channelInviteListener: ChannelInviteListView.ChannelInviteListener? = null

    // var previewStyle: MessagePreviewStyle? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        return UiInvitePreviewItemBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { binding ->
                // previewStyle?.let(binding.root::styleView)
                InviteViewHolder(binding)
            }
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setChannelInviteListener(channelInviteListener: ChannelInviteListView.ChannelInviteListener?) {
        this.channelInviteListener = channelInviteListener
    }

    inner class InviteViewHolder(
        private val binding: UiInvitePreviewItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var channel: Channel

        init {
            binding.btnAccept.setOnClickListener {
                channelInviteListener?.onAcceptInvite(channel)
            }
            binding.btnDecline.setOnClickListener {
                channelInviteListener?.onDeclineInvite(channel)
            }
            binding.contentRoot.setOnClickListener {
                channelInviteListener?.onClickInvite(channel)
            }
        }

        internal fun bind(channel: Channel) {
            this.channel = channel
            binding.channelAvatarView.setChannel(channel)
            binding.channelNameLabel.text = ChatUI.channelNameFormatter.formatChannelName(
                channel = channel,
                currentUser = ChatUI.currentUserProvider.getCurrentUser(),
            )
            binding.btnDecline.isVisible = channel.isDirectMessaging().not()
        }
    }

    private object InviteDiffCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            // Comparing only properties used by the ViewHolder
            return oldItem.id == newItem.id &&
                oldItem.createdAt == newItem.createdAt
        }
    }
}
