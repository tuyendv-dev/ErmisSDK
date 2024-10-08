package network.ermis.ui.view.messages.composer.content

import android.view.View

public interface MessageComposerContentContainer : Iterable<MessageComposerContent?> {
    public val center: MessageComposerContent?
    public val centerOverlap: MessageComposerContent?
    public val leading: MessageComposerContent?
    public val trailing: MessageComposerContent?
    public val header: MessageComposerContent?
    public val footer: MessageComposerContent?

    public fun asView(): View
    public fun findViewByKey(key: String): View?
}

public fun MessageComposerContentContainer.findRecordAudioButton(): View? = findViewByKey(
    MessageComposerContent.RECORD_AUDIO_BUTTON,
)
