package network.ermis.client.interceptor.message

import network.ermis.core.models.Message
import network.ermis.core.models.User

public interface PrepareMessageLogic {

    public fun prepareMessage(message: Message, channelId: String, channelType: String, user: User): Message
}
