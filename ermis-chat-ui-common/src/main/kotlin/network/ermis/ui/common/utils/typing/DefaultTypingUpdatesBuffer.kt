package network.ermis.ui.common.utils.typing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import network.ermis.core.internal.coroutines.DispatcherProvider

/**
 * After you call [onKeystroke] the class will call automatically
 * handle buffering typing events and call [onTypingStarted] and
 * [onTypingStopped] accordingly in a timed manner.
 *
 * The maximum frequency of sending out typing started events
 * is dictated by [DEFAULT_SEND_TYPING_UPDATES_INTERVAL].
 *
 * You should call [clear] before an instance of this class is removed.
 *
 * @param coroutineScope The coroutine scope used for running the timer and sending updates.
 * @param onTypingStarted Signals that a typing event should be sent.
 * Usually used to make an API call using [io.getstream.chat.android.client.ChatClient.keystroke]
 * @param onTypingStopped Signals that a stop typing event should be sent.
 * Usually used to make an API call using [io.getstream.chat.android.client.ChatClient.stopTyping]
 */
internal class DefaultTypingUpdatesBuffer(
    private val coroutineScope: CoroutineScope = CoroutineScope(DispatcherProvider.IO),
    private val onTypingStarted: () -> Unit,
    private val onTypingStopped: () -> Unit,
) : TypingUpdatesBuffer {

    /**
     * Holds the currently running job.
     */
    private var isTypingTimerJob: Job? = null

    /**
     * Used to intelligently call [onTypingStopped] and [onTypingStopped].
     */
    private var sendUpdatesJob: Job? = null

    /**
     * If the user is currently typing or not.
     *
     * Setting this value automatically triggers handling
     * typing events.
     */
    private var isTyping: Boolean = false
        set(value) {
            field = value
            if (isTyping) {
                handleTypingEvent()
            }
        }

    /**
     * Flips [isTyping] to false after a set amount of time
     * dictated by [DEFAULT_BUFFER_TYPING_UPDATES_INTERVAL].
     */
    private suspend fun startTypingTimer() {
        delay(DEFAULT_BUFFER_TYPING_UPDATES_INTERVAL)
        isTyping = false
    }

    /**
     * Sets the value of [isTyping] only if there is
     * a change in state in order to not create unnecessary events.
     * Also resets the job used to time periods of typing activity.
     * If the [inputText] is empty, it will call [onTypingStopped].
     *
     * @param [inputText] the current input text.
     */
    override fun onKeystroke(inputText: String) {
        isTypingTimerJob?.cancel()
        when (inputText.isEmpty()) {
            true -> {
                isTyping = false
                onTypingStopped()
            }
            false -> {
                if (!isTyping) {
                    isTyping = true
                }
                isTypingTimerJob = coroutineScope.launch { startTypingTimer() }
            }
        }
    }

    /**
     * Cancels all currently running coroutines and sets [isTyping] to false.
     *
     * Should be called before an instance of this class is removed.
     */
    override fun clear() {
        coroutineScope.coroutineContext.cancelChildren()
        if (isTyping) {
            isTyping = false
        }
        onTypingStopped()
    }

    /**
     * Calls [onTypingStarted] every [DEFAULT_SEND_TYPING_UPDATES_INTERVAL] while
     * the user is still typing and calls [onTypingStopped] afterwards.
     */
    private fun handleTypingEvent() {
        sendUpdatesJob?.cancel()

        sendUpdatesJob = coroutineScope.launch {
            while (isTyping) {
                onTypingStarted()
                delay(DEFAULT_SEND_TYPING_UPDATES_INTERVAL)
            }

            onTypingStopped()
        }
    }

    private companion object {
        /**
         * Dictates the frequency start typing updates are sent
         * at while the user is typing.
         * DEFAULT_SEND_TYPING_UPDATES_INTERVAL > DEFAULT_BUFFER_TYPING_UPDATES_INTERVAL
         */
        private const val DEFAULT_SEND_TYPING_UPDATES_INTERVAL: Long = 1100L

        /**
         * Dictates how long the user has to stop typing for
         * before he is considered to be inactive (stopped typing).
         */
        private const val DEFAULT_BUFFER_TYPING_UPDATES_INTERVAL: Long = 1000L
    }
}
