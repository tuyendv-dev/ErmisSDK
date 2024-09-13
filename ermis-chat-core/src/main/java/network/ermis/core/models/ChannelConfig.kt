package network.ermis.core.models

import androidx.compose.runtime.Immutable

/**
 * Configuration of a channel.
 *
 * @property type String.
 * @property config [Config]
 */
@Immutable
public data class ChannelConfig(val type: String, val config: Config)
