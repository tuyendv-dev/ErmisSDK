package network.ermis.core.models.image

import androidx.compose.runtime.Immutable

/**
 * Holds the original width and height information for images
 * hosted by Stream CDN which declare said properties in their URL.
 *
 * @param originalWidth The width of the original image.
 * @param originalHeight The height of the original image.
 */
@Immutable
public data class OriginalImageDimensions(
    val originalWidth: Int,
    val originalHeight: Int,
)
