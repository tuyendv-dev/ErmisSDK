
package network.ermis.ui.view.messages.adapter.viewholder.decorator.impl

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import network.ermis.ui.components.databinding.ItemMessagePlainTextBinding
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.MessageListListeners
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.internal.LongClickFriendlyLinkMovementMethod
import network.ermis.ui.helper.transformer.ChatMessageTextTransformer
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * ViewHolder used for displaying messages that contain only text.
 */
public class MessagePlainTextViewHolder internal constructor(
    parent: ViewGroup,
    decorators: List<Decorator>,
    private val listeners: MessageListListeners?,
    private val messageTextTransformer: ChatMessageTextTransformer,
    public val binding: ItemMessagePlainTextBinding =
        ItemMessagePlainTextBinding.inflate(
            parent.streamThemeInflater,
            parent,
            false,
        ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root, decorators) {

    init {
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
                messageText.setOnClickListener {
                    container.messageClickListener.onMessageClick(data.message)
                }
                LongClickFriendlyLinkMovementMethod.set(
                    textView = messageText,
                    longClickTarget = messageContainer,
                    onLinkClicked = container.linkClickListener::onLinkClick,
                )
            }
        }
    }

    override fun messageContainerView(): View = binding.messageContainer

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        super.bindData(data, diff)
        val textUnchanged = diff?.text == false
        val mentionsUnchanged = diff?.mentions == false
        if (textUnchanged && mentionsUnchanged) return

        with(binding) {
            messageTextTransformer.transformAndApply(messageText, data)
            messageContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                horizontalBias = if (data.isTheirs) 0f else 1f
            }
        }
    }
}
