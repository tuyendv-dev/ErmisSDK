package network.ermis.client.persistance

import network.ermis.core.models.ChannelConfig

/**
 * Repository to read and write data of [ChannelConfig]
 */
public interface ChannelConfigRepository {
    /**
     * Caches in memory data from DB.
     */
    public suspend fun cacheChannelConfigs()

    /**
     * Select the [ChannelConfig] for a channel type.
     */
    public fun selectChannelConfig(channelType: String): ChannelConfig?

    /**
     * Writes many [ChannelConfig]
     */
    public suspend fun insertChannelConfigs(configs: Collection<ChannelConfig>)

    /**
     * Writes [ChannelConfig]
     */
    public suspend fun insertChannelConfig(config: ChannelConfig)

    /**
     * Clear ChanelConfigs of this repository.
     */
    public suspend fun clear()
}
