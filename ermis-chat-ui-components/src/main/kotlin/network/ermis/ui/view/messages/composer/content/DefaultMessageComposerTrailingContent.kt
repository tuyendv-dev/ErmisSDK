
package network.ermis.ui.view.messages.composer.content

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import network.ermis.client.ErmisClient
import network.ermis.core.models.AttachmentType
import network.ermis.core.models.ChannelCapabilities
import network.ermis.ui.components.R
import network.ermis.ui.utils.getColorList
import network.ermis.ui.common.state.messages.Edit
import network.ermis.ui.common.state.messages.composer.MessageComposerState
import network.ermis.ui.common.state.messages.composer.RecordingState
import network.ermis.ui.components.databinding.MessageComposerDefaultTrailingContentBinding
import network.ermis.ui.view.messages.composer.MessageComposerContext
import network.ermis.ui.view.messages.composer.MessageComposerView
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * Represents the content shown at the end of [MessageComposerView].
 */
public interface MessageComposerTrailingContent : MessageComposerContent {
    /**
     * Click listener for the send message button.
     */
    public var sendMessageButtonClickListener: (() -> Unit)?

    /**
     * Touch listener for the mic button.
     */
    public var recordAudioButtonTouchListener: ((event: MotionEvent) -> Boolean)?
}

/**
 * Represents the default content shown at the end of [MessageComposerView].
 */
public open class DefaultMessageComposerTrailingContent : FrameLayout, MessageComposerTrailingContent {
    /**
     * Generated binding class for the XML layout.
     */
    protected lateinit var binding: MessageComposerDefaultTrailingContentBinding

    /**
     * The style for [MessageComposerView].
     */
    protected lateinit var style: MessageComposerViewStyle

    /**
     * Click listener for the send message button.
     */
    public override var sendMessageButtonClickListener: (() -> Unit)? = null

    /**
     * Touch listener for the mic button.
     */
    public override var recordAudioButtonTouchListener: ((event: MotionEvent) -> Boolean)? = null

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
    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding = MessageComposerDefaultTrailingContentBinding.inflate(streamThemeInflater, this)
        binding.sendMessageButton.setOnClickListener { sendMessageButtonClickListener?.invoke() }
        binding.recordAudioButton.setOnTouchListener { _, event ->
            recordAudioButtonTouchListener?.invoke(event) ?: false
        }
        binding.recordAudioButton.tag = RECORD_AUDIO_TAG
    }

    /**
     * Initializes the content view with [MessageComposerContext].
     *
     * @param messageComposerContext The context of this [MessageComposerView].
     */
    override fun attachContext(messageComposerContext: MessageComposerContext) {
        this.style = messageComposerContext.style

        val getStateListColor = { tintColor: Int ->
            getColorList(
                normalColor = tintColor,
                selectedColor = tintColor,
                disabledColor = context.getColorCompat(R.color.ui_grey_gainsboro),
            )
        }

        binding.sendMessageButton.setImageDrawable(style.sendMessageButtonIconDrawable)
        binding.sendMessageButton.updateLayoutParams {
            width = style.sendMessageButtonWidth
            height = style.sendMessageButtonHeight
        }
        binding.sendMessageButton.setPadding(
            style.sendMessageButtonPadding,
            style.sendMessageButtonPadding,
            style.sendMessageButtonPadding,
            style.sendMessageButtonPadding,
        )
        style.sendMessageButtonIconTintList?.also { tintList ->
            binding.sendMessageButton.imageTintList = tintList
        } ?: style.buttonIconDrawableTintColor?.let { tintColor ->
            binding.sendMessageButton.imageTintList = getStateListColor(tintColor)
        }

        binding.recordAudioButton.setImageDrawable(style.audioRecordingButtonIconDrawable)
        style.audioRecordingButtonIconTintList?.also { tintList ->
            binding.recordAudioButton.imageTintList = tintList
        } ?: style.buttonIconDrawableTintColor?.let { tintColor ->
            binding.recordAudioButton.imageTintList = getStateListColor(tintColor)
        }
        binding.recordAudioButton.updateLayoutParams {
            width = style.audioRecordingButtonWidth
            height = style.audioRecordingButtonHeight
        }
        binding.recordAudioButton.setPadding(
            style.audioRecordingButtonPadding,
            style.audioRecordingButtonPadding,
            style.audioRecordingButtonPadding,
            style.audioRecordingButtonPadding,
        )

        binding.cooldownBadgeTextView.setTextStyle(style.cooldownTimerTextStyle)
        binding.cooldownBadgeTextView.background = style.cooldownTimerBackgroundDrawable
    }

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    override fun renderState(state: MessageComposerState) {
        val appSettings = ErmisClient.instance().getAppSettings()
        val blockedMimeTypes = appSettings.app.fileUploadConfig.blockedMimeTypes
        val blockedFileExtensions = appSettings.app.fileUploadConfig.blockedFileExtensions
        val canSendMessage = state.ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
        val canUploadFile = state.ownCapabilities.contains(ChannelCapabilities.UPLOAD_FILE)
        val canUploadRecording = !blockedMimeTypes.contains(AttachmentType.AUDIO) &&
            !blockedFileExtensions.contains(AAC_EXTENSION)
        val hasTextInput = state.inputValue.isNotEmpty()
        val hasAttachments = state.attachments.isNotEmpty()
        val isInputValid = state.validationErrors.isEmpty()
        val isInEditMode = state.action is Edit

        val coolDownTime = state.coolDownTime
        val hasValidContent = (hasTextInput || hasAttachments) && isInputValid
        val noRecording = state.recording is RecordingState.Idle

        binding.root.isVisible = noRecording
        binding.apply {
            if (coolDownTime > 0 && !isInEditMode) {
                cooldownBadgeTextView.isVisible = true
                cooldownBadgeTextView.text = coolDownTime.toString()
                sendMessageButton.isVisible = false
                recordAudioButton.isVisible = false
            } else {
                cooldownBadgeTextView.isVisible = false
                sendMessageButton.isVisible = when (style.audioRecordingButtonPreferred) {
                    true -> hasTextInput || hasAttachments || isInEditMode
                    else -> true
                }
                sendMessageButton.isEnabled = style.sendMessageButtonEnabled && canSendMessage && hasValidContent
                recordAudioButton.isEnabled = style.sendMessageButtonEnabled &&
                    canSendMessage &&
                    canUploadRecording &&
                    canUploadFile
                recordAudioButton.isVisible = when (style.audioRecordingButtonVisible) {
                    true -> when (style.audioRecordingButtonPreferred) {
                        true -> canUploadFile && canUploadRecording && canSendMessage && !isInEditMode && !hasTextInput
                        else -> canUploadFile && canUploadRecording && canSendMessage && !isInEditMode
                    }
                    else -> false
                }
            }
        }
    }

    override fun findViewByKey(key: String): View? {
        return when (key) {
            MessageComposerContent.RECORD_AUDIO_BUTTON -> binding.recordAudioButton
            else -> null
        }
    }

    internal companion object {

        private const val RECORD_AUDIO_TAG = "record_audio"
        internal fun recordAudioButton(container: ViewGroup): View {
            return container.findViewById(R.id.recordAudioButton)
                ?: container.findViewWithTag(RECORD_AUDIO_TAG)
                ?: error("recordAudioButton not found in $container")
        }

        private const val AAC_EXTENSION = "aac"
    }
}
