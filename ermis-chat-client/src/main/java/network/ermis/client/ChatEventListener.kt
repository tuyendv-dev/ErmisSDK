package network.ermis.client

import network.ermis.client.events.ChatEvent

public fun interface ChatEventListener<EventT : ChatEvent> {
    public fun onEvent(event: EventT)
}
