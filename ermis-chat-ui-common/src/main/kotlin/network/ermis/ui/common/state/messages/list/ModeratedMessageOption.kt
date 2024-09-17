package network.ermis.ui.common.state.messages.list

import network.ermis.ui.common.R

/**
 * Represents possible options user can take upon moderating a message.
 *
 * @property text The text to be shown for the action.
 */
public sealed class ModeratedMessageOption {
    public abstract val text: Int
}

/**
 * Prompts the user to send the message anyway if the message was flagged by moderation.
 */
public object SendAnyway : ModeratedMessageOption() {
    public override val text: Int = R.string.ermis_ui_moderation_dialog_send
    override fun toString(): String = "SendAnyway"
}

/**
 * Prompts the user to edit the message if the message was flagged by moderation.
 */
public object EditMessage : ModeratedMessageOption() {
    public override val text: Int = R.string.ermis_ui_moderation_dialog_edit
    override fun toString(): String = "EditMessage"
}

/**
 * Prompts the user to delete the message if the message was flagged by moderation.
 */
public object DeleteMessage : ModeratedMessageOption() {
    public override val text: Int = R.string.ermis_ui_moderation_dialog_delete
    override fun toString(): String = "DeleteMessage"
}

/**
 * Custom actions that you can define for moderated messages.
 */
public data class CustomModerationOption(
    override val text: Int,
    public val extraData: Map<String, Any> = emptyMap(),
) : ModeratedMessageOption()

/**
 * @return A list of [ModeratedMessageOption] to show to the user.
 */
public fun defaultMessageModerationOptions(): List<ModeratedMessageOption> = listOf(
    SendAnyway,
    EditMessage,
    DeleteMessage,
)
