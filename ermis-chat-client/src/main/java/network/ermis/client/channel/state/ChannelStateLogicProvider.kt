package network.ermis.client.channel.state

import network.ermis.client.channel.ChannelMessagesUpdateLogic

/**
 * Provider for state {ChannelStateLogic}
 */
public interface ChannelStateLogicProvider {

    /**
     * Provides [ChannelStateLogic] for the channelType and channelId
     *
     * @param channelType String
     * @param channelId String
     */
    public fun channelStateLogic(channelType: String, channelId: String): ChannelMessagesUpdateLogic
}
