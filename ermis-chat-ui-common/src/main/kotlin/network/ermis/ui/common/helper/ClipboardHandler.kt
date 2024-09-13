package network.ermis.ui.common.helper

import android.content.ClipData
import android.content.ClipboardManager
import network.ermis.core.models.Message
import network.ermis.core.models.User

/**
 * Abstraction over the [ClipboardHandlerImpl] that allows users to copy messages.
 */
public fun interface ClipboardHandler {

    /**
     * @param message The message to copy.
     */
    public fun copyMessage(message: Message)
}

/**
 * A simple implementation that relies on the [clipboardManager] to copy messages.
 *
 * @param clipboardManager System service that allows for clipboard operations, such as putting
 * new data on the clipboard.
 */
public class ClipboardHandlerImpl(
    private val clipboardManager: ClipboardManager,
    private val autoTranslationEnabled: Boolean = false,
    private val getCurrentUser: () -> User? = { null },
) : ClipboardHandler {

    /**
     * Allows users to copy the message text.
     *
     * @param message Message to copy the text from.
     */
    override fun copyMessage(message: Message) {
        val displayedText = when (autoTranslationEnabled) {
            true -> getCurrentUser()?.language?.let { userLanguage ->
                message.getTranslation(userLanguage).ifEmpty { message.text }
            } ?: message.text
            else -> message.text
        }
        clipboardManager.setPrimaryClip(ClipData.newPlainText("message", displayedText))
    }
}
