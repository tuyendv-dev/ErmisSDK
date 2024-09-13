package network.ermis.client.persistance.noop

import network.ermis.client.persistance.ChannelConfigRepository
import network.ermis.core.models.ChannelConfig

/**
 * No-Op ChannelConfigRepository.
 */
internal object NoOpChannelConfigRepository : ChannelConfigRepository {
    override suspend fun cacheChannelConfigs() { /* No-Op */ }
    override fun selectChannelConfig(channelType: String): ChannelConfig? = null
    override suspend fun insertChannelConfigs(configs: Collection<ChannelConfig>) { /* No-Op */ }
    override suspend fun insertChannelConfig(config: ChannelConfig) { /* No-Op */ }
    override suspend fun clear() { /* No-Op */ }
}
