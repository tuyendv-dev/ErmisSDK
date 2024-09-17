package network.ermis.ui.view.messages.adapter.viewholder.internal

import android.view.ViewGroup
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.ItemUnreadSeparatorBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class UnreadSeparatorViewHolder(
    parent: ViewGroup,
    decorators: List<Decorator>,
    private val style: MessageListItemStyle,
    internal val binding: ItemUnreadSeparatorBinding = ItemUnreadSeparatorBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.UnreadSeparatorItem>(binding.root, decorators) {

    override fun bindData(data: MessageListItem.UnreadSeparatorItem, diff: MessageListItemPayloadDiff?) {
        super.bindData(data, diff)

        binding.root.setBackgroundColor(style.unreadSeparatorBackgroundColor)
        binding.unreadSeparatorLabel.setTextStyle(style.unreadSeparatorTextStyle)
        binding.unreadSeparatorLabel.text =
            context.resources.getString(R.string.ermis_ui_message_list_unread_separator)
    }
}
