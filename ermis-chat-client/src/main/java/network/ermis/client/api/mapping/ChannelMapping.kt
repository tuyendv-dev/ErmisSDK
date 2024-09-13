package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamChannelDto
import network.ermis.client.api.model.dto.DownstreamMemberDto
import network.ermis.client.api.model.dto.DownstreamMessageDto
import network.ermis.client.api.model.dto.DownstreamUserDto
import network.ermis.client.utils.extensions.syncUnreadCountWithReads
import network.ermis.core.models.Channel
import network.ermis.core.models.Config
import network.ermis.core.models.User
import java.util.Date

internal fun DownstreamChannelDto.toDomain(): Channel =
    Channel(
        id = id,
        type = type,
        name = name ?: "",
        image = image ?: "",
        description = description ?: "",
        watcherCount = watcher_count,
        frozen = frozen,
        lastMessageAt = last_message_at,
        createdAt = created_at,
        deletedAt = deleted_at,
        updatedAt = updated_at,
        memberCount = member_count,
        messages = messages.map(DownstreamMessageDto::toDomain),
        members = getMembers.map(DownstreamMemberDto::toDomain),
        watchers = watchers.map(DownstreamUserDto::toDomain),
        read = read.map { it.toDomain(last_message_at ?: it.last_read ?: Date()) },
        config = config?.toDomain() ?: Config(),
        createdBy = created_by?.toDomain() ?: User(),
        team = team,
        cooldown = cooldown,
        pinnedMessages = pinned_messages.map(DownstreamMessageDto::toDomain),
        ownCapabilities = own_capabilities?.toSet() ?: setOf(),
        memberCapabilities = member_capabilities?.toSet() ?: setOf(),
        membership = membership?.toDomain(), // TODO dang bo qua truong nay
        extraData = extraData.toMutableMap(),
    ).syncUnreadCountWithReads()
