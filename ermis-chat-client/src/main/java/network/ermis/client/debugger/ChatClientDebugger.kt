package network.ermis.client.debugger

import network.ermis.core.models.Message
import io.getstream.result.Error

public interface ChatClientDebugger {

    /**
     * Called when a non-fatal error occurs.
     *
     * @param tag The location where the error occurred.
     * @param src The source of the error.
     * @param desc The description of the error.
     * @param error The error that occurred.
     */
    public fun onNonFatalErrorOccurred(tag: String, src: String, desc: String, error: Error) {}

    /**
     * Creates an instance of [SendMessageDebugger] that allows you to debug the sending process of a message.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param message Message object
     * @param isRetrying True if this message is being retried.
     *
     * @return Your custom [SendMessageDebugger] implementation.
     */
    public fun debugSendMessage(
        channelType: String,
        channelId: String,
        message: Message,
        isRetrying: Boolean = false,
    ): SendMessageDebugger = StubSendMessageDebugger
}

/**
 * Mock [ChatClientDebugger] implementation.
 */
internal object StubChatClientDebugger : ChatClientDebugger
