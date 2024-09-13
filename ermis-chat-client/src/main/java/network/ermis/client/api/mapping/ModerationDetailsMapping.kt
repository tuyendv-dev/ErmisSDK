package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.DownstreamModerationDetailsDto
import network.ermis.core.models.MessageModerationAction
import network.ermis.core.models.MessageModerationDetails

/**
 * Maps an [DownstreamModerationDetailsDto] to its [MessageModerationDetails] representation.
 */
internal fun DownstreamModerationDetailsDto.toDomain(): MessageModerationDetails {
    return MessageModerationDetails(
        originalText = original_text.orEmpty(),
        action = MessageModerationAction.fromRawValue(action.orEmpty()),
        errorMsg = error_msg.orEmpty(),
    )
}
