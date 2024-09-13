package network.ermis.ui.common.state.messages.composer

import network.ermis.core.models.Attachment
import network.ermis.core.models.ChannelCapabilities
import network.ermis.core.models.Command
import network.ermis.core.models.LinkPreview
import network.ermis.core.models.User
import network.ermis.ui.common.state.messages.MessageAction
import network.ermis.ui.common.state.messages.MessageMode

/**
 * Represents the state within the message input.
 *
 * @param inputValue The current text value that's within the input.
 * @param attachments The currently selected attachments.
 * @param action The currently active [MessageAction].
 * @param validationErrors The list of validation errors.
 * @param mentionSuggestions The list of users that can be used to autocomplete the mention.
 * @param commandSuggestions The list of commands to be displayed in the command suggestion popup.
 * @param linkPreviews The list of links found in [inputValue] to be previewed.
 * @param coolDownTime The amount of time left until the user is allowed to send the next message.
 * @param messageMode The message mode that's currently active.
 * @param alsoSendToChannel If the message will be shown in the channel after it is sent.
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 * For a full list @see [ChannelCapabilities].
 * @param hasCommands Whether there are any commands available.
 * @param currentUser The currently logged in user.
 * @param recording The current recording state.
 */
public data class MessageComposerState @JvmOverloads constructor(
    val inputValue: String = "",
    val attachments: List<Attachment> = emptyList(),
    val action: MessageAction? = null,
    val validationErrors: List<ValidationError> = emptyList(),
    val mentionSuggestions: List<User> = emptyList(),
    val commandSuggestions: List<Command> = emptyList(),
    val linkPreviews: List<LinkPreview> = emptyList(),
    val coolDownTime: Int = 0,
    val messageMode: MessageMode = MessageMode.Normal,
    val alsoSendToChannel: Boolean = false,
    val ownCapabilities: Set<String> = setOf(),
    val hasCommands: Boolean = false,
    val currentUser: User? = null,
    val recording: RecordingState = RecordingState.Idle,
)
