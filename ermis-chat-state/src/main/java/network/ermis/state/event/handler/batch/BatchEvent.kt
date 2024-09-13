package network.ermis.state.event.handler.batch

import network.ermis.client.events.ChatEvent
import java.util.concurrent.atomic.AtomicInteger

private val idGenerator = AtomicInteger(0)

/**
 * Events container to represent the source of the received events.
 */
internal class BatchEvent(
    val id: Int = idGenerator.incrementAndGet(),
    val sortedEvents: List<ChatEvent>,
    val isFromHistorySync: Boolean,
) {
    val size: Int = sortedEvents.size
    val isFromSocketConnection: Boolean = !isFromHistorySync
}
