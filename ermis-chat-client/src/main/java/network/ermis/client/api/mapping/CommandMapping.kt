package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.CommandDto
import network.ermis.core.models.Command

internal fun CommandDto.toDomain(): Command {
    return Command(
        name = name,
        description = description,
        args = args,
        set = set,
    )
}
