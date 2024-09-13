
package network.ermis.ui.view.messages.adapter.viewholder.decorator.impl

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import network.ermis.ui.components.databinding.ItemMessageDeletedBinding
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater

public class MessageDeletedViewHolder internal constructor(
    parent: ViewGroup,
    decorators: List<Decorator>,
    public val style: MessageListItemStyle,
    public val binding: ItemMessageDeletedBinding = ItemMessageDeletedBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root, decorators) {

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        super.bindData(data, diff)

        if (diff?.deleted == false) return

        binding.deleteLabel.setTextStyle(
            when (data.isTheirs) {
                true -> style.textStyleMessageDeletedTheirs
                else -> style.textStyleMessageDeletedMine
            } ?: style.textStyleMessageDeleted,
        )

        binding.messageContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            horizontalBias = if (data.isTheirs) 0f else 1f
        }

        binding.footnote.updateLayoutParams<ConstraintLayout.LayoutParams> {
            horizontalBias = if (data.isTheirs) 0f else 1f
        }
    }
}
