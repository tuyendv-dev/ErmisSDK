
package network.ermis.ui.view.messages.adapter.viewholder.decorator.internal

import android.graphics.Color
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import network.ermis.ui.components.R
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
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessageDeletedViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessagePlainTextViewHolder
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.getPinnedText
import network.ermis.ui.utils.extensions.setStartDrawableWithSize
import network.ermis.ui.utils.extensions.updateConstraints

/**
 * Decorator responsible for highlighting pinned messages in the message list. Apart from that,
 * shows a caption indicating that the message was pinned by a particular user.
 */
internal class PinIndicatorDecorator(private val style: MessageListItemStyle) : BaseDecorator() {

    /**
     * The type of the decorator. In this case [Decorator.Type.BuiltIn.PIN_INDICATOR].
     */
    override val type: Decorator.Type = Decorator.Type.BuiltIn.PIN_INDICATOR

    /**
     * Decorates the pin indicator of the message containing custom attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateCustomAttachmentsMessage(
        viewHolder: CustomAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupPinIndicator(root, pinIndicatorTextView, data)
    }

    /**
     * Decorates the pin indicator of the Giphy attachment.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateGiphyAttachmentMessage(
        viewHolder: GiphyAttachmentViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupPinIndicator(root, pinIndicatorTextView, data)
    }

    /**
     * Decorates the pin indicator of the message containing file attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateFileAttachmentsMessage(
        viewHolder: FileAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupPinIndicator(root, pinIndicatorTextView, data)
    }

    /**
     * Decorates the pin indicator of messages containing image and/or video attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateMediaAttachmentsMessage(
        viewHolder: MediaAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupPinIndicator(root, pinIndicatorTextView, data)
    }

    /**
     * Decorates the pin indicator of the plain text message.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decoratePlainTextMessage(
        viewHolder: MessagePlainTextViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupPinIndicator(root, pinIndicatorTextView, data)
    }

    /**
     * Does nothing for the deleted message as it can't be pinned.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateDeletedMessage(
        viewHolder: MessageDeletedViewHolder,
        data: MessageListItem.MessageItem,
    ) = Unit

    /**
     * Does nothing for the ephemeral Giphy message as it can't be pinned.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateGiphyMessage(
        viewHolder: GiphyViewHolder,
        data: MessageListItem.MessageItem,
    ) = Unit

    /**
     * Decorates the pin indicator of the link attachments message.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateLinkAttachmentsMessage(
        viewHolder: LinkAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupPinIndicator(root, pinIndicatorTextView, data)
    }

    private fun setupPinIndicator(
        root: ConstraintLayout,
        pinIndicatorTextView: TextView,
        data: MessageListItem.MessageItem,
    ) {
        if (data.message.pinned) {
            pinIndicatorTextView.isVisible = true
            pinIndicatorTextView.text = data.message.getPinnedText(root.context)
            pinIndicatorTextView.setTextStyle(style.pinnedMessageIndicatorTextStyle)
            pinIndicatorTextView.setStartDrawableWithSize(
                style.pinnedMessageIndicatorIcon,
                R.dimen.ermis_ui_message_pin_indicator_icon_size,
            )

            root.setBackgroundColor(style.pinnedMessageBackgroundColor)
            root.updateConstraints {
                val bias = if (data.isMine) 1f else 0f
                setHorizontalBias(pinIndicatorTextView.id, bias)
            }
        } else {
            pinIndicatorTextView.isVisible = false

            root.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}
