package network.ermis.client.uploader

public object ImageMimeTypes {

    private val SUPPORTED_IMAGE_MIME_TYPES: Set<String> = setOf(
        "image/bmp",
        "image/gif",
        "image/jpeg",
        "image/png",
        "image/webp",
        "image/heic",
        "image/heic-sequence",
        "image/heif",
        "image/heif-sequence",
        "image/svg+xml",
    )

    public fun isImageMimeTypeSupported(mimeType: String?): Boolean {
        return SUPPORTED_IMAGE_MIME_TYPES.contains(mimeType)
    }
}
