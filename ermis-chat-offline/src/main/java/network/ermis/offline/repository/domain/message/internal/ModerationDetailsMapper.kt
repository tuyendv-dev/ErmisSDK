package network.ermis.offline.repository.domain.message.internal

import network.ermis.core.models.MessageModerationAction
import network.ermis.core.models.MessageModerationDetails

internal fun MessageModerationDetails.toEntity(): ModerationDetailsEntity {
    return ModerationDetailsEntity(
        originalText = originalText,
        action = action.rawValue,
        errorMsg = errorMsg,
    )
}

internal fun ModerationDetailsEntity.toModel(): MessageModerationDetails {
    return MessageModerationDetails(
        originalText = originalText,
        action = MessageModerationAction.fromRawValue(action),
        errorMsg = errorMsg,
    )
}
