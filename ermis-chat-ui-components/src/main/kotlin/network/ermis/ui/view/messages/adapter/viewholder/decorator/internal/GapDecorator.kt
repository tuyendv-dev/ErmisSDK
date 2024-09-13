
package network.ermis.ui.view.messages.adapter.viewholder.decorator.internal

import network.ermis.ui.common.state.messages.list.MessagePosition
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.view.internal.GapView
import network.ermis.ui.view.messages.adapter.viewholder.decorator.BaseDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.CustomAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.FileAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyAttachmentViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.LinkAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MediaAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessageDeletedViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessagePlainTextViewHolder

internal class GapDecorator : BaseDecorator() {

    /**
     * The type of the decorator. In this case [Decorator.Type.BuiltIn.GAP].
     */
    override val type: Decorator.Type = Decorator.Type.BuiltIn.GAP

    /**
     * Decorates the gap of the message containing custom attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateCustomAttachmentsMessage(
        viewHolder: CustomAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    /**
     * Decorates the gap of the Giphy attachment.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateGiphyAttachmentMessage(
        viewHolder: GiphyAttachmentViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    /**
     * Decorates the gap of the message containing file attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateFileAttachmentsMessage(
        viewHolder: FileAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    /**
     * Decorates the gap section of messages containing image and/or video attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateMediaAttachmentsMessage(
        viewHolder: MediaAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    override fun decorateDeletedMessage(
        viewHolder: MessageDeletedViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    override fun decoratePlainTextMessage(
        viewHolder: MessagePlainTextViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    override fun decorateGiphyMessage(
        viewHolder: GiphyViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    /**
     * Decorates the gap of the link attachment message.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateLinkAttachmentsMessage(
        viewHolder: LinkAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupGapView(viewHolder.binding.gapView, data)

    private fun setupGapView(gapView: GapView, data: MessageListItem.MessageItem) {
        if (data.positions.contains(MessagePosition.TOP)) {
            gapView.showBigGap()
        } else {
            gapView.showSmallGap()
        }
    }
}
