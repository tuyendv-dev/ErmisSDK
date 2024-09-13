
package network.ermis.ui.view.messages.composer.content

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import network.ermis.core.models.ChannelCapabilities
import network.ermis.ui.components.R
import network.ermis.ui.utils.getColorList
import network.ermis.ui.common.state.messages.Edit
import network.ermis.ui.common.state.messages.composer.MessageComposerState
import network.ermis.ui.common.state.messages.composer.RecordingState
import network.ermis.ui.components.databinding.MessageComposerDefaultLeadingContentBinding
import network.ermis.ui.view.messages.composer.MessageComposerContext
import network.ermis.ui.view.messages.composer.MessageComposerView
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.setBorderlessRipple
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * Represents the content shown at the start of [MessageComposerView].
 */
public interface MessageComposerLeadingContent : MessageComposerContent {
    /**
     * Click listener for the pick attachments button.
     */
    public var attachmentsButtonClickListener: (() -> Unit)?

    /**
     * Click listener for the pick commands button.
     */
    public var commandsButtonClickListener: (() -> Unit)?
}

/**
 * Represents the default content shown at the start of [MessageComposerView].
 */
public open class DefaultMessageComposerLeadingContent : FrameLayout, MessageComposerLeadingContent {
    /**
     * Generated binding class for the XML layout.
     */
    protected lateinit var binding: MessageComposerDefaultLeadingContentBinding

    /**
     * The style for [MessageComposerView].
     */
    protected lateinit var style: MessageComposerViewStyle

    /**
     * Click listener for the pick attachments button.
     */
    public override var attachmentsButtonClickListener: (() -> Unit)? = null

    /**
     * Click listener for the pick commands button.
     */
    public override var commandsButtonClickListener: (() -> Unit)? = null

    public constructor(context: Context) : this(context, null)

    public constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init()
    }

    /**
     * Initializes the initial layout of the view.
     */
    private fun init() {
        binding = MessageComposerDefaultLeadingContentBinding.inflate(streamThemeInflater, this)
        binding.attachmentsButton.setOnClickListener { attachmentsButtonClickListener?.invoke() }
        binding.commandsButton.setOnClickListener { commandsButtonClickListener?.invoke() }
    }

    /**
     * Initializes the content view with [MessageComposerContext].
     *
     * @param messageComposerContext The context of this [MessageComposerView].
     */
    override fun attachContext(messageComposerContext: MessageComposerContext) {
        this.style = messageComposerContext.style

        binding.commandsButton.isVisible = style.commandsButtonVisible
        binding.attachmentsButton.setImageDrawable(style.attachmentsButtonIconDrawable)
        binding.attachmentsButton.setBorderlessRipple(style.attachmentsButtonRippleColor)

        binding.attachmentsButton.isVisible = style.attachmentsButtonVisible
        binding.commandsButton.setImageDrawable(style.commandsButtonIconDrawable)
        binding.commandsButton.setBorderlessRipple(style.commandsButtonRippleColor)

        val getStateListColor = { tintColor: Int ->
            getColorList(
                normalColor = context.getColorCompat(R.color.ui_grey),
                selectedColor = tintColor,
                disabledColor = context.getColorCompat(R.color.ui_grey_gainsboro),
            )
        }

        style.attachmentsButtonIconTintList?.also { tintList ->
            binding.attachmentsButton.imageTintList = tintList
        } ?: style.buttonIconDrawableTintColor?.let { tintColor ->
            binding.attachmentsButton.imageTintList = getStateListColor(tintColor)
        }

        style.commandsButtonIconTintList?.also { tintList ->
            binding.commandsButton.imageTintList = tintList
        } ?: style.buttonIconDrawableTintColor?.let { tintColor ->
            binding.commandsButton.imageTintList = getStateListColor(tintColor)
        }
    }

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    override fun renderState(state: MessageComposerState) {
        val canSendMessage = state.ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
        val canUploadFile = state.ownCapabilities.contains(ChannelCapabilities.UPLOAD_FILE)
        val hasTextInput = state.inputValue.isNotEmpty()
        val hasAttachments = state.attachments.isNotEmpty()
        val hasCommandInput = state.inputValue.startsWith("/")
        val hasCommandSuggestions = state.commandSuggestions.isNotEmpty()
        val hasMentionSuggestions = state.mentionSuggestions.isNotEmpty()
        val isInEditMode = state.action is Edit
        val hasCommands = state.hasCommands
        val noRecording = state.recording is RecordingState.Idle

        binding.root.isVisible = noRecording
        // isVisible = noRecording
        binding.attachmentsButton.isEnabled = !hasCommandInput && !hasCommandSuggestions && !hasMentionSuggestions
        binding.attachmentsButton.isVisible = style.attachmentsButtonVisible && canSendMessage && canUploadFile && !isInEditMode

        binding.commandsButton.isEnabled = !hasTextInput && !hasAttachments
        binding.commandsButton.isVisible = style.commandsButtonVisible && canSendMessage && !isInEditMode && hasCommands
        binding.commandsButton.isSelected = hasCommandSuggestions
    }
}
