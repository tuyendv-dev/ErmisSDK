package network.ermis.ui.view.messages.composer

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.MessageReplyStyle
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getColorStateListCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDimensionOrNull
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.getEnum
import network.ermis.ui.utils.extensions.use
import network.ermis.ui.view.messages.common.AudioRecordPlayerViewStyle
import network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogStyle
import network.ermis.ui.view.messages.composer.attachment.picker.PickerMediaMode

/**
 * Style for [MessageComposerView].
 *
 * @param backgroundColor The background color of the message composer.
 * @param buttonIconDrawableTintColor The tint applied to attachments, commands and send buttons.
 * @param dividerBackgroundDrawable The background of the divider at the top.
 * @param commandSuggestionsTitleText The text for the title at the top of the command suggestions dialog.
 * @param commandSuggestionsTitleTextStyle The text style for the title at the top of the command suggestions dialog.
 * @param commandSuggestionsTitleIconDrawable The icon for the title at the top of the command suggestions dialog.
 * @param commandSuggestionsTitleIconDrawableTintColor The tint applied to the icon for the title at the top
 * of the command suggestions dialog.
 * @param commandSuggestionsBackgroundColor The background color of the command suggestions dialog.
 * @param commandSuggestionItemCommandNameTextStyle The text style for the command name.
 * @param commandSuggestionItemCommandDescriptionText The command description template with two placeholders.
 * @param commandSuggestionItemCommandDescriptionTextStyle The text style for the command description.
 * @param mentionSuggestionsBackgroundColor The background color of the mention suggestions dialog.
 * @param mentionSuggestionItemIconDrawable The icon for each command icon in the suggestion list.
 * @param mentionSuggestionItemIconDrawableTintColor The tint applied to the icon for each command icon
 * in the suggestion list.
 * @param mentionSuggestionItemUsernameTextStyle The text style that will be used for the user name.
 * @param mentionSuggestionItemMentionText The mention template that will be used for the mention preview.
 * @param mentionSuggestionItemMentionTextStyle The text style that will be used for the mention preview.
 * @param messageInputCommandsHandlingEnabled If command suggestions are shown based on user input.
 * @param messageInputMentionsHandlingEnabled If mention suggestions are shown based on user input.
 * @param messageInputTextStyle The text style of the text input field.
 * @param messageInputBackgroundDrawable The background drawable of the text input field.
 * @param messageInputCursorDrawable The drawable for the cursor in the text input field.
 * @param messageInputScrollbarEnabled If the vertical scrollbar should be drawn or not.
 * @param messageInputScrollbarFadingEnabled If the vertical edges should be faded on scroll or not.
 * @param messageInputMaxLines The maximum number of message input lines.
 * @param messageInputCannotSendHintText The input hint text in case we can't send messages in this channel.
 * @param messageInputInputType The [InputType] to be applied to the message input edit text.
 * @param messageInputShowReplyView Whether to show the default reply view inside the message input or not.
 * @param messageInputVideoAttachmentIconDrawable Overlays a drawable above video attachments.
 * By default, used to display a play button.
 * @param messageInputVideoAttachmentIconDrawableTint Sets the tint of the drawable overlaid above video attachments.
 * @param messageInputVideoAttachmentIconBackgroundColor Sets the background color of the icon overlaid above video
 * attachments.
 * @param messageInputVideoAttachmentIconCornerRadius Sets the corner radius of the parent card containing the video
 * attachment icon.
 * @param messageInputVideoAttachmentIconElevation Sets the elevation of the icon overlaid above video attachments.
 * @param messageInputVideoAttachmentIconDrawablePaddingTop Sets the top padding between the video attachment drawable
 * and its parent card.
 * @param messageInputVideoAttachmentIconDrawablePaddingBottom Sets the bottom padding between the video attachment
 * drawable and its parent card.
 * @param messageInputVideoAttachmentIconDrawablePaddingStart Sets the start padding between the video attachment
 * drawable and its parent card.
 * @param messageInputVideoAttachmentIconDrawablePaddingEnd Sets the end padding between the video attachment drawable
 * and its parent card.
 * @param audioRecordingButtonVisible If the button to record audio is displayed.
 * @param audioRecordingButtonEnabled If the button to record audio is enabled.
 * @param audioRecordingButtonPreferred If the button to record audio is displayed over send button while input is
 * empty.
 * @param audioRecordingButtonIconDrawable The icon for the button to record audio.
 * @param audioRecordingButtonIconTintList The tint list for the button to record audio.
 * @param audioRecordingButtonWidth The width of the button to record audio.
 * @param audioRecordingButtonHeight The height of the button to record audio.
 * @param audioRecordingButtonPadding The padding of the button to record audio.
 * @param audioRecordingHoldToRecordText The info text that will be shown if touch event on audio button was too short.
 * @param audioRecordingHoldToRecordTextStyle The text style that will be used for the "hold to record" text.
 * @param audioRecordingHoldToRecordBackgroundDrawable The drawable will be used as a background for the "hold to
 * record" text.
 * @param audioRecordingHoldToRecordBackgroundDrawableTint The tint color will be used for background drawable of
 * the "hold to record" text.
 * @param audioRecordingSlideToCancelText The info text that will be shown while holding mic button.
 * @param audioRecordingSlideToCancelTextStyle The text style that will be used for the "slide to cancel" text.
 * @param audioRecordingSlideToCancelStartDrawable The icon that will be displayed in front of the
 * "slide to cancel" text.
 * @param audioRecordingSlideToCancelStartDrawableTint The tint color that will be used for the icon in front of the
 * "slide to cancel" text.
 * @param audioRecordingFloatingButtonIconDrawable The icon that will be displayed in inside the floating draggable
 * button while recording.
 * @param audioRecordingFloatingButtonIconDrawableTint The tint color that will be used for the the mic icon inside
 * the floating draggable button.
 * @param audioRecordingFloatingButtonBackgroundDrawable The background drawable that will be applied to the floating
 * draggable button while recording.
 * @param audioRecordingFloatingButtonBackgroundDrawableTint The tint color that will be used for the background
 * drawable in the floating draggable button.
 * @param audioRecordingFloatingLockIconDrawable The floating icon that will be displayed above floating button
 * while unlocked.
 * @param audioRecordingFloatingLockIconDrawableTint The tint color that will be used for the the lock icon.
 * @param audioRecordingFloatingLockedIconDrawable The floating icon that will be displayed above recording view
 * when locked.
 * @param audioRecordingFloatingLockedIconDrawableTint The tint color that will be used for the the locked icon.
 * @param audioRecordingWaveformColor The color of the waveform.
 * @param attachmentsButtonVisible If the button to pick attachments is displayed.
 * @param attachmentsButtonIconDrawable The icon for the attachments button.
 * @param attachmentsButtonIconTintList The tint list for the attachments button.
 * @param attachmentsButtonRippleColor Ripple color of the attachments button.
 * @param commandsButtonVisible If the button to select commands is displayed.
 * @param commandsButtonIconDrawable The icon for the commands button.
 * @param commandsButtonIconTintList The tint list for the commands button.
 * @param commandsButtonRippleColor Ripple color of the commands button.
 * @param alsoSendToChannelCheckboxVisible If the checkbox to send a message to the channel is displayed.
 * @param alsoSendToChannelCheckboxDrawable The drawable that will be used for the checkbox.
 * @param alsoSendToChannelCheckboxText The text that will be displayed next to the checkbox.
 * @param alsoSendToChannelCheckboxTextStyle The text style that will be used for the checkbox text.
 * @param editModeText The text for edit mode title.
 * @param editModeIconDrawable The icon displayed in top left corner when the user edits a message.
 * @param replyModeText The text for reply mode title.
 * @param replyModeIconDrawable The icon displayed in top left corner when the user replies to a message.
 * @param dismissModeIconDrawable The icon for the button that dismisses edit or reply mode.
 * @param sendMessageButtonEnabled If the button to send message is enabled.
 * @param sendMessageButtonIconDrawable The icon for the button to send message.
 * @param sendMessageButtonIconTintList The tint list for the button to send message.
 * @param cooldownTimerTextStyle The text style that will be used for cooldown timer.
 * @param cooldownTimerBackgroundDrawable Background drawable for cooldown timer.
 * @param messageReplyBackgroundColor Sets the background color of the quoted message bubble visible in the composer
 * when replying to a message.
 * @param messageReplyTextStyleMine  Sets the style of the text inside the quoted message bubble visible in the composer
 * when replying to a message. Applied to messages sent by the current user.
 * @param messageReplyMessageBackgroundStrokeColorMine Sets the color of the stroke of the quoted message bubble visible
 * in the composer when replying to a message. Applied to messages sent by the current user.
 * @param messageReplyMessageBackgroundStrokeWidthMine Sets the width of the stroke of the quoted message bubble visible
 * in the composer when replying to a message. Applied to messages sent by the current user.
 * @param messageReplyTextStyleTheirs  Sets the style of the text inside the quoted message bubble visible in the
 * composer when replying to a message. Applied to messages sent by users other than the currently logged in one.
 * @param messageReplyMessageBackgroundStrokeColorTheirs Sets the color of the stroke of the quoted message bubble
 * visible in the composer when replying to a message. Applied to messages sent by users other than the currently
 * logged in one.
 * @param messageReplyMessageBackgroundStrokeWidthTheirs Sets the width of the stroke of the quoted message bubble
 * visible in the composer when replying to a message. Applied to messages sent by users other than the currently
 * logged in one.
 */
