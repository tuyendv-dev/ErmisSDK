
package network.ermis.ui.view.pinned

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.Message
import network.ermis.ui.ChatUI
import io.getstream.chat.android.ui.common.extensions.internal.context
import network.ermis.ui.components.databinding.ItemMentionListBinding
import network.ermis.ui.components.databinding.PinnedMessageListLoadingMoreViewBinding
import network.ermis.ui.view.messages.preview.MessagePreviewStyle
import network.ermis.ui.view.pinned.PinnedMessageListView.PinnedMessageSelectedListener
import network.ermis.ui.utils.extensions.asMention
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class PinnedMessageListAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback) {

    private var pinnedMessageSelectedListener: PinnedMessageSelectedListener? = null

    var messagePreviewStyle: MessagePreviewStyle? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_MESSAGE) {
            ItemMentionListBinding
                .inflate(parent.streamThemeInflater, parent, false)
                .let { binding ->
                    messagePreviewStyle?.let(binding.root::styleView)
                    MessagePreviewViewHolder(binding)
                }
        } else {
            PinnedMessageListLoadingMoreViewBinding
                .inflate(parent.streamThemeInflater, parent, false)
                .let { binding ->
                    PinnedMessageLoadingMoreView(binding)
                }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessagePreviewViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id.isNotEmpty()) {
            ITEM_MESSAGE
        } else {
            ITEM_LOADING_MORE
        }
    }

    fun setPinnedMessageSelectedListener(pinnedMessageSelectedListener: PinnedMessageSelectedListener?) {
        this.pinnedMessageSelectedListener = pinnedMessageSelectedListener
    }

    companion object {
        private const val ITEM_MESSAGE = 0
        private const val ITEM_LOADING_MORE = 1
    }

    inner class PinnedMessageLoadingMoreView(
        private val binding: PinnedMessageListLoadingMoreViewBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    inner class MessagePreviewViewHolder(
        private val binding: ItemMentionListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var message: Message

        init {
            binding.root.setOnClickListener {
                pinnedMessageSelectedListener?.onPinnedMessageSelected(message)
            }
        }

        internal fun bind(message: Message) {
            this.message = message
            binding.root.setMessage(message, ChatUI.currentUserProvider.getCurrentUser()?.asMention(context))
        }
    }

    private object MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            // Comparing only properties used by the ViewHolder
            return oldItem.id == newItem.id &&
                oldItem.createdAt == newItem.createdAt &&
                oldItem.createdLocallyAt == newItem.createdLocallyAt &&
                oldItem.text == newItem.text &&
                oldItem.user == newItem.user
        }
    }
}
