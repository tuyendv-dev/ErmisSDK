
package network.ermis.ui.view.messages.adapter.viewholder.decorator.internal

import android.widget.TextView
import network.ermis.ui.view.messages.MessageListItemStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.viewholder.decorator.BaseDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.CustomAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.FileAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyAttachmentViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.LinkAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MediaAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessagePlainTextViewHolder
import network.ermis.ui.font.setTextStyle

internal class TextDecorator(private val style: MessageListItemStyle) : BaseDecorator() {

    /**
     * The type of the decorator. In this case [Decorator.Type.BuiltIn.TEXT].
     */
    override val type: Decorator.Type = Decorator.Type.BuiltIn.TEXT

    /**
     * Decorates the text of the message containing custom attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateCustomAttachmentsMessage(
        viewHolder: CustomAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupTextView(viewHolder.binding.messageText, data)

    /**
     * Decorates the text of the Giphy attachment.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateGiphyAttachmentMessage(
        viewHolder: GiphyAttachmentViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupTextView(viewHolder.binding.messageText, data)

    /**
     * Decorates the text of the message containing file attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateFileAttachmentsMessage(
        viewHolder: FileAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupTextView(viewHolder.binding.messageText, data)

    /**
     * Decorates the text of messages containing image and/or video attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateMediaAttachmentsMessage(
        viewHolder: MediaAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupTextView(viewHolder.binding.messageText, data)

    /**
     * Decorates the text of the plain text message.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decoratePlainTextMessage(
        viewHolder: MessagePlainTextViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupTextView(viewHolder.binding.messageText, data)

    /**
     * Does nothing for the ephemeral Giphy message as it can't contain text.
     */
    override fun decorateGiphyMessage(
        viewHolder: GiphyViewHolder,
        data: MessageListItem.MessageItem,
    ) = Unit

    /**
     * Decorates the text of the message containing file attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateLinkAttachmentsMessage(
        viewHolder: LinkAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = setupTextView(viewHolder.binding.messageText, data)

    private fun setupTextView(textView: TextView, data: MessageListItem.MessageItem) {
        val textStyle = if (data.isMine) style.textStyleMine else style.textStyleTheirs
        textView.setTextStyle(textStyle)

        style.getStyleLinkTextColor(data.isMine)?.let { linkTextColor ->
            textView.setLinkTextColor(linkTextColor)
        }
    }
}
