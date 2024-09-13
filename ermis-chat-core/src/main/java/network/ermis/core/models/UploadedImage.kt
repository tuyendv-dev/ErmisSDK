package network.ermis.core.models

import androidx.compose.runtime.Immutable

/**
 * Represents a successfully uploaded image.
 *
 * @param file The URL of the uploaded image.
 * @param thumbUrl The thumb URL generated using custom CDN.
 */
@Immutable
public data class UploadedImage(
    val file: String,
    val thumbUrl: String? = null,
)
