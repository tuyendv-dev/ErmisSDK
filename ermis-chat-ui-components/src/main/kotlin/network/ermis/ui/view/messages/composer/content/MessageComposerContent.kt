
package network.ermis.ui.view.messages.composer.content

import android.view.View
import network.ermis.ui.common.state.messages.composer.MessageComposerState
import network.ermis.ui.view.messages.composer.MessageComposerContext
import network.ermis.ui.view.messages.composer.MessageComposerView

/**
 * An interface that must be implemented by the content views of [MessageComposerView].
 */
public interface MessageComposerContent {
    /**
     * Initializes the content view with [MessageComposerContext].
     *
     * @param messageComposerContext The context of this [MessageComposerView].
     */
    public fun attachContext(messageComposerContext: MessageComposerContext)

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    public fun renderState(state: MessageComposerState)

    /**
     * Finds the first descendant view with the given [key].
     *
     * @param key the key to search for.
     */
    public fun findViewByKey(key: String): View? {
        return null
    }

    public companion object PredefinedKeys {
        public const val RECORD_AUDIO_BUTTON: String = "record_audio_button"
    }
}

public fun MessageComposerContent.asView(): View? = this as? View
