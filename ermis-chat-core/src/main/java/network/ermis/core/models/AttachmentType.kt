package network.ermis.core.models

/**
 * Represents types of attachments.
 */
public object AttachmentType {
    public const val IMAGE: String = "image"
    public const val IMGUR: String = "imgur"
    public const val GIPHY: String = "giphy"
    public const val VIDEO: String = "video"
    public const val AUDIO: String = "audio"
    public const val PRODUCT: String = "product"
    public const val FILE: String = "file"
    public const val LINK: String = "link"
    public const val AUDIO_RECORDING: String = "voiceRecording"
    public const val UNKNOWN: String = "unknown"

    public fun getTypeFromMimeType(mimeType: String?): String = mimeType?.let { type ->
        when {
            type.contains("image") -> IMAGE
            type.contains("video") -> VIDEO
            else -> FILE
        }
    } ?: FILE
}
