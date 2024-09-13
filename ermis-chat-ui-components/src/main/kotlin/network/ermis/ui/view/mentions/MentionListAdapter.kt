
package network.ermis.ui.view.mentions

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.Message
import network.ermis.ui.ChatUI
import io.getstream.chat.android.ui.common.extensions.internal.context
import network.ermis.ui.components.databinding.ItemMentionListBinding
import network.ermis.ui.view.mentions.MentionListView.MentionSelectedListener
import network.ermis.ui.view.mentions.MentionListAdapter.MessagePreviewViewHolder
import network.ermis.ui.view.messages.preview.MessagePreviewStyle
import network.ermis.ui.view.messages.preview.MessagePreviewView
import network.ermis.ui.utils.extensions.asMention
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class MentionListAdapter : ListAdapter<Message, MessagePreviewViewHolder>(MessageDiffCallback) {

    private var mentionSelectedListener: MentionSelectedListener? = null

    var previewStyle: MessagePreviewStyle? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagePreviewViewHolder {
        return ItemMentionListBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { binding ->
                previewStyle?.let(binding.root::styleView)
                MessagePreviewViewHolder(binding.root)
            }
    }

    override fun onBindViewHolder(holder: MessagePreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setMentionSelectedListener(mentionSelectedListener: MentionSelectedListener?) {
        this.mentionSelectedListener = mentionSelectedListener
    }

    inner class MessagePreviewViewHolder(
        private val view: MessagePreviewView,
    ) : RecyclerView.ViewHolder(view) {

        private lateinit var message: Message

        init {
            view.setOnClickListener {
                mentionSelectedListener?.onMentionSelected(message)
            }
        }

        internal fun bind(message: Message) {
            this.message = message
            view.setMessage(message, ChatUI.currentUserProvider.getCurrentUser()?.asMention(context))
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
