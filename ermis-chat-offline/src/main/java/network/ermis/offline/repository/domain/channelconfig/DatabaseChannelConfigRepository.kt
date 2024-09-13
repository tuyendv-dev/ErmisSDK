package network.ermis.offline.repository.domain.channelconfig

import network.ermis.client.persistance.ChannelConfigRepository
import network.ermis.core.models.ChannelConfig
import java.util.Collections

/**
 * The channel config repository stores all channel configs in room as well as in memory.
 * Call channelConfigRepository.load to load all configs into memory.
 */
internal class DatabaseChannelConfigRepository(
    private val channelConfigDao: ChannelConfigDao,
) : ChannelConfigRepository {
    private val channelConfigs: MutableMap<String, ChannelConfig> = Collections.synchronizedMap(mutableMapOf())

    /**
     * Caches in memory data from DB.
     */
    override suspend fun cacheChannelConfigs() {
        channelConfigs += channelConfigDao.selectAll().map(ChannelConfigEntity::toModel)
            .associateBy(ChannelConfig::type)
    }

    /**
     * Select the [ChannelConfig] for a channel type.
     */
    override fun selectChannelConfig(channelType: String): ChannelConfig? {
        return channelConfigs[channelType]
    }

    /**
     * Writes many [ChannelConfig]
     */
    override suspend fun insertChannelConfigs(configs: Collection<ChannelConfig>) {
        // update the local configs
        channelConfigs += configs.associateBy(ChannelConfig::type)

        // insert into room db
        channelConfigDao.insert(configs.map(ChannelConfig::toEntity))
    }

    /**
     * Writes [ChannelConfig]
     */
    override suspend fun insertChannelConfig(config: ChannelConfig) {
        channelConfigs += config.type to config
        channelConfigDao.insert(config.toEntity())
    }

    override suspend fun clear() {
        channelConfigDao.deleteAll()
    }
}
