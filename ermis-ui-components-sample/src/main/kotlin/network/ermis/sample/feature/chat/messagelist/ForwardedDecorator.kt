package network.ermis.sample.feature.chat.messagelist

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.MarginLayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import network.ermis.ui.common.utils.Utils
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.viewholder.decorator.BaseDecorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.CustomAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.FileAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyAttachmentViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.LinkAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MediaAttachmentsViewHolder
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.MessagePlainTextViewHolder
import network.ermis.chat.ui.sample.R

class ForwardedDecorator(
    private val forceShowForAllMessages: Boolean = false,
) : BaseDecorator() {

    override val type: Decorator.Type = CustomDecoratorType.FORWARDED

    private val forwardedViewId = View.generateViewId()

    override fun decorateCustomAttachmentsMessage(
        viewHolder: CustomAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        setupForwardedView(viewHolder.binding.messageContainer, data)
    }

    override fun decorateGiphyAttachmentMessage(
        viewHolder: GiphyAttachmentViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        setupForwardedView(viewHolder.binding.messageContainer, data)
    }

    override fun decorateFileAttachmentsMessage(
        viewHolder: FileAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        setupForwardedView(viewHolder.binding.messageContainer, data)
    }

    override fun decorateMediaAttachmentsMessage(
        viewHolder: MediaAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        setupForwardedView(viewHolder.binding.messageContainer, data)
    }

    override fun decoratePlainTextMessage(viewHolder: MessagePlainTextViewHolder, data: MessageListItem.MessageItem) {
        setupForwardedView(viewHolder.binding.messageContainer, data, isPlainText = true)
    }

    override fun decorateLinkAttachmentsMessage(
        viewHolder: LinkAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        setupForwardedView(viewHolder.binding.messageContainer, data)
    }

    private fun setupForwardedView(
        container: ViewGroup,
        data: MessageListItem.MessageItem,
        isPlainText: Boolean = false,
    ) {
        val isForwarded = forceShowForAllMessages || data.message.extraData["forwarded"] as? Boolean ?: false
        var textView = container.findViewById<TextView>(forwardedViewId)
        if (textView == null && isForwarded) {
            textView = createTextView(container, isPlainText)
            container.addView(textView, 0)
        }
        textView?.isVisible = isForwarded
    }

    private fun createTextView(container: ViewGroup, isPlainText: Boolean) = TextView(container.context).apply {
        id = forwardedViewId
        layoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            topMargin = Utils.dpToPx(MARGIN_TOP_DP)
            marginStart = Utils.dpToPx(MARGIN_START_DP)
            marginEnd = Utils.dpToPx(MARGIN_END_DP)
            if (!isPlainText) bottomMargin = Utils.dpToPx(MARGIN_BOTTOM_DP)
        }
        setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE)
        setText(R.string.message_forwarded)
        setTextColor(ContextCompat.getColor(container.context, R.color.message_forwarded))
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.rounded_arrow_top_right_24, 0, 0, 0)
    }

    companion object {
        const val MARGIN_TOP_DP = 4
        const val MARGIN_START_DP = 8
        const val MARGIN_END_DP = 16
        const val MARGIN_BOTTOM_DP = 4

        const val TEXT_SIZE = 13f
    }
}
