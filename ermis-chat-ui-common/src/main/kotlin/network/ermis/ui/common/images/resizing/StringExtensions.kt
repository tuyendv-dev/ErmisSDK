package network.ermis.ui.common.images.resizing

import network.ermis.client.utils.extensions.createResizedStreamCdnImageUrl

/**
 * A convenience method which applies Stream CDN image resizing if it's enabled and applicable.
 * It doesn't hold any special value other than minimizing repetition.
 *
 * @param streamCdnImageResizing Holds value information about the resizing mode including if it is enabled or not.
 *
 * @return The URL to the resized image if resizing was applicable, otherwise returns the URL to the original image.
 */
public fun String.applyStreamCdnImageResizingIfEnabled(streamCdnImageResizing: ErmisImageResizing): String =
    if (streamCdnImageResizing.imageResizingEnabled) {
        this.createResizedStreamCdnImageUrl(
            resizedHeightPercentage = streamCdnImageResizing.resizedWidthPercentage,
            resizedWidthPercentage = streamCdnImageResizing.resizedHeightPercentage,
            resizeMode = streamCdnImageResizing.resizeMode,
            cropMode = streamCdnImageResizing.cropMode,
        )
    } else {
        this
    }
