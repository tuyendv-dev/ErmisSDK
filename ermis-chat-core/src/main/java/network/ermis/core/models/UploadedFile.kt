package network.ermis.core.models

import androidx.compose.runtime.Immutable

/**
 * Represents a successfully uploaded file.
 *
 * @param file The URL of the uploaded file.
 * @param thumbUrl The property is auto-generated when uploading videos using
 * Stream CDN and can be used to display video previews.
 */
@Immutable
public data class UploadedFile(
    val file: String,
    val thumbUrl: String? = null,
)
