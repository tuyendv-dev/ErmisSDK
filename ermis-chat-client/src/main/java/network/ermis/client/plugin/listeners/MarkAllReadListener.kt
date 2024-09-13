package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient

/**
 * Listener for [ErmisClient.markAllRead] requests.
 */
public interface MarkAllReadListener {

    /**
     * Register this side effect to run just before actual [ErmisClient.markAllRead] request is launched.
     */
    public suspend fun onMarkAllReadRequest()
}
