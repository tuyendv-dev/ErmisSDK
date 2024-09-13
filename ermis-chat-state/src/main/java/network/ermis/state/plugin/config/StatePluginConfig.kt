package network.ermis.state.plugin.config

import network.ermis.core.models.TimeDuration

/**
 * Provides a configuration for [io.getstream.chat.android.state.plugin.internal.StatePlugin].
 *
 * @param backgroundSyncEnabled Whether the SDK should perform background sync if some queries fail.
 * @param userPresence Whether the SDK should receive user presence changes.
 * @param syncMaxThreshold The maximum time allowed for data to synchronize. If not synced within this limit, the SDK deletes it.
 */
public data class StatePluginConfig @JvmOverloads constructor(
    public val backgroundSyncEnabled: Boolean = true,
    public val userPresence: Boolean = true,
    public val syncMaxThreshold: TimeDuration = TimeDuration.hours(12),
)
