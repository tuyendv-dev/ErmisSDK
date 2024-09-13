
package network.ermis.ui.view.messages.adapter.viewholder.internal

import android.view.ViewGroup
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.BaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * ViewHolder used for displaying loading more indicator.
 *
 * @param parent The parent container.
 * @param style Style for view holders.
 */
internal class LoadingMoreViewHolder(
    parent: ViewGroup,
    style: MessageListItemStyle,
) : BaseMessageItemViewHolder<MessageListItem.LoadingMoreIndicatorItem>(
    parent.streamThemeInflater.inflate(style.loadingMoreView, parent, false),
) {

    override fun bindData(data: MessageListItem.LoadingMoreIndicatorItem, diff: MessageListItemPayloadDiff?) = Unit
}
