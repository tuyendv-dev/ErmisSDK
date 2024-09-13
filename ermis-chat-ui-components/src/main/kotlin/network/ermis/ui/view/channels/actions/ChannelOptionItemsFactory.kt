
package network.ermis.ui.view.channels.actions

import android.content.Context
import network.ermis.client.utils.extensions.currentUserUnreadCount
import network.ermis.core.models.Channel
import network.ermis.core.models.ChannelCapabilities
import network.ermis.ui.components.R
import network.ermis.ui.common.state.channels.Cancel
import network.ermis.ui.common.state.channels.DeleteConversation
import network.ermis.ui.common.state.channels.LeaveGroup
import network.ermis.ui.common.state.channels.MarkAsRead
import network.ermis.ui.common.state.channels.ViewInfo
import network.ermis.ui.utils.extensions.getDrawableCompat

/**
 * An interface that allows the creation of channel option items.
 */
internal interface ChannelOptionItemsFactory {

    /**
     * Creates [ChannelOptionItem]s for the selected channel.
     *
     * @param selectedChannel The currently selected channel.
     * @param ownCapabilities Set of capabilities the user is given for the current channel.
     * @param style The style of the dialog.
     * @return The list of channel option items to display.
     */
    fun createChannelOptionItems(
        selectedChannel: Channel,
        ownCapabilities: Set<String>,
        style: ChannelActionsDialogViewStyle,
    ): List<ChannelOptionItem>

    companion object {
        /**
         * Builds the default channel option items factory.
         *
         * @return The default implementation of [ChannelOptionItemsFactory].
         */
        fun defaultFactory(context: Context): ChannelOptionItemsFactory {
            return DefaultChannelOptionItemsFactory(context)
        }
    }
}

/**
 * The default implementation of [ChannelOptionItemsFactory].
 *
 * @param context The context to load resources.
 */
internal open class DefaultChannelOptionItemsFactory(
    private val context: Context,
) : ChannelOptionItemsFactory {

    /**
     * Creates [ChannelOptionItem]s for the selected channel.
     *
     * @param selectedChannel The currently selected channel.
     * @param ownCapabilities Set of capabilities the user is given for the current channel.
     * @param style The style of the dialog.
     * @return The list of channel option items to display.
     */
    override fun createChannelOptionItems(
        selectedChannel: Channel,
        ownCapabilities: Set<String>,
        style: ChannelActionsDialogViewStyle,
    ): List<ChannelOptionItem> {
        val canLeaveChannel = ownCapabilities.contains(ChannelCapabilities.LEAVE_CHANNEL)
        val canDeleteChannel = ownCapabilities.contains(ChannelCapabilities.DELETE_CHANNEL)
        val canReadEvent = ownCapabilities.contains(ChannelCapabilities.READ_EVENTS)

        return listOfNotNull(
            if (style.viewInfoEnabled) {
                ChannelOptionItem(
                    optionText = context.getString(R.string.ermis_ui_channel_list_view_info),
                    optionIcon = context.getDrawableCompat(R.drawable.ic_single_user)!!,
                    channelAction = ViewInfo(selectedChannel),
                )
            } else {
                null
            },
            if (style.markAsReadEnabled && canReadEvent && selectedChannel.currentUserUnreadCount > 0) {
                ChannelOptionItem(
                    optionText = context.getString(R.string.ermis_ui_channel_list_mark_as_read),
                    optionIcon = context.getDrawableCompat(R.drawable.ic_mark_as_read)!!,
                    channelAction = MarkAsRead(selectedChannel),
                )
            } else {
                null
            },
            if (style.leaveGroupEnabled && canLeaveChannel) {
                ChannelOptionItem(
                    optionText = context.getString(R.string.ermis_ui_channel_list_leave_channel),
                    optionIcon = context.getDrawableCompat(R.drawable.ic_leave_group)!!,
                    channelAction = LeaveGroup(selectedChannel),
                )
            } else {
                null
            },
            if (style.deleteConversationEnabled && canDeleteChannel) {
                ChannelOptionItem(
                    optionText = context.getString(R.string.ermis_ui_channel_list_delete_channel),
                    optionIcon = context.getDrawableCompat(R.drawable.ic_delete)!!,
                    channelAction = DeleteConversation(selectedChannel),
                    isWarningItem = true,
                )
            } else {
                null
            },
            if (style.cancelEnabled) {
                ChannelOptionItem(
                    optionText = context.getString(R.string.ermis_ui_channel_list_dismiss_dialog),
                    optionIcon = context.getDrawableCompat(R.drawable.ic_clear)!!,
                    channelAction = Cancel,
                )
            } else {
                null
            },
        )
    }
}
