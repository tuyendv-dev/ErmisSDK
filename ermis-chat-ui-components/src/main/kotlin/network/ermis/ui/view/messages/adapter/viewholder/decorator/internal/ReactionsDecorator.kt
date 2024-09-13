
package network.ermis.ui.view.messages.adapter.viewholder.decorator.internal

import android.graphics.Rect
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
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
import network.ermis.ui.view.messages.reactions.view.ViewReactionsView
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.hasReactions
import network.ermis.ui.utils.extensions.hasSingleReaction
import network.ermis.ui.utils.extensions.updateConstraints

internal class ReactionsDecorator(private val style: MessageListItemStyle) : BaseDecorator() {

    /**
     * The type of the decorator. In this case [Decorator.Type.BuiltIn.REACTIONS].
     */
    override val type: Decorator.Type = Decorator.Type.BuiltIn.REACTIONS

    /**
     * Decorates the reactions section of the message containing custom attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateCustomAttachmentsMessage(
        viewHolder: CustomAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupReactionsView(root, messageContainer, reactionsSpace, reactionsView, data)
    }

    /**
     * Decorates the reactions section of the Giphy attachment.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateGiphyAttachmentMessage(
        viewHolder: GiphyAttachmentViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupReactionsView(root, messageContainer, reactionsSpace, reactionsView, data)
    }

    /**
     * Decorates the reactions section of the message containing file attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateFileAttachmentsMessage(
        viewHolder: FileAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupReactionsView(root, messageContainer, reactionsSpace, reactionsView, data)
    }

    /**
     * Decorates the reactions section of messages containing image and/or video attachments.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateMediaAttachmentsMessage(
        viewHolder: MediaAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) = with(viewHolder.binding) {
        setupReactionsView(root, messageContainer, reactionsSpace, reactionsView, data)
    }

    /**
     * Decorates the reactions section of the plain text message.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decoratePlainTextMessage(
        viewHolder: MessagePlainTextViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        with(viewHolder.binding) {
            setupReactionsView(root, messageContainer, reactionsSpace, reactionsView, data)
        }
    }

    /**
     * Does nothing for the deleted message as it can't contain reactions.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateDeletedMessage(
        viewHolder: MessageDeletedViewHolder,
        data: MessageListItem.MessageItem,
    ) = Unit

    /**
     * Does nothing for the ephemeral Giphy message it can't contain reactions.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateGiphyMessage(
        viewHolder: GiphyViewHolder,
        data: MessageListItem.MessageItem,
    ) = Unit

    /**
     * Decorates the reactions section of the link attachment message.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateLinkAttachmentsMessage(
        viewHolder: LinkAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        with(viewHolder.binding) {
            setupReactionsView(root, messageContainer, reactionsSpace, reactionsView, data)
        }
    }

    private fun setupReactionsView(
        rootConstraintLayout: ConstraintLayout,
        contentView: View,
        reactionsSpace: View,
        reactionsView: ViewReactionsView,
        data: MessageListItem.MessageItem,
    ) {
        if (data.message.hasReactions()) {
            reactionsView.isVisible = true
            reactionsSpace.isVisible = true

            reactionsView.applyStyle(style.reactionsViewStyle)

            reactionsView.setMessage(data.message, data.isMine) {
                rootConstraintLayout.updateConstraints {
                    clear(reactionsView.id, ConstraintSet.START)
                    clear(reactionsView.id, ConstraintSet.END)
                    clear(reactionsSpace.id, ConstraintSet.START)
                    clear(reactionsSpace.id, ConstraintSet.END)
                }

                reactionsSpace.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    if (data.isTheirs) {
                        endToEnd = contentView.id
                        marginEnd = 0
                    } else {
                        startToStart = contentView.id
                        marginStart = 0
                    }
                }

                reactionsView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    if (data.isTheirs) {
                        startToEnd = reactionsSpace.id
                    } else {
                        endToStart = reactionsSpace.id
                    }
                }

                reactionsSpace.doOnPreDraw {
                    val dynamicOffset = calculateDynamicOffset(
                        rootConstraintLayout,
                        reactionsSpace,
                        reactionsView,
                        data,
                    )

                    updateOffset(contentView, reactionsSpace, data, dynamicOffset)
                }
            }
        } else {
            reactionsView.isVisible = false
            reactionsSpace.isVisible = false
        }
    }

    private fun calculateDynamicOffset(
        rootConstraintLayout: ConstraintLayout,
        reactionsSpace: View,
        reactionsView: ViewReactionsView,
        data: MessageListItem.MessageItem,
    ): Int {
        val offsetViewBounds = Rect()
        reactionsSpace.getDrawingRect(offsetViewBounds)
        rootConstraintLayout.offsetDescendantRectToMyCoords(reactionsSpace, offsetViewBounds)
        val relativeXToParent = offsetViewBounds.left
        val rootWidth =
            rootConstraintLayout.measuredWidth - (rootConstraintLayout.paddingStart + rootConstraintLayout.paddingEnd)

        val offsetFromParent =
            if (data.isTheirs) relativeXToParent else rootConstraintLayout.measuredWidth - relativeXToParent

        val expectedReactionsAndOffsetWidth = offsetFromParent + reactionsView.measuredWidth

        return when {
            expectedReactionsAndOffsetWidth > rootConstraintLayout.measuredWidth -> expectedReactionsAndOffsetWidth - rootWidth
            data.message.hasSingleReaction() -> SINGLE_REACTION_OFFSET
            else -> MULTIPLE_REACTIONS_OFFSET
        }
    }

    private fun updateOffset(
        contentView: View,
        reactionsSpace: View,
        data: MessageListItem.MessageItem,
        dynamicOffset: Int,
    ) {
        reactionsSpace.updateLayoutParams<ConstraintLayout.LayoutParams> {
            if (data.isTheirs) {
                endToEnd = contentView.id
                marginEnd = dynamicOffset
            } else {
                startToStart = contentView.id
                marginStart = dynamicOffset
            }
        }
    }

    private companion object {
        private val SINGLE_REACTION_OFFSET = 8.dpToPx()
        private val MULTIPLE_REACTIONS_OFFSET = 26.dpToPx()
    }
}
