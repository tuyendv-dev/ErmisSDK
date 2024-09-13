package network.ermis.ui.common.utils

import androidx.annotation.Px
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType

/**
 * Enum class that holds a value used to obtain Giphy images of different quality level.
 *
 * @param value The name of the type.
 */
public enum class GiphyInfoType(public val value: String) {
    /**
     * Original quality giphy, the largest size to load.
     */
    ORIGINAL("original"),

    /**
     * Lower quality with a fixed height, adjusts width according to the Giphy aspect ratio. Lower size than [ORIGINAL].
     */
    FIXED_HEIGHT("fixed_height"),

    /**
     * Lower quality with a fixed height with width adjusted according to the aspect ratio
     * and played at a lower frame rate. Significantly lower size, but visually less appealing.
     */
    FIXED_HEIGHT_DOWNSAMPLED("fixed_height_downsampled"),
}

/**
 * Default width used for Giphy Images if no width metadata is available.
 */
public const val GIPHY_INFO_DEFAULT_WIDTH_DP: Int = 200

/**
 * Default height used for Giphy Images if no width metadata is available.
 */
public const val GIPHY_INFO_DEFAULT_HEIGHT_DP: Int = 200

/**
 * Returns an object containing extra information about the Giphy image based
 * on its type.
 *
 * @see GiphyInfoType
 * @see GiphyInfo
 */
public fun Attachment.giphyInfo(field: GiphyInfoType): GiphyInfo? {
    val giphyInfoMap =
        (extraData[AttachmentType.GIPHY] as? Map<String, Any>?)?.get(field.value) as? Map<String, String>?

    return giphyInfoMap?.let { map ->
        GiphyInfo(
            url = map["url"] ?: "",
            width = getGiphySize(map, "width", Utils.dpToPx(GIPHY_INFO_DEFAULT_WIDTH_DP)),
            height = getGiphySize(map, "height", Utils.dpToPx(GIPHY_INFO_DEFAULT_HEIGHT_DP)),
        )
    }
}

/**
 * Returns specified size for the giphy.
 *
 * @param map Map containing giphy size.
 * @param size The size we need.
 * @param defaultValue Default value if the size can't be parsed.
 *
 * @return The requested giphy [size].
 */
private fun getGiphySize(map: Map<String, String>, size: String, defaultValue: Int): Int {
    return if (!map[size].isNullOrBlank()) map[size]?.toInt() ?: defaultValue else defaultValue
}

/**
 * Contains extra information about Giphy attachments.
 *
 * @param url Url for the Giphy image.
 * @param width The width of the Giphy image.
 * @param height The height of the Giphy image.
 */
public data class GiphyInfo(
    val url: String,
    @Px val width: Int,
    @Px val height: Int,
)
