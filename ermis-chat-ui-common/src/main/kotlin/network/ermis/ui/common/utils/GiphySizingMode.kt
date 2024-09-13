package network.ermis.ui.common.utils

/**
 * Sets the way in which Giphy container
 * sizing is determined.
 */
public enum class GiphySizingMode {

    /**
     * Automatically resizes Giphy containers
     * while respecting the original Giphy image dimension ratio.
     *
     * Setting this mode means that the container will ignore any
     * width and height dimensions.
     */
    ADAPTIVE,

    /**
     * Sets Giphy containers to fixed size mode.
     *
     * Setting this mode means that the container will respect
     * given width and height dimensions.
     */
    FIXED_SIZE,
}
