package network.ermis.state.event.handler

import androidx.annotation.VisibleForTesting
import network.ermis.client.events.ChatEvent

/**
 * Handles WebSocket and/or Synced events to update states and offline storage.
 */
internal interface EventHandler {

    /**
     * Triggers WebSocket event subscription.
     */
    fun startListening()

    /**
     * Cancels WebSocket event subscription.
     */
    fun stopListening()

    /**
     * For testing purpose only. Simulates socket event handling.
     */
    @VisibleForTesting
    suspend fun handleEvents(vararg events: ChatEvent)
}
