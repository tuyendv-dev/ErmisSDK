package network.ermis.state.event.chat.factory

import kotlinx.coroutines.flow.StateFlow
import network.ermis.client.ErmisClient
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Channel
import network.ermis.state.event.chat.ChatEventHandler
import network.ermis.state.event.chat.DefaultChatEventHandler

/**
 * A [ChatEventHandler] factory.
 * Allows to create chat event handler instance with visible channels map.
 *
 * @param clientState The client used to obtain current user.
 */
public open class ChatEventHandlerFactory(
    private val clientState: ClientState = ErmisClient.instance().clientState,
) {

    /**
     * Creates a [ChatEventHandler] instance.
     *
     * @param channels The visible channels map.
     */
    public open fun chatEventHandler(channels: StateFlow<Map<String, Channel>?>): ChatEventHandler {
        return DefaultChatEventHandler(channels = channels, clientState = clientState)
    }
}
