
package network.ermis.ui.view.messages.adapter.viewholder.decorator

import network.ermis.core.internal.exhaustive
import network.ermis.ui.view.messages.adapter.BaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.CustomAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.DateDividerViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.FileAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyAttachmentViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.LinkAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MediaAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessageDeletedViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessagePlainTextViewHolder

public abstract class BaseDecorator : Decorator {

    public override fun <T : MessageListItem> decorate(
        viewHolder: BaseMessageItemViewHolder<T>,
        data: T,
    ) {
        if (data !is MessageListItem.MessageItem) {
            return
        }
        when (viewHolder) {
            is MessageDeletedViewHolder -> decorateDeletedMessage(viewHolder, data)
            is MessagePlainTextViewHolder -> decoratePlainTextMessage(viewHolder, data)
            is CustomAttachmentsViewHolder -> decorateCustomAttachmentsMessage(viewHolder, data)
            is LinkAttachmentsViewHolder -> decorateLinkAttachmentsMessage(viewHolder, data)
            is GiphyViewHolder -> decorateGiphyMessage(viewHolder, data)
            is GiphyAttachmentViewHolder -> decorateGiphyAttachmentMessage(viewHolder, data)
            is FileAttachmentsViewHolder -> decorateFileAttachmentsMessage(viewHolder, data)
            is MediaAttachmentsViewHolder -> decorateMediaAttachmentsMessage(viewHolder, data)
            is DateDividerViewHolder -> Unit
            else -> Unit
        }.exhaustive
    }

    /**
     * Applies various decorations to the [CustomAttachmentsViewHolder].
     *
     * @param viewHolder The holder to be decorated.
     * @param data The data used to define various decorations.
     */
    protected abstract fun decorateCustomAttachmentsMessage(
        viewHolder: CustomAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    )

    /**
     * Applies various decorations to the [GiphyAttachmentViewHolder].
     *
     * @param viewHolder The holder to be decorated.
     * @param data The data used to define various decorations.
     */
    protected abstract fun decorateGiphyAttachmentMessage(
        viewHolder: GiphyAttachmentViewHolder,
        data: MessageListItem.MessageItem,
    )

    /**
     * Applies various decorations to the [FileAttachmentsViewHolder].
     *
     * @param viewHolder The holder to be decorated.
     * @param data The data used to define various decorations.
     */
    protected abstract fun decorateFileAttachmentsMessage(
        viewHolder: FileAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    )

    /**
     * Applies various decorations to the [MediaAttachmentsViewHolder].
     *
     * @param viewHolder The holder to be decorated.
     * @param data The data used to define various decorations.
     */
    protected abstract fun decorateMediaAttachmentsMessage(
        viewHolder: MediaAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    )

    protected abstract fun decoratePlainTextMessage(
        viewHolder: MessagePlainTextViewHolder,
        data: MessageListItem.MessageItem,
    )

    /**
     * Applies various decorations to [LinkAttachmentsViewHolder].
     *
     * @param viewHolder The holder to be decorated.
     * @param data The data used to define various decorations.
     */
    protected abstract fun decorateLinkAttachmentsMessage(
        viewHolder: LinkAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    )

    /**
     * Applies various decorations to the [MessageDeletedViewHolder].
     *
     * @param viewHolder The holder to be decorated.
     * @param data The data used to define various decorations.
     */
    protected open fun decorateDeletedMessage(
        viewHolder: MessageDeletedViewHolder,
        data: MessageListItem.MessageItem,
    ): Unit = Unit

    /**
     * Applies various decorations to the [GiphyViewHolder].
     *
     * @param viewHolder The holder to be decorated.
     * @param data The data used to define various decorations.
     */
    protected open fun decorateGiphyMessage(
        viewHolder: GiphyViewHolder,
        data: MessageListItem.MessageItem,
    ): Unit = Unit
}
