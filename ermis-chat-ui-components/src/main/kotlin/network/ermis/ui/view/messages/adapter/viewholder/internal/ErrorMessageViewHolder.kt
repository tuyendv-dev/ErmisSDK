
package network.ermis.ui.view.messages.adapter.viewholder.internal

import android.view.ViewGroup
import network.ermis.ui.components.databinding.ItemErrorMessageBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.BaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.getTranslatedText
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class ErrorMessageViewHolder(
    parent: ViewGroup,
    private val style: MessageListItemStyle,
    internal val binding: ItemErrorMessageBinding = ItemErrorMessageBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : BaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root) {

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        if (diff?.syncStatus == false) return

        val displayedText = data.message.getTranslatedText()
        binding.messageTextView.text = displayedText
        binding.messageTextView.setTextStyle(style.textStyleErrorMessage)
    }
}
