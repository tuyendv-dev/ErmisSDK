package network.ermis.sample.debugger

import network.ermis.client.debugger.ChatClientDebugger
import network.ermis.client.debugger.SendMessageDebugger
import network.ermis.core.models.Message
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result

class CustomChatClientDebugger : ChatClientDebugger {

    override fun onNonFatalErrorOccurred(tag: String, src: String, desc: String, error: Error) {
        // TODO: Implement your custom logic here
    }

    override fun debugSendMessage(
        channelType: String,
        channelId: String,
        message: Message,
        isRetrying: Boolean,
    ): SendMessageDebugger {
        return CustomSendMessageDebugger(channelType, channelId, message, isRetrying)
    }
}

class CustomSendMessageDebugger(
    channelType: String,
    channelId: String,
    message: Message,
    isRetrying: Boolean,
) : SendMessageDebugger {

    private val logger by taggedLogger("SendMessageDebugger")

    private val cid = "$channelType:$channelId"

    init {
        logger.i { "<init> #debug; isRetrying: $isRetrying, cid: $cid, message: $message" }
    }

    override fun onStart(message: Message) {
        logger.d { "[onStart] #debug; message: $message" }
    }

    override fun onInterceptionStart(message: Message) {
        logger.d { "[onInterceptionStart] #debug; message: $message" }
    }

    override fun onInterceptionUpdate(message: Message) {
        logger.d { "[onInterceptionUpdate] #debug; message: $message" }
    }

    override fun onInterceptionStop(result: Result<Message>, message: Message) {
        logger.v { "[onInterceptionStop] #debug; result: $result, message: $message" }
    }

    override fun onSendStart(message: Message) {
        logger.d { "[onSendStart] #debug; message: $message" }
    }

    override fun onSendStop(result: Result<Message>, message: Message) {
        logger.v { "[onSendStop] #debug; result: $result, message: $message" }
    }

    override fun onStop(result: Result<Message>, message: Message) {
        logger.v { "[onStop] #debug; result: $result, message: $message" }
    }
}
