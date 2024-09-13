package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DeviceDto
import network.ermis.client.api.model.dto.DownstreamChannelMuteDto
import network.ermis.client.api.model.dto.DownstreamMuteDto
import network.ermis.client.api.model.dto.DownstreamUserDto
import network.ermis.client.api.model.dto.UpstreamUserDto
import network.ermis.core.models.Device
import network.ermis.core.models.User

internal fun User.toDto(): UpstreamUserDto =
    UpstreamUserDto(
        banned = isBanned,
        id = id,
        name = name,
        image = image,
        invisible = isInvisible,
        language = language,
        role = role,
        devices = devices.map(Device::toDto),
        teams = teams,
        extraData = extraData,
    )

internal fun DownstreamUserDto.toDomain(): User =
    User(
        id = id,
        name = name ?: "",
        image = image ?: avatar ?: "",
        role = role,
        invisible = invisible,
        language = language ?: "",
        banned = banned,
        devices = devices.orEmpty().map(DeviceDto::toDomain),
        online = online,
        createdAt = created_at,
        deactivatedAt = deactivated_at,
        updatedAt = updated_at,
        lastActive = last_active,
        totalUnreadCount = total_unread_count,
        unreadChannels = unread_channels,
        mutes = mutes.orEmpty().map(DownstreamMuteDto::toDomain),
        teams = teams,
        channelMutes = channel_mutes.orEmpty().map(DownstreamChannelMuteDto::toDomain),
        extraData = extraData.toMutableMap(),
    )
