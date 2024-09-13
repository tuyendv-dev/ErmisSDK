
package network.ermis.ui.view.messages.options

import android.content.Context
import network.ermis.client.utils.attachment.isGiphy
import network.ermis.core.models.AttachmentType
import network.ermis.core.models.ChannelCapabilities
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import network.ermis.core.models.User
import network.ermis.ui.components.R
import network.ermis.ui.common.state.messages.Copy
import network.ermis.ui.common.state.messages.Delete
import network.ermis.ui.common.state.messages.Edit
import network.ermis.ui.common.state.messages.Flag
import network.ermis.ui.common.state.messages.MarkAsUnread
import network.ermis.ui.common.state.messages.Pin
import network.ermis.ui.common.state.messages.Reply
import network.ermis.ui.common.state.messages.Resend
import network.ermis.ui.common.state.messages.ThreadReply
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extension.hasLink

/**
 * An interface that allows the creation of message option items.
 */
public interface MessageOptionItemsFactory {

    /**
     * Creates [MessageOptionItem]s for the selected message.
     *
     * @param selectedMessage The currently selected message.
     * @param currentUser The currently logged in user.
     * @param isInThread If the message is being displayed in a thread.
     * @param ownCapabilities Set of capabilities the user is given for the current channel.
     * @param style The style to be applied to the view.
     * @return The list of message option items to display.
     */
    public fun createMessageOptionItems(
        selectedMessage: Message,
        currentUser: User?,
        isInThread: Boolean,
        ownCapabilities: Set<String>,
        style: MessageListViewStyle,
    ): List<MessageOptionItem>

    public companion object {
        /**
         * Builds the default message option items factory.
         *
         * @return The default implementation of [MessageOptionItemsFactory].
         */
        public fun defaultFactory(context: Context): MessageOptionItemsFactory {
            return DefaultMessageOptionItemsFactory(context)
        }
    }
}

/**
 * The default implementation of [MessageOptionItemsFactory].
 *
 * @param context The context to load resources.
 */
public open class DefaultMessageOptionItemsFactory(
    private val context: Context,
) : MessageOptionItemsFactory {

    /**
     * Creates [MessageOptionItem]s for the selected message.
     *
     * @param selectedMessage The currently selected message.
     * @param currentUser The currently logged in user.
     * @param isInThread If the message is being displayed in a thread.
     * @param ownCapabilities Set of capabilities the user is given for the current channel.
     * @param style The style to be applied to the view.
     * @return The list of message option items to display.
     */
    override fun createMessageOptionItems(
        selectedMessage: Message,
        currentUser: User?,
        isInThread: Boolean,
        ownCapabilities: Set<String>,
        style: MessageListViewStyle,
    ): List<MessageOptionItem> {
        if (selectedMessage.id.isEmpty()) {
            return emptyList()
        }

        val selectedMessageUserId = selectedMessage.user.id

        val isTextOnlyMessage = selectedMessage.text.isNotEmpty() && selectedMessage.attachments.isEmpty()
        val hasLinks = selectedMessage.attachments.any { it.hasLink() && !it.isGiphy() }
        val isOwnMessage = selectedMessageUserId == currentUser?.id
        val isMessageSynced = selectedMessage.syncStatus == SyncStatus.COMPLETED
        val isMessageFailed = selectedMessage.syncStatus == SyncStatus.FAILED_PERMANENTLY

        // user capabilities
        val canQuoteMessage = ownCapabilities.contains(ChannelCapabilities.QUOTE_MESSAGE)
        val canThreadReply = ownCapabilities.contains(ChannelCapabilities.SEND_REPLY)
        val canPinMessage = ownCapabilities.contains(ChannelCapabilities.PIN_MESSAGE)
        val canDeleteOwnMessage = ownCapabilities.contains(ChannelCapabilities.DELETE_OWN_MESSAGE)
        val canDeleteAnyMessage = ownCapabilities.contains(ChannelCapabilities.DELETE_ANY_MESSAGE)
        val canEditOwnMessage = ownCapabilities.contains(ChannelCapabilities.UPDATE_OWN_MESSAGE)
        val canEditAnyMessage = ownCapabilities.contains(ChannelCapabilities.UPDATE_ANY_MESSAGE)
        val canMarkAsUnread = ownCapabilities.contains(ChannelCapabilities.READ_EVENTS)

        return listOfNotNull(
            if (style.retryMessageEnabled && isOwnMessage && isMessageFailed) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_resend_message),
                    optionIcon = context.getDrawableCompat(style.retryIcon)!!,
                    messageAction = Resend(selectedMessage),
                )
            } else {
                null
            },
            if (style.replyEnabled && isMessageSynced && canQuoteMessage) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_reply),
                    optionIcon = context.getDrawableCompat(style.replyIcon)!!,
                    messageAction = Reply(selectedMessage),
                )
            } else {
                null
            },
            if (style.threadsEnabled && !isInThread && isMessageSynced && canThreadReply) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_thread_reply),
                    optionIcon = context.getDrawableCompat(style.threadReplyIcon)!!,
                    messageAction = ThreadReply(selectedMessage),
                )
            } else {
                null
            },
            if (style.markAsUnreadEnabled && canMarkAsUnread) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_mark_as_unread),
                    optionIcon = context.getDrawableCompat(style.markAsUnreadIcon)!!,
                    messageAction = MarkAsUnread(selectedMessage),
                )
            } else {
                null
            },
            if (style.copyTextEnabled && (isTextOnlyMessage || hasLinks)) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_copy_message),
                    optionIcon = context.getDrawableCompat(style.copyIcon)!!,
                    messageAction = Copy(selectedMessage),
                )
            } else {
                null
            },
            if (style.editMessageEnabled && ((isOwnMessage && canEditOwnMessage) || canEditAnyMessage) &&
                selectedMessage.command != AttachmentType.GIPHY
            ) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_edit_message),
                    optionIcon = context.getDrawableCompat(style.editIcon)!!,
                    messageAction = Edit(selectedMessage),
                )
            } else {
                null
            },
            if (style.flagEnabled && !isOwnMessage) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_flag_message),
                    optionIcon = context.getDrawableCompat(style.flagIcon)!!,
                    messageAction = Flag(selectedMessage),
                )
            } else {
                null
            },
            if (style.pinMessageEnabled && isMessageSynced && canPinMessage) {
                val (pinText, pinIcon) = if (selectedMessage.pinned) {
                    R.string.ermis_ui_message_list_unpin_message to style.unpinIcon
                } else {
                    R.string.ermis_ui_message_list_pin_message to style.pinIcon
                }

                MessageOptionItem(
                    optionText = context.getString(pinText),
                    optionIcon = context.getDrawableCompat(pinIcon)!!,
                    messageAction = Pin(selectedMessage),
                )
            } else {
                null
            },
            if (style.deleteMessageEnabled && (canDeleteAnyMessage || (isOwnMessage && canDeleteOwnMessage))) {
                MessageOptionItem(
                    optionText = context.getString(R.string.ermis_ui_message_list_delete_message),
                    optionIcon = context.getDrawableCompat(style.deleteIcon)!!,
                    messageAction = Delete(selectedMessage),
                    isWarningItem = true,
                )
            } else {
                null
            },
        )
    }
}
