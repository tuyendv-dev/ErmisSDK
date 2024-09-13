package network.ermis.ui.view.messages.adapter.viewholder.internal

import android.view.ViewGroup
import network.ermis.ui.components.databinding.ItemSystemMessageBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.BaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class SystemMessageViewHolder(
    parent: ViewGroup,
    private val style: MessageListItemStyle,
    internal val binding: ItemSystemMessageBinding = ItemSystemMessageBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : BaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root) {

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        if (diff?.text == false) return
        val displayedText = data.message.text
        binding.messageTextView.text = displayedText
        binding.messageTextView.setTextStyle(style.textStyleSystemMessage)
        binding.messageTextView.gravity = style.systemMessageAlignment
    }
}
