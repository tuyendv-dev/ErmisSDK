package network.ermis.sample.feature.chat.composer

import android.content.Context
import android.util.AttributeSet
import network.ermis.ui.common.state.messages.composer.MessageComposerState
import network.ermis.ui.view.messages.composer.content.DefaultMessageComposerLeadingContent

class CustomMessageComposerLeadingContent : DefaultMessageComposerLeadingContent {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun renderState(state: MessageComposerState) {
        super.renderState(state)
        binding.attachmentsButton.isEnabled = state.attachments.isEmpty()
    }
}
