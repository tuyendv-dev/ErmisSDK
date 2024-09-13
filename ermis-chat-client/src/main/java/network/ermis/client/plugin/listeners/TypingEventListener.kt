package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.client.events.ChatEvent
import network.ermis.core.models.EventType
import io.getstream.result.Error
import io.getstream.result.Result
import java.util.Date

/**
 * Listener for [ErmisClient.keystroke] and [ErmisClient.stopTyping] requests.
 */
public interface TypingEventListener {

    /**
     * Runs this precondition before [ErmisClient.keystroke] and [ErmisClient.stopTyping] request is invoked.
     *
     * @param eventType Type of the event that can be one of the [EventType.TYPING_START] or
     * [EventType.TYPING_STOP] etc.
     * @param channelType Type of the channel in which the event is sent.
     * @param channelId Id of the channel in which the event is sent.
     * @param extraData Any extra data such as parent id.
     * @param eventTime [Date] object as the time of this event.
     *
     * @return [Result] having [Unit] if precondition passes otherwise [Error] describing what went wrong.
     */
    public fun onTypingEventPrecondition(
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
        eventTime: Date,
    ): Result<Unit>

    /**
     * Runs this side effect before [ErmisClient.keystroke] and [ErmisClient.stopTyping] request is invoked.
     *
     * @param eventType Type of the event that can be one of the [EventType.TYPING_START] or
     * [EventType.TYPING_STOP] etc.
     * @param channelType Type of the channel in which the event is sent.
     * @param channelId Id of the channel in which the event is sent.
     * @param extraData Any extra data such as parent id.
     * @param eventTime [Date] object as the time of this event.
     */
    public fun onTypingEventRequest(
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
        eventTime: Date,
    )

    /**
     * Runs this side effect after [ErmisClient.keystroke] and [ErmisClient.stopTyping] request is completed.
     *
     * @param result Result of the original request.
     * @param eventType Type of the event that can be one of the [EventType.TYPING_START] or
     * [EventType.TYPING_STOP] etc.
     * @param channelType Type of the channel in which the event is sent.
     * @param channelId Id of the channel in which the event is sent.
     * @param extraData Any extra data such as parent id.
     * @param eventTime [Date] object as the time of this event.
     */
    @Suppress("LongParameterList")
    public fun onTypingEventResult(
        result: Result<ChatEvent>,
        eventType: String,
        channelType: String,
        channelId: String,
        extraData: Map<Any, Any>,
        eventTime: Date,
    )
}
