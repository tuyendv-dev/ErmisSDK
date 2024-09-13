
package network.ermis.ui.view.messages.adapter.viewholder.decorator.internal

import androidx.core.view.isVisible
import network.ermis.ui.view.MessageListView
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
import network.ermis.ui.widgets.avatar.UserAvatarView

internal class AvatarDecorator(
    private val showAvatarPredicate: MessageListView.ShowAvatarPredicate,
) : BaseDecorator() {

    override val type: Decorator.Type = Decorator.Type.BuiltIn.AVATAR

    /**
     * Decorates the avatar of the custom attachments message, based on the message owner.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateCustomAttachmentsMessage(
        viewHolder: CustomAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        controlVisibility(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine)
        setupAvatar(getAvatarView(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine), data)
    }

    /**
     * Decorates the avatar of the Giphy attachment, based on the message owner.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateGiphyAttachmentMessage(
        viewHolder: GiphyAttachmentViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        controlVisibility(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine)
        setupAvatar(getAvatarView(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine), data)
    }

    /**
     * Decorates the avatar of the file attachments message, based on the message owner.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateFileAttachmentsMessage(
        viewHolder: FileAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        controlVisibility(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine)
        setupAvatar(getAvatarView(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine), data)
    }

    /**
     * Decorates the avatars of messages containing image and/or video attachments, based on the message owner.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateMediaAttachmentsMessage(
        viewHolder: MediaAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        controlVisibility(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine)
        setupAvatar(getAvatarView(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine), data)
    }

    /**
     * Decorates the avatar of the plain text message, based on the message owner.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decoratePlainTextMessage(
        viewHolder: MessagePlainTextViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        controlVisibility(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine)
        setupAvatar(getAvatarView(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine), data)
    }

    /**
     * Decorates the avatar of the link attachment message, based on the message owner.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateLinkAttachmentsMessage(
        viewHolder: LinkAttachmentsViewHolder,
        data: MessageListItem.MessageItem,
    ) {
        controlVisibility(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine)
        setupAvatar(getAvatarView(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine), data)
    }

    /**
     * Does nothing for ephemeral Giphy message, as it doesn't contain an avatar.
     */
    override fun decorateGiphyMessage(
        viewHolder: GiphyViewHolder,
        data: MessageListItem.MessageItem,
    ) = Unit

    /**
     * Decorates the avatar of the deleted message, based on the message owner.
     *
     * @param viewHolder The holder to decorate.
     * @param data The item that holds all the information.
     */
    override fun decorateDeletedMessage(viewHolder: MessageDeletedViewHolder, data: MessageListItem.MessageItem) {
        controlVisibility(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine)
        setupAvatar(getAvatarView(viewHolder.binding.userAvatarMineView, viewHolder.binding.userAvatarView, data.isMine), data)
    }

    private fun setupAvatar(avatarView: UserAvatarView, data: MessageListItem.MessageItem) {
        val shouldShow = showAvatarPredicate.shouldShow(data)

        avatarView.isVisible = shouldShow

        if (shouldShow) {
            avatarView.setUser(data.message.user)
        }
    }

    private fun getAvatarView(myAvatar: UserAvatarView, theirAvatar: UserAvatarView, isMine: Boolean): UserAvatarView {
        return if (isMine) myAvatar else theirAvatar
    }

    private fun controlVisibility(myAvatar: UserAvatarView, theirAvatar: UserAvatarView, isMine: Boolean) {
        theirAvatar.isVisible = !isMine
        myAvatar.isVisible = isMine
    }
}
