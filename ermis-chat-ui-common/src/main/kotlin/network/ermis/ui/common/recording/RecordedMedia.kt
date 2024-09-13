package network.ermis.ui.common.recording

import network.ermis.core.models.Attachment

public data class RecordedMedia(
    val durationInMs: Int,
    val attachment: Attachment,
)
