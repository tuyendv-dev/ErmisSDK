package network.ermis.ui.common.utils.typing

/**
 * Designed to buffer typing inputs.
 *
 * Its implementation should receive keystroke events by calling [TypingUpdatesBuffer.onKeystroke]
 * which it will internally buffer and send start and stop typing API calls accordingly.
 * This cuts down on unnecessary API calls.
 *
 * For the default implementation see [DefaultTypingUpdatesBuffer].
 */
public interface TypingUpdatesBuffer {

    /**
     * Should be called on every input change.
     *
     * @param [inputText] the current input text.
     */
    public fun onKeystroke(inputText: String)

    /**
     * Should send a stop typing event manually.
     *
     * Useful for runtime hygiene such as responding to lifecycle events.
     */
    public fun clear()
}