public data class MessageComposerViewStyle(
    @ColorInt public val backgroundColor: Int,
    @Deprecated(
        message = "Use the " +
            "commandSuggestionsTitleIconDrawableTintColor" +
            "/mentionSuggestionItemIconDrawableTintColor " +
            "/attachmentsButtonIconTintList " +
            "/commandsButtonIconTintList " +
            "/sendMessageButtonIconTintList " +
            "property instead.",
        replaceWith = ReplaceWith("proper button tint property"),
        level = DeprecationLevel.WARNING,
    )
    @ColorInt public val buttonIconDrawableTintColor: Int?,
    public val dividerBackgroundDrawable: Drawable,
    // Command suggestions content
    public val commandSuggestionsTitleText: String,
    public val commandSuggestionsTitleTextStyle: TextStyle,
    public val commandSuggestionsTitleIconDrawable: Drawable,
    public val commandSuggestionsTitleIconDrawableTintColor: Int?,
    @ColorInt public val commandSuggestionsBackgroundColor: Int,
    public val commandSuggestionItemCommandNameTextStyle: TextStyle,
    public val commandSuggestionItemCommandDescriptionText: String,
    public val commandSuggestionItemCommandDescriptionTextStyle: TextStyle,
    // Mention suggestions content
    @ColorInt public val mentionSuggestionsBackgroundColor: Int,
    public val mentionSuggestionItemIconDrawable: Drawable,
    public val mentionSuggestionItemIconDrawableTintColor: Int?,
    public val mentionSuggestionItemUsernameTextStyle: TextStyle,
    public val mentionSuggestionItemMentionText: String,
    public val mentionSuggestionItemMentionTextStyle: TextStyle,
    // Center content
    public val messageInputCommandsHandlingEnabled: Boolean,
    public val messageInputMentionsHandlingEnabled: Boolean,
    public val messageInputTextStyle: TextStyle,
    public val messageInputBackgroundDrawable: Drawable,
    public val messageInputCursorDrawable: Drawable?,
    public val messageInputScrollbarEnabled: Boolean,
    public val messageInputScrollbarFadingEnabled: Boolean,
    public val messageInputMaxLines: Int,
    public val messageInputCannotSendHintText: String,
    public val messageInputInputType: Int,
    public val messageInputShowReplyView: Boolean,
    public val messageInputVideoAttachmentIconDrawable: Drawable,
    @ColorInt public val messageInputVideoAttachmentIconDrawableTint: Int?,
    @ColorInt public val messageInputVideoAttachmentIconBackgroundColor: Int?,
    public val messageInputVideoAttachmentIconCornerRadius: Float,
    public val messageInputVideoAttachmentIconElevation: Float,
    public val messageInputVideoAttachmentIconDrawablePaddingTop: Int,
    public val messageInputVideoAttachmentIconDrawablePaddingBottom: Int,
    public val messageInputVideoAttachmentIconDrawablePaddingStart: Int,
    public val messageInputVideoAttachmentIconDrawablePaddingEnd: Int,
    // Center overlap content
    public val audioRecordingHoldToRecordText: String,
    public val audioRecordingHoldToRecordTextStyle: TextStyle,
    public val audioRecordingHoldToRecordBackgroundDrawable: Drawable,
    @ColorInt public val audioRecordingHoldToRecordBackgroundDrawableTint: Int?,
    public val audioRecordingSlideToCancelText: String,
    public val audioRecordingSlideToCancelTextStyle: TextStyle,
    public val audioRecordingSlideToCancelStartDrawable: Drawable,
    @ColorInt public val audioRecordingSlideToCancelStartDrawableTint: Int?,
    public val audioRecordingFloatingButtonIconDrawable: Drawable,
    @ColorInt public val audioRecordingFloatingButtonIconDrawableTint: Int?,
    public val audioRecordingFloatingButtonBackgroundDrawable: Drawable,
    @ColorInt public val audioRecordingFloatingButtonBackgroundDrawableTint: Int?,
    public val audioRecordingFloatingLockIconDrawable: Drawable,
    @ColorInt public val audioRecordingFloatingLockIconDrawableTint: Int?,
    public val audioRecordingFloatingLockedIconDrawable: Drawable,
    @ColorInt public val audioRecordingFloatingLockedIconDrawableTint: Int?,
    @ColorInt public val audioRecordingWaveformColor: Int?,
    // Leading content
    public val attachmentsButtonVisible: Boolean,
    public val attachmentsButtonIconDrawable: Drawable,
    public val attachmentsButtonIconTintList: ColorStateList?,
    @ColorInt public val attachmentsButtonRippleColor: Int?,
    public val commandsButtonVisible: Boolean,
    public val commandsButtonIconDrawable: Drawable,
    public val commandsButtonIconTintList: ColorStateList?,
    @ColorInt public val commandsButtonRippleColor: Int?,
    // Footer content
    public val alsoSendToChannelCheckboxVisible: Boolean,
    public val alsoSendToChannelCheckboxDrawable: Drawable?,
    public val alsoSendToChannelCheckboxText: String,
    public val alsoSendToChannelCheckboxTextStyle: TextStyle,
    // Header content
    public val editModeText: String,
    public val editModeIconDrawable: Drawable,
    public val replyModeText: String,
    public val replyModeIconDrawable: Drawable,
    public val dismissModeIconDrawable: Drawable,
    // Trailing content
    public val sendMessageButtonEnabled: Boolean,
    public val sendMessageButtonIconDrawable: Drawable,
    public val sendMessageButtonIconTintList: ColorStateList?,
    @Px public val sendMessageButtonWidth: Int,
    @Px public val sendMessageButtonHeight: Int,
    @Px public val sendMessageButtonPadding: Int,
    public val audioRecordingButtonVisible: Boolean,
    public val audioRecordingButtonEnabled: Boolean,
    public val audioRecordingButtonPreferred: Boolean,
    public val audioRecordingButtonIconDrawable: Drawable,
    public val audioRecordingButtonIconTintList: ColorStateList?,
    @Px public val audioRecordingButtonWidth: Int,
    @Px public val audioRecordingButtonHeight: Int,
    @Px public val audioRecordingButtonPadding: Int,
    public val cooldownTimerTextStyle: TextStyle,
    public val cooldownTimerBackgroundDrawable: Drawable,
    // Message reply customization, by default belongs to center content as well
    @ColorInt public val messageReplyBackgroundColor: Int,
    public val messageReplyTextStyleMine: TextStyle,
    @ColorInt public val messageReplyMessageBackgroundStrokeColorMine: Int,
    @Px public val messageReplyMessageBackgroundStrokeWidthMine: Float,
    public val messageReplyTextStyleTheirs: TextStyle,
    @ColorInt public val messageReplyMessageBackgroundStrokeColorTheirs: Int,
    @Px public val messageReplyMessageBackgroundStrokeWidthTheirs: Float,
    public val attachmentsPickerDialogStyle: AttachmentsPickerDialogStyle,
    public val audioRecordPlayerViewStyle: AudioRecordPlayerViewStyle?,
) : ViewStyle {

    /**
     * Creates an instance of [MessageReplyStyle] from the parameters provided by [MessageComposerViewStyle].
     *
     * @return an instance of [MessageReplyStyle].
     */
    public fun toMessageReplyStyle(): MessageReplyStyle = MessageReplyStyle(
        messageBackgroundColorMine = messageReplyBackgroundColor,
        messageBackgroundColorTheirs = messageReplyBackgroundColor,
        linkBackgroundColorMine = messageReplyBackgroundColor,
        linkBackgroundColorTheirs = messageReplyBackgroundColor,
        textStyleMine = messageReplyTextStyleMine,
        textStyleTheirs = messageReplyTextStyleTheirs,
        linkStyleMine = messageReplyTextStyleMine,
        linkStyleTheirs = messageReplyTextStyleTheirs,
        messageStrokeColorMine = messageReplyMessageBackgroundStrokeColorMine,
        messageStrokeColorTheirs = messageReplyMessageBackgroundStrokeColorTheirs,
        messageStrokeWidthMine = messageReplyMessageBackgroundStrokeWidthMine,
        messageStrokeWidthTheirs = messageReplyMessageBackgroundStrokeWidthTheirs,
    )

    public companion object {

        @Suppress("MaxLineLength", "ComplexMethod", "LongMethod")
        internal operator fun invoke(context: Context, attrs: AttributeSet?): MessageComposerViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.MessageComposerView,
                R.attr.ermisUiMessageComposerViewStyle,
                R.style.ermisUi_MessageComposerView,
            ).use { a ->
                var backgroundColor: Int
                context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.background)).use {
                    backgroundColor = it.getColor(0, context.getColorCompat(R.color.ui_white))
                }

                val buttonIconDrawableTintColor = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerIconDrawableTintColor,
                )

                val dividerBackgroundDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerDividerBackgroundDrawable,
                ) ?: context.getDrawableCompat(R.drawable.divider)!!

                /**
                 * Command suggestions content
                 */
                val commandSuggestionsTitleText = a.getString(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsTitleText,
                ) ?: context.getString(R.string.ermis_ui_message_composer_instant_commands)

                val commandSuggestionsTitleTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsTitleTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsTitleTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsTitleTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsTitleTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsTitleTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val commandSuggestionsTitleIconDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsTitleIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_command_blue)!!

                val commandSuggestionsTitleIconDrawableTintColor = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemIconDrawableTintColor,
                )

                val commandSuggestionsBackgroundColor = a.getColor(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionsBackgroundColor,
                    context.getColorCompat(R.color.ui_white),
                )

                val commandSuggestionItemCommandNameTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandNameTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandNameTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandNameTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandNameTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandNameTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val commandSuggestionItemCommandDescriptionText = a.getString(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandDescriptionText,
                ) ?: context.getString(R.string.ermis_ui_message_composer_command_template)

                val commandSuggestionItemCommandDescriptionTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandDescriptionTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandDescriptionTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandDescriptionTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandDescriptionTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCommandSuggestionItemCommandDescriptionTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                /**
                 * Mention suggestions content
                 */
                val mentionSuggestionsBackgroundColor = a.getColor(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionsBackgroundColor,
                    context.getColorCompat(R.color.ui_white),
                )

                val mentionSuggestionItemIconDrawable: Drawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_mention)!!

                val mentionSuggestionItemIconDrawableTintColor = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemIconDrawableTintColor,
                )

                val mentionSuggestionItemUsernameTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemUsernameTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemUsernameTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemUsernameTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemUsernameTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemUsernameTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val mentionSuggestionItemMentionText = a.getString(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemMentionText,
                ) ?: context.getString(R.string.ermis_ui_message_composer_mention_template)

                val mentionSuggestionItemMentionTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemMentionTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemMentionTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemMentionTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemMentionTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMentionSuggestionItemMentionTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                /**
                 * Center content
                 */
                val messageInputCommandsHandlingEnabled = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandsHandlingEnabled,
                    true,
                )

                val messageInputMentionsHandlingEnabled = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMentionsHandlingEnabled,
                    true,
                )

                val messageInputTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputTextSize,
                        context.resources.getDimensionPixelSize(R.dimen.ui_text_size_input),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputTextStyle,
                        Typeface.NORMAL,
                    )
                    .hint(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputHintText,
                        context.getString(R.string.ermis_ui_message_composer_hint_normal),
                    )
                    .hintColor(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputHintColor,
                        context.getColorCompat(R.color.ui_text_color_hint),
                    )
                    .build()

                val messageInputBackgroundDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputBackgroundDrawable,
                ) ?: context.getDrawableCompat(R.drawable.shape_edit_text_round)!!

                val messageInputCursorDrawable: Drawable? = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputCursorDrawable,
                )

                val messageInputScrollbarEnabled = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerScrollbarEnabled,
                    false,
                )
                val messageInputScrollbarFadingEnabled = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerScrollbarFadingEnabled,
                    false,
                )

                val messageInputMaxLines = a.getInt(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputMaxLines,
                    7,
                )

                val messageInputCannotSendHintText = a.getString(
                    R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputCannotSendHintText,
                ) ?: context.getString(R.string.ermis_ui_message_composer_hint_cannot_send_message)

                /**
                 * Center overlap content
                 */
                val audioRecordingHoldToRecordText = a.getString(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordText,
                ) ?: context.getString(R.string.ermis_ui_message_composer_hold_to_record)
                val audioRecordingHoldToRecordTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordTextColor,
                        context.getColorCompat(R.color.ui_white_snow),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordTextStyle,
                        Typeface.BOLD,
                    )
                    .build()
                val audioRecordingHoldToRecordBackgroundDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordBackgroundDrawable,
                ) ?: context.getDrawableCompat(R.drawable.message_composer_audio_record_hold_background)!!

                val audioRecordingHoldToRecordBackgroundDrawableTint = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingHoldToRecordBackgroundDrawableTint,
                )

                val audioRecordingSlideToCancelText = a.getString(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelText,
                ) ?: context.getString(R.string.ermis_ui_message_composer_slide_to_cancel)
                val audioRecordingSlideToCancelTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()
                val audioRecordingSlideToCancelStartDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelStartDrawable,
                ) ?: context.getDrawableCompat(R.drawable.arrow_left)!!

                val audioRecordingSlideToCancelStartDrawableTint = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingSlideToCancelStartDrawableTint,
                )
                val audioRecordingFloatingButtonIconDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingButtonIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_mic)!!
                val audioRecordingFloatingButtonIconDrawableTint = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingButtonIconDrawableTint,
                )

                val audioRecordingFloatingButtonBackgroundDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingButtonBackgroundDrawable,
                ) ?: context.getDrawableCompat(R.drawable.message_composer_audio_record_mic_background)!!
                val audioRecordingFloatingButtonBackgroundDrawableTint = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingButtonBackgroundDrawableTint,
                )

                val audioRecordingFloatingLockIconDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingLockIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_mic_lock)!!
                val audioRecordingFloatingLockIconDrawableTint = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingLockIconDrawableTint,
                )

                val audioRecordingFloatingLockedIconDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingLockedIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_mic_locked)!!
                val audioRecordingFloatingLockedIconDrawableTint = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingFloatingLockedIconDrawableTint,
                )
                val audioRecordingWaveformColor = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingWaveformColor,
                )

                /**
                 * Leading content
                 */
                val attachmentsButtonVisible = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsButtonVisible,
                    true,
                )

                val attachmentsButtonIconDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsButtonIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_attach)!!

                val attachmentsButtonIconTintList = a.getColorStateListCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsButtonIconTintList,
                )

                val attachmentsButtonRippleColor = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsButtonRippleColor,
                )

                val commandsButtonVisible = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandsButtonVisible,
                    true,
                )

                val commandsButtonIconDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandsButtonIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_command)!!

                val commandsButtonIconTintList = a.getColorStateListCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandsButtonIconTintList,
                )

                val commandsButtonRippleColor = a.getColorOrNull(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCommandsButtonRippleColor,
                )

                /**
                 * Footer content
                 */
                val alsoSendToChannelCheckboxVisible = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxVisible,
                    true,
                )

                val alsoSendToChannelCheckboxDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxDrawable,
                )

                val alsoSendToChannelCheckboxText: String = a.getText(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxText,
                )?.toString() ?: context.getString(R.string.ermis_ui_message_composer_send_to_channel)

                val alsoSendToChannelCheckboxTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAlsoSendToChannelCheckboxTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                /**
                 * Header content
                 */
                val editModeText: String = a.getText(
                    R.styleable.MessageComposerView_ermisUiMessageComposerEditModeText,
                )?.toString() ?: context.getString(R.string.ermis_ui_message_composer_mode_edit)

                val editModeIconDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerEditModeIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_edit)!!

                val replyModeText: String = a.getText(
                    R.styleable.MessageComposerView_ermisUiMessageComposerReplyModeText,
                )?.toString() ?: context.getString(R.string.ermis_ui_message_composer_mode_reply)

                val replyModeIconDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerReplyModeIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_arrow_curve_left_grey)!!

                val dismissModeIconDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerDismissModeIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_clear)!!

                /**
                 * Trailing content
                 */
                val sendMessageButtonEnabled = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerSendMessageButtonEnabled,
                    true,
                )

                val sendMessageButtonIconDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerSendMessageButtonIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_send_message)!!

                val sendMessageButtonIconTintList = a.getColorStateListCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerSendMessageButtonIconTintList,
                )

                val sendMessageButtonWidth: Int =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerSendMessageButtonWidth,
                        DEFAULT_TRAILING_BUTTON_SIZE.dpToPx(),
                    )
                val sendMessageButtonHeight: Int =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerSendMessageButtonHeight,
                        DEFAULT_TRAILING_BUTTON_SIZE.dpToPx(),
                    )
                val sendMessageButtonPadding: Int =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerSendMessageButtonPadding,
                        DEFAULT_TRAILING_BUTTON_PADDING.dpToPx(),
                    )

                val audioRecordingButtonEnabled = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonEnabled,
                    true,
                )
                val audioRecordingButtonVisible = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonVisible,
                    false,
                )
                val audioRecordingButtonPreferred = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonPreferred,
                    false,
                )
                val audioRecordingButtonIconDrawable = a.getDrawableCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonIconDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_mic)!!

                val audioRecordingButtonIconTintList = a.getColorStateListCompat(
                    context,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonIconTintList,
                )

                val audioRecordingButtonWidth: Int =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonWidth,
                        DEFAULT_TRAILING_BUTTON_SIZE.dpToPx(),
                    )
                val audioRecordingButtonHeight: Int =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonHeight,
                        DEFAULT_TRAILING_BUTTON_SIZE.dpToPx(),
                    )
                val audioRecordingButtonPadding: Int =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordingButtonPadding,
                        DEFAULT_TRAILING_BUTTON_PADDING.dpToPx(),
                    )

                val cooldownTimerTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCooldownTimerTextSize,
                        context.getDimension(R.dimen.ui_text_large),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCooldownTimerTextColor,
                        context.getColorCompat(R.color.ui_literal_white),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCooldownTimerTextFontAssets,
                        R.styleable.MessageComposerView_ermisUiMessageComposerCooldownTimerTextFont,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerCooldownTimerTextStyle,
                        Typeface.BOLD,
                    )
                    .build()

                val cooldownTimerBackgroundDrawable = a.getDrawable(
                    R.styleable.MessageComposerView_ermisUiMessageComposerCooldownTimerBackgroundDrawable,
                ) ?: context.getDrawableCompat(R.drawable.cooldown_badge_background)!!

                val messageInputInputType = a.getInt(
                    R.styleable.MessageComposerView_android_inputType,
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
                )

                val messageInputShowReplyView = a.getBoolean(
                    R.styleable.MessageComposerView_ermisUiMessageComposerShowMessageReplyView,
                    true,
                )

                val messageInputVideoAttachmentIconDrawable =
                    a.getDrawable(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconDrawable,
                    )
                        ?: ContextCompat.getDrawable(context, R.drawable.ic_play)!!

                val messageInputVideoAttachmentIconDrawableTint =
                    a.getColorOrNull(R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconDrawableTint)

                val messageInputVideoAttachmentIconBackgroundColor =
                    a.getColorOrNull(R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconBackgroundColor)

                val messageInputVideoAttachmentIconCornerRadius =
                    a.getDimension(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoCornerRadius,
                        20.dpToPxPrecise(),
                    )

                val messageInputVideoAttachmentIconElevation =
                    a.getDimension(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconElevation,
                        0f,
                    )

                val messageInputVideoAttachmentIconDrawablePaddingTop =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconDrawablePaddingTop,
                        0,
                    )

                val messageInputVideoAttachmentIconDrawablePaddingBottom =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconDrawablePaddingBottom,
                        0,
                    )

                val messageInputVideoAttachmentIconDrawablePaddingStart =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconDrawablePaddingStart,
                        0,
                    )

                val messageInputVideoAttachmentIconDrawablePaddingEnd =
                    a.getDimensionPixelSize(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconDrawablePaddingEnd,
                        0,
                    )

                val messageInputVideoAttachmentIconDrawablePadding =
                    a.getDimensionOrNull(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageInputVideoAttachmentIconDrawablePadding,
                    )?.toInt()

                val mediumTypeface = ResourcesCompat.getFont(context, R.font.roboto_medium) ?: Typeface.DEFAULT

                val messageReplyBackgroundColor: Int =
                    a.getColor(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyBackgroundColor,
                        context.getColorCompat(R.color.ui_white),
                    )

                val messageReplyTextStyleMine: TextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextSizeMine,
                        context.getDimension(MessageReplyStyle.DEFAULT_TEXT_SIZE),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextColorMine,
                        context.getColorCompat(MessageReplyStyle.DEFAULT_TEXT_COLOR),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextFontAssetsMine,
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextStyleMine,
                        mediumTypeface,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextStyleMine,
                        MessageReplyStyle.DEFAULT_TEXT_STYLE,
                    )
                    .build()

                val messageReplyMessageBackgroundStrokeColorMine: Int =
                    a.getColor(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyStrokeColorMine,
                        context.getColorCompat(R.color.ui_grey_gainsboro),
                    )

                val messageReplyMessageBackgroundStrokeWidthMine: Float =
                    a.getDimension(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyStrokeWidthMine,
                        DEFAULT_MESSAGE_REPLY_BACKGROUND_STROKE_WIDTH,
                    )

                val messageReplyTextStyleTheirs: TextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextSizeTheirs,
                        context.getDimension(MessageReplyStyle.DEFAULT_TEXT_SIZE),
                    )
                    .color(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextColorTheirs,
                        context.getColorCompat(MessageReplyStyle.DEFAULT_TEXT_COLOR),
                    )
                    .font(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextFontAssetsTheirs,
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextStyleTheirs,
                        mediumTypeface,
                    )
                    .style(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyTextStyleTheirs,
                        MessageReplyStyle.DEFAULT_TEXT_STYLE,
                    )
                    .build()

                val messageReplyMessageBackgroundStrokeColorTheirs: Int =
                    a.getColor(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyStrokeColorTheirs,
                        context.getColorCompat(R.color.ui_grey_gainsboro),
                    )

                val messageReplyMessageBackgroundStrokeWidthTheirs: Float =
                    a.getDimension(
                        R.styleable.MessageComposerView_ermisUiMessageComposerMessageReplyStrokeWidthTheirs,
                        DEFAULT_MESSAGE_REPLY_BACKGROUND_STROKE_WIDTH,
                    )

                var playerViewStyle: AudioRecordPlayerViewStyle? = null
                val playerViewStyleResId: Int = a.getResourceId(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAudioRecordPlayerViewStyle,
                    R.style.ermisUi_AudioRecordPlayerView,
                )
                if (playerViewStyleResId != R.style.ermisUi_AudioRecordPlayerView) {
                    context.obtainStyledAttributes(playerViewStyleResId, R.styleable.AudioRecordPlayerView).use {
                        playerViewStyle = AudioRecordPlayerViewStyle(
                            context = context, attributes = it,
                        )
                    }
                }

                return MessageComposerViewStyle(
                    backgroundColor = backgroundColor,
                    buttonIconDrawableTintColor = buttonIconDrawableTintColor,
                    dividerBackgroundDrawable = dividerBackgroundDrawable,
                    // Command suggestions content
                    commandSuggestionsTitleText = commandSuggestionsTitleText,
                    commandSuggestionsTitleTextStyle = commandSuggestionsTitleTextStyle,
                    commandSuggestionsTitleIconDrawable = commandSuggestionsTitleIconDrawable,
                    commandSuggestionsTitleIconDrawableTintColor = commandSuggestionsTitleIconDrawableTintColor,
                    commandSuggestionsBackgroundColor = commandSuggestionsBackgroundColor,
                    commandSuggestionItemCommandNameTextStyle = commandSuggestionItemCommandNameTextStyle,
                    commandSuggestionItemCommandDescriptionText = commandSuggestionItemCommandDescriptionText,
                    commandSuggestionItemCommandDescriptionTextStyle = commandSuggestionItemCommandDescriptionTextStyle,
                    // Mention suggestions content
                    mentionSuggestionsBackgroundColor = mentionSuggestionsBackgroundColor,
                    mentionSuggestionItemIconDrawable = mentionSuggestionItemIconDrawable,
                    mentionSuggestionItemIconDrawableTintColor = mentionSuggestionItemIconDrawableTintColor,
                    mentionSuggestionItemUsernameTextStyle = mentionSuggestionItemUsernameTextStyle,
                    mentionSuggestionItemMentionText = mentionSuggestionItemMentionText,
                    mentionSuggestionItemMentionTextStyle = mentionSuggestionItemMentionTextStyle,
                    // Center content
                    messageInputCommandsHandlingEnabled = messageInputCommandsHandlingEnabled,
                    messageInputMentionsHandlingEnabled = messageInputMentionsHandlingEnabled,
                    messageInputTextStyle = messageInputTextStyle,
                    messageInputBackgroundDrawable = messageInputBackgroundDrawable,
                    messageInputCursorDrawable = messageInputCursorDrawable,
                    messageInputScrollbarEnabled = messageInputScrollbarEnabled,
                    messageInputScrollbarFadingEnabled = messageInputScrollbarFadingEnabled,
                    messageInputMaxLines = messageInputMaxLines,
                    messageInputCannotSendHintText = messageInputCannotSendHintText,
                    messageInputInputType = messageInputInputType,
                    messageInputShowReplyView = messageInputShowReplyView,
                    messageInputVideoAttachmentIconDrawable = messageInputVideoAttachmentIconDrawable,
                    messageInputVideoAttachmentIconDrawableTint = messageInputVideoAttachmentIconDrawableTint,
                    messageInputVideoAttachmentIconBackgroundColor = messageInputVideoAttachmentIconBackgroundColor,
                    messageInputVideoAttachmentIconCornerRadius = messageInputVideoAttachmentIconCornerRadius,
                    messageInputVideoAttachmentIconElevation = messageInputVideoAttachmentIconElevation,
                    messageInputVideoAttachmentIconDrawablePaddingTop = messageInputVideoAttachmentIconDrawablePadding
                        ?: messageInputVideoAttachmentIconDrawablePaddingTop,
                    messageInputVideoAttachmentIconDrawablePaddingBottom = messageInputVideoAttachmentIconDrawablePadding
                        ?: messageInputVideoAttachmentIconDrawablePaddingBottom,
                    messageInputVideoAttachmentIconDrawablePaddingStart = messageInputVideoAttachmentIconDrawablePadding
                        ?: messageInputVideoAttachmentIconDrawablePaddingStart,
                    messageInputVideoAttachmentIconDrawablePaddingEnd = messageInputVideoAttachmentIconDrawablePadding
                        ?: messageInputVideoAttachmentIconDrawablePaddingEnd,
                    // Center overlap content
                    audioRecordingHoldToRecordText = audioRecordingHoldToRecordText,
                    audioRecordingHoldToRecordTextStyle = audioRecordingHoldToRecordTextStyle,
                    audioRecordingHoldToRecordBackgroundDrawable = audioRecordingHoldToRecordBackgroundDrawable,
                    audioRecordingHoldToRecordBackgroundDrawableTint = audioRecordingHoldToRecordBackgroundDrawableTint,
                    audioRecordingSlideToCancelText = audioRecordingSlideToCancelText,
                    audioRecordingSlideToCancelTextStyle = audioRecordingSlideToCancelTextStyle,
                    audioRecordingSlideToCancelStartDrawable = audioRecordingSlideToCancelStartDrawable,
                    audioRecordingSlideToCancelStartDrawableTint = audioRecordingSlideToCancelStartDrawableTint,
                    audioRecordingFloatingButtonIconDrawable = audioRecordingFloatingButtonIconDrawable,
                    audioRecordingFloatingButtonIconDrawableTint = audioRecordingFloatingButtonIconDrawableTint,
                    audioRecordingFloatingButtonBackgroundDrawable = audioRecordingFloatingButtonBackgroundDrawable,
                    audioRecordingFloatingButtonBackgroundDrawableTint = audioRecordingFloatingButtonBackgroundDrawableTint,
                    audioRecordingFloatingLockIconDrawable = audioRecordingFloatingLockIconDrawable,
                    audioRecordingFloatingLockIconDrawableTint = audioRecordingFloatingLockIconDrawableTint,
                    audioRecordingFloatingLockedIconDrawable = audioRecordingFloatingLockedIconDrawable,
                    audioRecordingFloatingLockedIconDrawableTint = audioRecordingFloatingLockedIconDrawableTint,
                    audioRecordingWaveformColor = audioRecordingWaveformColor,
                    // Leading content
                    attachmentsButtonVisible = attachmentsButtonVisible,
                    attachmentsButtonIconDrawable = attachmentsButtonIconDrawable,
                    attachmentsButtonIconTintList = attachmentsButtonIconTintList,
                    attachmentsButtonRippleColor = attachmentsButtonRippleColor,
                    commandsButtonVisible = commandsButtonVisible,
                    commandsButtonIconDrawable = commandsButtonIconDrawable,
                    commandsButtonIconTintList = commandsButtonIconTintList,
                    commandsButtonRippleColor = commandsButtonRippleColor,
                    // Footer content
                    alsoSendToChannelCheckboxVisible = alsoSendToChannelCheckboxVisible,
                    alsoSendToChannelCheckboxDrawable = alsoSendToChannelCheckboxDrawable,
                    alsoSendToChannelCheckboxText = alsoSendToChannelCheckboxText,
                    alsoSendToChannelCheckboxTextStyle = alsoSendToChannelCheckboxTextStyle,
                    // Header content
                    editModeText = editModeText,
                    editModeIconDrawable = editModeIconDrawable,
                    replyModeText = replyModeText,
                    replyModeIconDrawable = replyModeIconDrawable,
                    dismissModeIconDrawable = dismissModeIconDrawable,
                    // Trailing content
                    sendMessageButtonEnabled = sendMessageButtonEnabled,
                    sendMessageButtonIconDrawable = sendMessageButtonIconDrawable,
                    sendMessageButtonIconTintList = sendMessageButtonIconTintList,
                    sendMessageButtonWidth = sendMessageButtonWidth,
                    sendMessageButtonHeight = sendMessageButtonHeight,
                    sendMessageButtonPadding = sendMessageButtonPadding,
                    audioRecordingButtonEnabled = audioRecordingButtonEnabled,
                    audioRecordingButtonVisible = audioRecordingButtonVisible,
                    audioRecordingButtonPreferred = audioRecordingButtonPreferred,
                    audioRecordingButtonIconDrawable = audioRecordingButtonIconDrawable,
                    audioRecordingButtonIconTintList = audioRecordingButtonIconTintList,
                    audioRecordingButtonWidth = audioRecordingButtonWidth,
                    audioRecordingButtonHeight = audioRecordingButtonHeight,
                    audioRecordingButtonPadding = audioRecordingButtonPadding,
                    cooldownTimerTextStyle = cooldownTimerTextStyle,
                    cooldownTimerBackgroundDrawable = cooldownTimerBackgroundDrawable,
                    messageReplyBackgroundColor = messageReplyBackgroundColor,
                    messageReplyTextStyleMine = messageReplyTextStyleMine,
                    messageReplyMessageBackgroundStrokeColorMine = messageReplyMessageBackgroundStrokeColorMine,
                    messageReplyMessageBackgroundStrokeWidthMine = messageReplyMessageBackgroundStrokeWidthMine,
                    messageReplyTextStyleTheirs = messageReplyTextStyleTheirs,
                    messageReplyMessageBackgroundStrokeColorTheirs = messageReplyMessageBackgroundStrokeColorTheirs,
                    messageReplyMessageBackgroundStrokeWidthTheirs = messageReplyMessageBackgroundStrokeWidthTheirs,
                    attachmentsPickerDialogStyle = createAttachmentPickerDialogStyle(context, a),
                    audioRecordPlayerViewStyle = playerViewStyle,
                ).let(TransformStyle.messageComposerStyleTransformer::transform)
            }
        }

        @Suppress("MaxLineLength", "LongMethod", "ComplexMethod")
        private fun createAttachmentPickerDialogStyle(context: Context, a: TypedArray): AttachmentsPickerDialogStyle {
            val attachmentsPickerBackgroundColor = a.getColor(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerBackgroundColor,
                context.getColorCompat(R.color.ui_white_smoke),
            )

            val allowAccessButtonTextStyle = TextStyle.Builder(a)
                .size(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessButtonTextSize,
                    context.getDimension(R.dimen.ui_text_large),
                )
                .color(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessButtonTextColor,
                    context.getColorCompat(R.color.ui_accent_blue),
                )
                .font(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessButtonTextFontAssets,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessButtonTextFont,
                )
                .style(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessButtonTextStyle,
                    Typeface.BOLD,
                )
                .build()

            val submitAttachmentsButtonIconDrawable = a.getDrawableCompat(
                context,
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerSubmitAttachmentsButtonIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.ic_next)!!

            val attachmentTabToggleButtonStateList = a.getColorStateList(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAttachmentTabToggleButtonStateList,
            ) ?: context.getColorStateListCompat(R.color.ermis_ui_attachment_tab_button)

            /**
             * Media attachments tab
             */
            val mediaAttachmentsTabEnabled = a.getBoolean(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentsTabEnabled,
                true,
            )

            val mediaAttachmentsTabIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentsTabIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.attachment_permission_media)!!

            val allowAccessToMediaButtonText = a.getText(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessToMediaButtonText,
            )?.toString() ?: context.getString(R.string.ermis_ui_message_composer_gallery_access)

            val allowAccessToMediaIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessToMediaIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.attachment_permission_media)!!

            val videoLengthTextVisible = a.getBoolean(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoLengthTextVisible,
                true,
            )

            val videoLengthTextStyle = TextStyle.Builder(a)
                .size(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoLengthTextSize,
                    context.getDimension(R.dimen.ui_text_small),
                )
                .color(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoLengthTextColor,
                    context.getColorCompat(R.color.ui_black),
                )
                .font(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoLengthTextFontAssets,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoLengthTextFont,
                )
                .style(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoLengthTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val videoIconVisible = a.getBoolean(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoIconVisible,
                true,
            )

            val videoIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.ic_video)!!

            val videoIconDrawableTint =
                a.getColorOrNull(R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerVideoIconDrawableTint)

            val mediaAttachmentNoMediaText = a.getString(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentNoMediaText,
            ) ?: context.getString(R.string.ermis_ui_message_composer_no_files)

            val mediaAttachmentNoMediaTextStyle = TextStyle.Builder(a)
                .size(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentNoMediaTextSize,
                    context.getDimension(R.dimen.ui_text_large),
                )
                .color(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentNoMediaTextColor,
                    context.getColorCompat(R.color.ui_text_color_primary),
                )
                .font(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentNoMediaTextFontAssets,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentNoMediaTextFont,
                )
                .style(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaAttachmentNoMediaTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            /**
             * File attachments tab
             */
            val fileAttachmentsTabEnabled = a.getBoolean(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsTabEnabled,
                true,
            )

            val fileAttachmentsTabIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsTabIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.attachment_permission_file)!!

            val allowAccessToFilesButtonText = a.getText(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessToFilesButtonText,
            )?.toString() ?: context.getString(R.string.ermis_ui_message_composer_files_access)

            val allowAccessToFilesIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessToFilesIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.attachment_permission_file)!!

            val recentFilesText = a.getText(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerRecentFilesText,
            )?.toString() ?: context.getString(R.string.ermis_ui_message_composer_recent_files)

            val recentFilesTextStyle = TextStyle.Builder(a)
                .size(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerRecentFilesTextSize,
                    context.getDimension(R.dimen.ui_spacing_medium),
                )
                .color(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerRecentFilesTextColor,
                    context.getColorCompat(R.color.ui_black),
                )
                .font(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerRecentFilesTextFontAssets,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerRecentFilesTextFont,
                )
                .style(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerRecentFilesTextStyle,
                    Typeface.BOLD,
                )
                .build()

            val fileManagerIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileManagerIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.ic_file_manager)!!

            val fileAttachmentsNoFilesText = a.getString(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsNoFilesText,
            ) ?: context.getString(R.string.ermis_ui_message_composer_no_files)

            val fileAttachmentsNoFilesTextStyle = TextStyle.Builder(a)
                .size(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsNoFilesTextSize,
                    context.getDimension(R.dimen.ui_text_large),
                )
                .color(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsNoFilesTextColor,
                    context.getColorCompat(R.color.ui_text_color_primary),
                )
                .font(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsNoFilesTextFontAssets,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsNoFilesTextFont,
                )
                .style(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentsNoFilesTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val fileAttachmentItemNameTextStyle = TextStyle.Builder(a)
                .size(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemNameTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )
                .color(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemNameTextColor,
                    context.getColorCompat(R.color.ui_black),
                )
                .font(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemNameTextFontAssets,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemNameTextFont,
                )
                .style(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemNameTextStyle,
                    Typeface.BOLD,
                )
                .build()

            val fileAttachmentItemSizeTextStyle = TextStyle.Builder(a)
                .size(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemSizeTextSize,
                    context.getDimension(R.dimen.ui_text_small),
                )
                .color(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemSizeTextColor,
                    context.getColorCompat(R.color.ui_text_color_secondary),
                )
                .font(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemSizeTextFontAssets,
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemSizeTextFont,
                )
                .style(
                    R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemSizeTextStyle,
                    Typeface.BOLD,
                )
                .build()

            val fileAttachmentItemCheckboxSelectedDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemCheckboxSelectedDrawable,
            ) ?: context.getDrawableCompat(R.drawable.circle_blue)!!

            val fileAttachmentItemCheckboxDeselectedDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemCheckboxDeselectedDrawable,
            ) ?: context.getDrawableCompat(R.drawable.ic_file_manager)!!

            val fileAttachmentItemCheckboxTextColor = a.getColor(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerFileAttachmentItemCheckboxTextColor,
                context.getColorCompat(R.color.ui_literal_white),
            )

            /**
             * Camera attachments tab
             */
            val cameraAttachmentsTabEnabled = a.getBoolean(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerCameraAttachmentsTabEnabled,
                true,
            )

            val cameraAttachmentsTabIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerCameraAttachmentsTabIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.attachment_permission_camera)!!

            val allowAccessToCameraButtonText = a.getText(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessToCameraButtonText,
            )?.toString() ?: context.getString(R.string.ermis_ui_message_composer_camera_access)

            val allowAccessToCameraIconDrawable = a.getDrawable(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerAllowAccessToCameraIconDrawable,
            ) ?: context.getDrawableCompat(R.drawable.attachment_permission_camera)!!

            val pickerMediaMode = a.getEnum(
                R.styleable.MessageComposerView_ermisUiMessageComposerAttachmentsPickerMediaMode,
                PickerMediaMode.PHOTO_AND_VIDEO,
            )

            return AttachmentsPickerDialogStyle(
                attachmentsPickerBackgroundColor = attachmentsPickerBackgroundColor,
                allowAccessButtonTextStyle = allowAccessButtonTextStyle,
                submitAttachmentsButtonIconDrawable = submitAttachmentsButtonIconDrawable,
                attachmentTabToggleButtonStateList = attachmentTabToggleButtonStateList,
                // Media attachments tab
                mediaAttachmentsTabEnabled = mediaAttachmentsTabEnabled,
                mediaAttachmentsTabIconDrawable = mediaAttachmentsTabIconDrawable,
                allowAccessToMediaButtonText = allowAccessToMediaButtonText,
                allowAccessToMediaIconDrawable = allowAccessToMediaIconDrawable,
                videoLengthTextVisible = videoLengthTextVisible,
                videoLengthTextStyle = videoLengthTextStyle,
                videoIconVisible = videoIconVisible,
                videoIconDrawable = videoIconDrawable,
                videoIconDrawableTint = videoIconDrawableTint,
                mediaAttachmentNoMediaText = mediaAttachmentNoMediaText,
                mediaAttachmentNoMediaTextStyle = mediaAttachmentNoMediaTextStyle,
                // File attachments tab
                fileAttachmentsTabEnabled = fileAttachmentsTabEnabled,
                fileAttachmentsTabIconDrawable = fileAttachmentsTabIconDrawable,
                allowAccessToFilesButtonText = allowAccessToFilesButtonText,
                allowAccessToFilesIconDrawable = allowAccessToFilesIconDrawable,
                recentFilesText = recentFilesText,
                recentFilesTextStyle = recentFilesTextStyle,
                fileManagerIconDrawable = fileManagerIconDrawable,
                fileAttachmentsNoFilesText = fileAttachmentsNoFilesText,
                fileAttachmentsNoFilesTextStyle = fileAttachmentsNoFilesTextStyle,
                fileAttachmentItemNameTextStyle = fileAttachmentItemNameTextStyle,
                fileAttachmentItemSizeTextStyle = fileAttachmentItemSizeTextStyle,
                fileAttachmentItemCheckboxSelectedDrawable = fileAttachmentItemCheckboxSelectedDrawable,
                fileAttachmentItemCheckboxDeselectedDrawable = fileAttachmentItemCheckboxDeselectedDrawable,
                fileAttachmentItemCheckboxTextColor = fileAttachmentItemCheckboxTextColor,
                // Camera attachments tab
                cameraAttachmentsTabEnabled = cameraAttachmentsTabEnabled,
                cameraAttachmentsTabIconDrawable = cameraAttachmentsTabIconDrawable,
                allowAccessToCameraButtonText = allowAccessToCameraButtonText,
                allowAccessToCameraIconDrawable = allowAccessToCameraIconDrawable,
                pickerMediaMode = pickerMediaMode,
            ).let(TransformStyle.attachmentsPickerStyleTransformer::transform)
        }

        private const val DEFAULT_MESSAGE_REPLY_BACKGROUND_STROKE_WIDTH = 4F
        private const val DEFAULT_TRAILING_BUTTON_SIZE = 32
        private const val DEFAULT_TRAILING_BUTTON_PADDING = 4
    }
}