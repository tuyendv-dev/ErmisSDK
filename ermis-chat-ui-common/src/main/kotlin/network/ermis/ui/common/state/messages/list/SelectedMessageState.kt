package network.ermis.ui.common.state.messages.list

import network.ermis.core.models.ChannelCapabilities
import network.ermis.core.models.Message

/**
 * Represents a state when a message or its reactions were selected.
 *
 * @property message The selected message.
 * @property ownCapabilities Set of capabilities the user is given for the current channel.
 * For a full list @see [ChannelCapabilities].
 */
public sealed class SelectedMessageState {
    public abstract val message: Message
    public abstract val ownCapabilities: Set<String>
}

/**
 * Represents a state when a message was selected.
 */
public data class SelectedMessageOptionsState(
    override val message: Message,
    override val ownCapabilities: Set<String>,
) : SelectedMessageState()

/**
 * Represents a state when message reactions were selected.
 */
public data class SelectedMessageReactionsState(
    override val message: Message,
    override val ownCapabilities: Set<String>,
) : SelectedMessageState()

/**
 * Represents a state when the show more reactions button was clicked.
 */
public data class SelectedMessageReactionsPickerState(
    override val message: Message,
    override val ownCapabilities: Set<String>,
) : SelectedMessageState()

/**
 * Represents a state when the moderated message was selected.
 */
public data class SelectedMessageFailedModerationState(
    override val message: Message,
    override val ownCapabilities: Set<String>,
) : SelectedMessageState()
