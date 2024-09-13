package network.ermis.client.channel

import network.ermis.client.channel.state.ChannelState
import network.ermis.core.models.Message

public interface ChannelMessagesUpdateLogic {

    public fun upsertMessage(message: Message, updateCount: Boolean = true)

    public fun upsertMessages(
        messages: List<Message>,
        shouldRefreshMessages: Boolean = false,
        updateCount: Boolean = true,
    )

    public fun listenForChannelState(): ChannelState

    public fun replyMessage(repliedMessage: Message?)
}
