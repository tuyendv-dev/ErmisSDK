package network.ermis.offline.repository.domain.message.channelinfo

import network.ermis.offline.repository.domain.message.channelinfo.ChannelInfoEntity
import network.ermis.core.models.ChannelInfo

internal fun ChannelInfo.toEntity(): ChannelInfoEntity = ChannelInfoEntity(
    cid = cid,
    id = id,
    type = type,
    memberCount = memberCount,
    name = name,
)

internal fun ChannelInfoEntity.toModel(): ChannelInfo = ChannelInfo(
    cid = cid,
    id = id,
    type = type,
    memberCount = memberCount ?: 0,
    name = name,
)
