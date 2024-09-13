
package network.ermis.ui.view.messages.adapter.internal

import network.ermis.client.utils.attachment.isAudioRecording
import network.ermis.client.utils.attachment.isGiphy
import network.ermis.client.utils.attachment.isImage
import network.ermis.client.utils.attachment.isVideo
import network.ermis.client.utils.message.isDeleted
import network.ermis.client.utils.message.isError
import network.ermis.client.utils.message.isGiphyEphemeral
import network.ermis.client.utils.message.isModerationBounce
import network.ermis.client.utils.message.isSystem
import network.ermis.core.models.Attachment
import network.ermis.core.models.Message
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.CUSTOM_ATTACHMENTS
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.DATE_DIVIDER
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.ERROR_MESSAGE
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.FILE_ATTACHMENTS
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.GIPHY
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.GIPHY_ATTACHMENT
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.LINK_ATTACHMENTS
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.LOADING_INDICATOR
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.MEDIA_ATTACHMENT
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.MESSAGE_DELETED
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.PLAIN_TEXT
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.START_OF_THE_CHANNEL
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.SYSTEM_MESSAGE
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.THREAD_PLACEHOLDER
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.THREAD_SEPARATOR
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.TYPING_INDICATOR
import network.ermis.ui.view.messages.adapter.MessageListItemViewType.UNREAD_SEPARATOR
import network.ermis.ui.view.messages.adapter.viewholder.attachment.AttachmentFactoryManager
import network.ermis.ui.utils.extension.hasLink
import network.ermis.ui.utils.extension.isFailed
import network.ermis.ui.utils.extension.isUploading

internal object MessageListItemViewTypeMapper {

    fun getViewTypeValue(messageListItem: MessageListItem, attachmentFactoryManager: AttachmentFactoryManager): Int {
        return when (messageListItem) {
            is MessageListItem.DateSeparatorItem -> DATE_DIVIDER
            is MessageListItem.LoadingMoreIndicatorItem -> LOADING_INDICATOR
            is MessageListItem.ThreadSeparatorItem -> THREAD_SEPARATOR
            is MessageListItem.MessageItem -> messageItemToViewType(messageListItem, attachmentFactoryManager)
            is MessageListItem.TypingItem -> TYPING_INDICATOR
            is MessageListItem.ThreadPlaceholderItem -> THREAD_PLACEHOLDER
            is MessageListItem.UnreadSeparatorItem -> UNREAD_SEPARATOR
            is MessageListItem.StartOfTheChannelItem -> START_OF_THE_CHANNEL
        }
    }

    /**
     * Transforms the given [messageItem] to the type of the message we should show in the list.
     *
     * @param messageItem The message item that holds all the information required to generate a message type.
     * @param attachmentFactoryManager A manager for the registered custom attachment factories.
     * @return The [Int] message type.
     */
    private fun messageItemToViewType(
        messageItem: MessageListItem.MessageItem,
        attachmentFactoryManager: AttachmentFactoryManager,
    ): Int {
        val message = messageItem.message

        val (linksAndGiphy, _) = message.attachments.partition { attachment -> attachment.hasLink() }
        val containsGiphy = linksAndGiphy.any(Attachment::isGiphy)
        val hasAttachments = message.attachments.isNotEmpty()

        val containsOnlyLinks = message.containsOnlyLinkAttachments()

        return when {
            message.isDeleted() -> MESSAGE_DELETED
            message.isError() && !message.isModerationBounce() -> ERROR_MESSAGE
            message.isSystem() -> SYSTEM_MESSAGE
            message.isGiphyEphemeral() -> GIPHY
            hasAttachments -> when {
                attachmentFactoryManager.canHandle(message) -> CUSTOM_ATTACHMENTS
                containsGiphy -> GIPHY_ATTACHMENT
                containsOnlyLinks -> LINK_ATTACHMENTS
                message.isMediaAttachment() -> MEDIA_ATTACHMENT
                else -> FILE_ATTACHMENTS
            }
            else -> PLAIN_TEXT
        }
    }

    /**
     * Checks if the message contains only image or video attachments (Can also optionally contain links).
     */
    private fun Message.isMediaAttachment(): Boolean {
        return attachments.isNotEmpty() &&
            attachments.all { it.isImage() || it.isVideo() || it.hasLink() || it.isAudioRecording() } &&
            attachments.none { it.isUploading() || it.isFailed() }
    }

    /**
     * Checks if all attachments are link attachments.
     */
    private fun Message.containsOnlyLinkAttachments(): Boolean {
        if (this.attachments.isEmpty()) return false

        return this.attachments.all { attachment -> attachment.hasLink() }
    }
}
