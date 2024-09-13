
package network.ermis.ui.view.messages.adapter.viewholder.decorator.impl

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import network.ermis.ui.components.databinding.ItemFileAttachmentsBinding
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.MessageListListeners
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.view.internal.AttachmentClickListener
import network.ermis.ui.view.messages.adapter.view.internal.AttachmentDownloadClickListener
import network.ermis.ui.view.messages.adapter.view.internal.AttachmentLongClickListener
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.internal.LongClickFriendlyLinkMovementMethod
import network.ermis.ui.helper.transformer.ChatMessageTextTransformer
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.streamThemeInflater
import io.getstream.log.taggedLogger

/**
 * ViewHolder that displays message items containing file attachments.
 *
 * Note: This ViewHolder is used in situations where the message either contains
 * multiple attachment types or a single attachment type that does not have
 * a designated ViewHolder.
 *
 * You can see the full list of ViewHolders in [io.getstream.chat.android.ui.feature.messages.list.adapter.MessageListItemViewHolderFactory].
 *
 * @param parent The parent container.
 * @param decorators List of decorators applied to the ViewHolder.
 * @param messageTextTransformer Formats strings and sets them on the respective TextView.
 * @param listeners Listeners used by the ViewHolder.
 * @param binding Binding generated for the layout.
 */
public class FileAttachmentsViewHolder internal constructor(
    parent: ViewGroup,
    decorators: List<Decorator>,
    private val listeners: MessageListListeners?,
    private val messageTextTransformer: ChatMessageTextTransformer,
    public val binding: ItemFileAttachmentsBinding = ItemFileAttachmentsBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root, decorators) {

    private val logger by taggedLogger("FileAttachmentListVH")

    /**
     * Initializes the ViewHolder class.
     */
    init {
        initializeListeners()
        setLinkMovementMethod()
        binding.fileAttachmentsView.setPadding(4.dpToPx())
    }

    override fun messageContainerView(): View = binding.messageContainer

    /**
     * Binds the data to the view.
     */
    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        logger.d { "[bindData] data: $data, diff: $diff" }
        super.bindData(data, diff)

        updateHorizontalBias(data)

        binding.fileAttachmentsView.setAttachments(data.message.attachments)

        if (data.message.text.isNotEmpty()) {
            messageTextTransformer.transformAndApply(binding.messageText, data)
            binding.messageText.visibility = View.VISIBLE
        } else {
            binding.messageText.visibility = View.GONE
        }
    }

    /**
     * Updates the horizontal bias of the message according to the owner
     * of the message.
     */
    private fun updateHorizontalBias(data: MessageListItem.MessageItem) {
        binding.messageContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            this.horizontalBias = if (data.isMine) 1f else 0f
        }
    }

    /**
     * Initializes listeners that enable handling clicks on various
     * elements such as reactions, threads, message containers, etc.
     */
    private fun initializeListeners() {
        binding.run {
            listeners?.let { container ->
                reactionsView.setReactionClickListener {
                    container.reactionViewClickListener.onReactionViewClick(data.message)
                }
                footnote.setOnThreadClickListener {
                    container.threadClickListener.onThreadClick(data.message)
                }
                messageContainer.setOnLongClickListener {
                    container.messageLongClickListener.onMessageLongClick(data.message)
                    true
                }
                userAvatarView.setOnClickListener {
                    container.userClickListener.onUserClick(data.message.user)
                }
                binding.fileAttachmentsView.attachmentLongClickListener = AttachmentLongClickListener {
                    container.messageLongClickListener.onMessageLongClick(data.message)
                }
                binding.fileAttachmentsView.attachmentClickListener = AttachmentClickListener { attachment ->
                    container.attachmentClickListener.onAttachmentClick(data.message, attachment)
                }
                binding.fileAttachmentsView.attachmentDownloadClickListener =
                    AttachmentDownloadClickListener(container.attachmentDownloadClickListener::onAttachmentDownloadClick)
            }
        }
    }

    /**
     * Enables clicking on links.
     */
    private fun setLinkMovementMethod() {
        listeners?.let { listenerContainer ->
            LongClickFriendlyLinkMovementMethod.set(
                textView = binding.messageText,
                longClickTarget = binding.messageContainer,
                onLinkClicked = listenerContainer.linkClickListener::onLinkClick,
            )
        }
    }
}
