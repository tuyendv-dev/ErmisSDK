package network.ermis.core.models.image

/**
 * Sets the cropping strategy when added as the 'crop' query parameter to
 * a Stream CDN hosted image URL. The default crop mode when resizing is
 * [CropImageMode.CENTER].
 *
 * note: Used when the resize strategy is set to [ResizeImageMode.CROP].
 *
 * @param queryParameterName The value used to form a query parameter.
 */
public enum class CropImageMode(public val queryParameterName: String) {

    /**
     * Keeps the top area of the image and crops out the rest.
     */
    TOP("top"),

    /**
     * Keeps the bottom area of the image and crops out the rest.
     */
    BOTTOM("bottom"),

    /**
     * Keeps the right area of the image and crops out the rest.
     */
    RIGHT("right"),

    /**
     * Keeps the left area of the image and crops out the rest.
     */
    LEFT("left"),

    /**
     * Keeps the center area of the image and crops out the rest.
     */
    CENTER("center"),
}
