package network.ermis.client.ermiscdn

/**
 * Contains query parameter keys used to resize Stream CDN hosted images.
 */
internal object ResizeImageQueryParameterKeys {

    /**
     * Query parameter key used to append the resized width value.
     */
    const val QUERY_PARAMETER_KEY_RESIZED_WIDTH: String = "w"

    /**
     * Query parameter key used to append the resized height value.
     */
    const val QUERY_PARAMETER_KEY_RESIZED_HEIGHT: String = "h"

    /**
     * Query parameter key used to append the resize mode value.
     */
    const val QUERY_PARAMETER_KEY_RESIZE_MODE: String = "resize"

    /**
     * Query parameter key used to append the crop mode value.
     */
    const val QUERY_PARAMETER_KEY_CROP_MODE: String = "crop"
}
