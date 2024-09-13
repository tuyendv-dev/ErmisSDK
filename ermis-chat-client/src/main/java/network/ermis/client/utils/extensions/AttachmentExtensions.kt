package network.ermis.client.utils.extensions

import network.ermis.core.models.Attachment

internal const val ATTACHMENT_TYPE_IMAGE = "image"
internal const val ATTACHMENT_TYPE_FILE = "file"
public const val EXTRA_UPLOAD_ID: String = "uploadId"
public const val EXTRA_DURATION: String = "duration"
public const val EXTRA_WAVEFORM_DATA: String = "waveform_data"

internal val Attachment.isImage: Boolean
    get() = mimeType?.startsWith(ATTACHMENT_TYPE_IMAGE) ?: false

public val Attachment.uploadId: String?
    get() = extraData[EXTRA_UPLOAD_ID] as String?

/**
 * Duration of the attachment in seconds.
 */
public val Attachment.duration: Float?
    get() = (extraData[EXTRA_DURATION] as? Number)?.toFloat()

/**
 * Waveform data of the attachment.
 */
public val Attachment.waveformData: List<Float>?
    get() = extraData[EXTRA_WAVEFORM_DATA] as? List<Float>
