package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.CommandDto
import network.ermis.client.api.model.dto.ConfigDto
import network.ermis.core.models.Config

internal fun ConfigDto.toDomain(): Config =
    Config(
        createdAt = created_at,
        updatedAt = updated_at,
        name = name ?: "",
        typingEventsEnabled = typing_events,
        readEventsEnabled = read_events,
        connectEventsEnabled = connect_events,
        searchEnabled = search,
        isReactionsEnabled = reactions,
        isThreadEnabled = replies,
        muteEnabled = mutes,
        uploadsEnabled = uploads,
        urlEnrichmentEnabled = url_enrichment,
        customEventsEnabled = custom_events,
        pushNotificationsEnabled = push_notifications,
        messageRetention = message_retention,
        maxMessageLength = max_message_length,
        automod = automod,
        automodBehavior = automod_behavior,
        blocklistBehavior = blocklist_behavior ?: "",
        commands = commands.map(CommandDto::toDomain),
    )
