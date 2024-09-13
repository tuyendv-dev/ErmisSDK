
package network.ermis.ui.view.messages.adapter.internal

import android.view.View
import androidx.annotation.CallSuper
import network.ermis.ui.view.messages.adapter.BaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator

public abstract class DecoratedBaseMessageItemViewHolder<T : MessageListItem> internal constructor(
    itemView: View,
    private val decorators: List<Decorator>,
) : BaseMessageItemViewHolder<T>(itemView) {
    @CallSuper
    override fun bindData(data: T, diff: MessageListItemPayloadDiff?) {
        decorators.forEach { it.decorate(this, data) }
    }
}
