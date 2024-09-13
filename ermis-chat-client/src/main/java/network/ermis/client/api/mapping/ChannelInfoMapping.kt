package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.ChannelInfoDto
import network.ermis.core.models.ChannelInfo

internal fun ChannelInfoDto.toDomain(): ChannelInfo =
    ChannelInfo(
        cid = cid,
        id = id,
        memberCount = member_count,
        name = name,
        type = type,
        image = image,
    )
