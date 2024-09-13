
package network.ermis.ui.view.messages.composer.content

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import network.ermis.ui.common.state.messages.Edit
import network.ermis.ui.common.state.messages.Reply
import network.ermis.ui.common.state.messages.composer.MessageComposerState
import network.ermis.ui.common.state.messages.composer.RecordingState
import network.ermis.ui.components.databinding.MessageComposerDefaultHeaderContentBinding
import network.ermis.ui.view.messages.composer.MessageComposerContext
import network.ermis.ui.view.messages.composer.MessageComposerView
import network.ermis.ui.view.messages.composer.MessageComposerViewStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * Represents the content shown at the top of [MessageComposerView].
 */
public interface MessageComposerHeaderContent : MessageComposerContent {
    /**
     * Click listener for the dismiss action button.
     */
    public var dismissActionClickListener: (() -> Unit)?
}

/**
 * Represents the default content shown at the top of [MessageComposerView].
 */
public open class DefaultMessageComposerHeaderContent : FrameLayout, MessageComposerHeaderContent {
    /**
     * Generated binding class for the XML layout.
     */
    protected lateinit var binding: MessageComposerDefaultHeaderContentBinding

    /**
     * The style for [MessageComposerView].
     */
    protected lateinit var style: MessageComposerViewStyle

    /**
     * Click listener for the dismiss action button.
     */
    public override var dismissActionClickListener: (() -> Unit)? = null

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
        binding = MessageComposerDefaultHeaderContentBinding.inflate(streamThemeInflater, this)
        binding.dismissInputModeButton.setOnClickListener { dismissActionClickListener?.invoke() }
    }

    /**
     * Initializes the content view with [MessageComposerContext].
     *
     * @param messageComposerContext The context of this [MessageComposerView].
     */
    override fun attachContext(messageComposerContext: MessageComposerContext) {
        this.style = messageComposerContext.style

        binding.dismissInputModeButton.setImageDrawable(style.dismissModeIconDrawable)
    }

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    override fun renderState(state: MessageComposerState) {
        val noRecording = state.recording is RecordingState.Idle
        binding.root.isVisible = noRecording
        when (state.action) {
            is Reply -> {
                binding.inputModeHeaderContainer.isVisible = true
                binding.inputModeTextView.text = style.replyModeText
                binding.inputModeImageView.setImageDrawable(style.replyModeIconDrawable)
            }
            is Edit -> {
                binding.inputModeHeaderContainer.isVisible = true
                binding.inputModeTextView.text = style.editModeText
                binding.inputModeImageView.setImageDrawable(style.editModeIconDrawable)
            }
            else -> {
                binding.inputModeHeaderContainer.isVisible = false
            }
        }
    }
}
