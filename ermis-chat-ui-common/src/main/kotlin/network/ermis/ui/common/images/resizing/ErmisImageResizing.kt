package network.ermis.ui.common.images.resizing

import androidx.annotation.FloatRange
import network.ermis.core.models.image.CropImageMode
import network.ermis.core.models.image.ResizeImageMode

/**
 * Used to adjust the request to resize images hosted on Stream's CDN.
 *
 * Note: This only affects images hosted on Stream's CDN which contain original width (ow) and height (oh)
 * query parameters.
 *
 * @param imageResizingEnabled Enables or disables image resizing.
 * @param resizedWidthPercentage The percentage of the original image width the resized image width will be.
 * @param resizedHeightPercentage The percentage of the original image height the resized image height will be.
 * @param resizeMode Sets the image resizing mode. If null, the default mode used by the CDN is
 * [ResizeImageMode.CLIP].
 * @param cropMode Sets the image crop mode. If null, the default mode used by the CDN is
 * [CropImageMode.CENTER].
 */
public data class ErmisImageResizing(
    val imageResizingEnabled: Boolean,
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) val resizedWidthPercentage: Float,
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) val resizedHeightPercentage: Float,
    val resizeMode: ResizeImageMode?,
    val cropMode: CropImageMode?,
) {

    public companion object {

        /**
         * Creates the default Stream CDN image resizing strategy where the image
         * resizing is disabled.
         *
         * @return Stream CDN hosted image resizing strategy in the form of [ErmisImageResizing].
         */
        public fun defaultStreamCdnImageResizing(): ErmisImageResizing = ErmisImageResizing(
            imageResizingEnabled = false,
            resizedWidthPercentage = 1f,
            resizedHeightPercentage = 1f,
            resizeMode = null,
            cropMode = null,
        )
    }
}
