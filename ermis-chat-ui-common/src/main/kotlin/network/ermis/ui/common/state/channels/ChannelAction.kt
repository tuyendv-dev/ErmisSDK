package network.ermis.ui.common.state.channels

import network.ermis.core.models.Channel

/**
 * Represents the list of actions users can take with selected channels.
 *
 * @property channel The selected channel.
 */
public sealed class ChannelAction {
    public abstract val channel: Channel
}

/**
 * Show more info about the channel.
 */
public data class ViewInfo(override val channel: Channel) : ChannelAction()

public data class MarkAsRead(override val channel: Channel) : ChannelAction()

/**
 * Shows a dialog to leave the group.
 */
public data class LeaveGroup(override val channel: Channel) : ChannelAction()

/**
 * Mutes the channel.
 */
public data class MuteChannel(override val channel: Channel) : ChannelAction()

/**
 * Unmutes the channel.
 */
public data class UnmuteChannel(override val channel: Channel) : ChannelAction()

/**
 * Shows a dialog to delete the conversation, if we have the permission.
 */
public data class DeleteConversation(override val channel: Channel) : ChannelAction()

/**
 * Dismisses the actions.
 */
public object Cancel : ChannelAction() {
    override val channel: Channel = Channel()
    override fun toString(): String = "Cancel"
}
