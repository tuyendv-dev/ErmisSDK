
package network.ermis.ui.view.messages.adapter.viewholder.internal

import android.view.ViewGroup
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.ItemThreadDividerBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class ThreadSeparatorViewHolder(
    parent: ViewGroup,
    decorators: List<Decorator>,
    private val style: MessageListItemStyle,
    internal val binding: ItemThreadDividerBinding =
        ItemThreadDividerBinding.inflate(
            parent.streamThemeInflater,
            parent,
            false,
        ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.ThreadSeparatorItem>(binding.root, decorators) {

    override fun bindData(
        data: MessageListItem.ThreadSeparatorItem,
        diff: MessageListItemPayloadDiff?,
    ) {
        super.bindData(data, diff)

        binding.threadSeparatorLabel.setTextStyle(style.threadSeparatorTextStyle)
        binding.threadSeparatorLabel.text = context.resources.getQuantityString(
            R.plurals.ermis_ui_message_list_thread_separator,
            data.messageCount,
            data.messageCount,
        )
    }
}
