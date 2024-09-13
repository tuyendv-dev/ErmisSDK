
package network.ermis.ui.view.messages.adapter.viewholder.decorator.impl

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import network.ermis.ui.components.databinding.ItemLinkAttachmentBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.MessageListListeners
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.internal.LongClickFriendlyLinkMovementMethod
import network.ermis.ui.helper.transformer.ChatMessageTextTransformer
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.extension.hasLink

/**
 * ViewHolder used for displaying messages that contain link attachments
 * and no other types of attachments.
 *
 * @param parent The parent container.
 * @param decorators List of decorators applied to the ViewHolder.
 * @param messageTextTransformer Formats strings and sets them on the respective TextView.
 * @param listeners Listeners used by the ViewHolder.
 * @param binding Binding generated for the layout.
 */
public class LinkAttachmentsViewHolder internal constructor(
    parent: ViewGroup,
    decorators: List<Decorator>,
    private val messageTextTransformer: ChatMessageTextTransformer,
    private val listeners: MessageListListeners?,
    public val binding: ItemLinkAttachmentBinding = ItemLinkAttachmentBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
    public val style: MessageListItemStyle,
) : DecoratedBaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root, decorators) {

    /**
     * Initializes the ViewHolder class.
     */
    init {
        applyLinkAttachmentViewStyle()
        initializeListeners()
        setLinkMovementMethod()
    }

    override fun messageContainerView(): View = binding.messageContainer

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        super.bindData(data, diff)

        updateHorizontalBias(data)

        val linkAttachment = data.message.attachments.firstOrNull { attachment -> attachment.hasLink() }

        linkAttachment?.let { attachment ->
            binding.linkAttachmentView.showLinkAttachment(attachment, style)
            messageTextTransformer.transformAndApply(binding.messageText, data)
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
                messageContainer.setOnClickListener {
                    container.messageClickListener.onMessageClick(data.message)
                }
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
                linkAttachmentView.setLinkPreviewClickListener {
                    listeners.linkClickListener.onLinkClick(it)
                }
                linkAttachmentView.setOnLongClickListener {
                    container.messageLongClickListener.onMessageLongClick(data.message)
                    true
                }
            }
        }
    }

    /**
     * Enables clicking on links.
     */
    private fun setLinkMovementMethod() {
        listeners?.let { container ->
            LongClickFriendlyLinkMovementMethod.set(
                textView = binding.messageText,
                longClickTarget = binding.messageContainer,
                onLinkClicked = container.linkClickListener::onLinkClick,
            )
        }
    }

    /**
     * Applies styling to [io.getstream.chat.android.ui.feature.messages.list.adapter.view.internal.LinkAttachmentView].
     */
    private fun applyLinkAttachmentViewStyle() {
        with(binding.linkAttachmentView) {
            setLinkDescriptionMaxLines(style.linkDescriptionMaxLines)
            setDescriptionTextStyle(style.textStyleLinkDescription)
            setTitleTextStyle(style.textStyleLinkTitle)
            setLabelTextStyle(style.textStyleLinkLabel)
        }
    }
}
