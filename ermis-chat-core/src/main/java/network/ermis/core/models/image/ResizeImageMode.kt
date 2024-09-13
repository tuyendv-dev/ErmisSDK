package network.ermis.core.models.image

/**
 * Sets the resizing mode when added as the 'resize' query parameter to
 * a Stream CDN hosted image URL. The default CDN resizing mode is [CLIP].
 *
 * @param queryParameterName The value used to form a query parameter.
 */
public enum class ResizeImageMode(public val queryParameterName: String) {

    /**
     * Make the image as large as possible, while maintaining the aspect ratio and
     * keeping the height and width less than or equal to the given height and width.
     */
    CLIP("clip"),

    /**
     * Crop the image to the given dimensions.
     * Use [CropImageMode] to give focus to a specific portion of the image.
     */
    CROP("crop"),

    /**
     * Make the image as large as possible, while maintaining the aspect ratio and keeping the height and width
     * less than or equal to the given height and width. Fill any leftover space with a black background.
     */
    FILL("fill"),

    /**
     * Ignore the aspect ratio, and resize the image to the given height and width.
     */
    SCALE("scale"),
}
