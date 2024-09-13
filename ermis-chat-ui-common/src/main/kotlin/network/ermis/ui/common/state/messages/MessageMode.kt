package network.ermis.ui.common.state.messages

import network.ermis.core.models.Message
import network.ermis.state.plugin.state.channel.thread.ThreadState

/**
 * Represents the message mode that's currently active.
 */
public sealed class MessageMode {

    /**
     * Regular mode, conversation with other users.
     */
    public object Normal : MessageMode() { override fun toString(): String = "Normal" }

    /**
     * Thread mode, where there's a parent message to respond to.
     *
     * @param parentMessage The message users are responding to in a Thread.
     * @param threadState The state of the current thread.
     */
    public data class MessageThread @JvmOverloads constructor(
        public val parentMessage: Message,
        public val threadState: ThreadState? = null,
    ) : MessageMode()
}

internal fun MessageMode.stringify(): String = when (this) {
    MessageMode.Normal -> "Normal"
    is MessageMode.MessageThread -> "MessageThread(parentMessage.id=${parentMessage.id})"
}
