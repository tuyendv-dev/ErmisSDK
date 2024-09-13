package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.SearchWarningDto
import network.ermis.core.models.SearchWarning

internal fun SearchWarningDto.toDomain(): SearchWarning {
    return SearchWarning(
        channelSearchCids = channel_search_cids,
        channelSearchCount = channel_search_count,
        warningCode = warning_code,
        warningDescription = warning_description,
    )
}
