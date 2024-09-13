package network.ermis.client.utils.extensions

import android.net.Uri
import androidx.annotation.FloatRange
import androidx.core.net.toUri
import network.ermis.client.ermiscdn.ResizeImageQueryParameterKeys
import network.ermis.core.models.image.CropImageMode
import network.ermis.core.models.image.OriginalImageDimensions
import network.ermis.core.models.image.ResizeImageMode
import io.getstream.log.StreamLog

private val snakeRegex = "_[a-zA-Z]".toRegex()
private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

/**
 * Converts string written in snake case to String in camel case with the first symbol in lower case.
 * For example string "created_at_some_time" is converted to "createdAtSomeTime".
 */
public fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) { matchResult ->
        matchResult.value.replace("_", "").uppercase()
    }
}

public fun String.lowerCamelCaseToGetter(): String = "get${this[0].uppercase()}${this.substring(1)}"

/**
 * Converts String written in camel case to String in snake case.
 * For example string "createdAtSomeTime" is converted to "created_at_some_time".
 */
internal fun String.camelCaseToSnakeCase(): String {
    return camelRegex.replace(this) { "_${it.value}" }.lowercase()
}

/**
 * Parses CID of channel to channelType and channelId.
 *
 * @return Pair<String, String> Pair with channelType and channelId.
 * @throws IllegalStateException Throws an exception if format of cid is incorrect.
 */
@Throws(IllegalStateException::class)
public fun String.cidToTypeAndId(): Pair<String, String> {
    check(isNotEmpty()) { "cid can not be empty" }
    check(':' in this) { "cid needs to be in the format channelType:channelId. For example, messaging:123" }
    return checkNotNull(split(":")
        .takeIf { it.size >= 2 }?.let {
            if (it.size == 2) {
                it.first() to it.last()
            } else {
                val type = it.first()
                val id = this.substring(it.first().length + 1, this.length)
                type to id
            }
        }
    )
}

public fun String.cidIsChannelDirect(): Boolean {
    return this.contains("messaging")
}

@Throws(IllegalStateException::class)
public fun String.channelIdToProjectId(): String {
    check(isNotEmpty()) { "id can not be empty" }
    check(':' in this) { "id needs to be in the format projectId:channelId." }
    return checkNotNull(split(":")
        .takeIf { it.size >= 2 }?.let {
                it.first()
        }
    )
}

/**
 * Returns [OriginalImageDimensions] if the image is hosted by Stream's CDN and is resizable,
 * otherwise returns null.
 *
 * @return Class containing the original width and height dimensions of the image or null.
 */
public fun String.getStreamCdnHostedImageDimensions(): OriginalImageDimensions? {
    return try {
        val imageUri = this.toUri()

        val width = imageUri.getQueryParameter("ow")
            ?.toInt()

        val height = imageUri.getQueryParameter("oh")
            ?.toInt()

        if (height != null && width != null) {
            OriginalImageDimensions(
                originalWidth = width,
                originalHeight = height,
            )
        } else {
            null
        }
    } catch (e: java.lang.Exception) {
        val logger = StreamLog.getLogger("Chat: getStreamCDNHostedImageDimensions")
        logger.e { "Failed to parse Stream CDN image dimensions from the URL:\n ${e.stackTraceToString()}" }

        null
    }
}

/**
 * Generates a string URL with Stream CDN image resizing query parameters added to it. Once this URL is called, Stream's
 * CDN will generate a resized image which is accessible using the link returned by this function.
 *
 * @param resizedWidthPercentage The percentage of the original image width the resized image width will be.
 * @param resizedHeightPercentage The percentage of the original image height the resized image height will be.
 * @param resizeMode Sets the image resizing mode. The default mode is [ResizeImageMode.CLIP].
 * @param cropMode Sets the image crop mode. The default mode is [CropImageMode.CENTER].
 */
public fun String.createResizedStreamCdnImageUrl(
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) resizedWidthPercentage: Float,
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) resizedHeightPercentage: Float,
    resizeMode: ResizeImageMode? = null,
    cropMode: CropImageMode? = null,
): String {
    val logger = StreamLog.getLogger("Chat:resizedStreamCdnImageUrl")

    val streamCdnImageDimensions = this.getStreamCdnHostedImageDimensions()

    return if (streamCdnImageDimensions != null) {
        val imageLinkUri = this.toUri()

        if (imageLinkUri.wasImagePreviouslyResized()) {
            logger.w {
                "Image URL already contains resizing parameters. Please apply resizing parameters only to " +
                    "original image URLs."
            }

            return this@createResizedStreamCdnImageUrl
        }

        val resizedWidth: Int = (streamCdnImageDimensions.originalWidth * resizedWidthPercentage).toInt()
        val resizedHeight: Int = (streamCdnImageDimensions.originalHeight * resizedHeightPercentage).toInt()

        val resizedImageUrl = imageLinkUri
            .buildUpon()
            .appendValueAsQueryParameterIfNotNull(
                key = ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_RESIZED_WIDTH,
                value = resizedWidth,
            )
            .appendValueAsQueryParameterIfNotNull(
                key = ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_RESIZED_HEIGHT,
                value = resizedHeight,
            )
            .appendValueAsQueryParameterIfNotNull(
                key = ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_RESIZE_MODE,
                value = resizeMode?.queryParameterName,
            )
            .appendValueAsQueryParameterIfNotNull(
                key = ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_CROP_MODE,
                value = cropMode?.queryParameterName,
            )
            .build()
            .toString()

        logger.i {
            "Resized image URL: $resizedImageUrl"
        }

        resizedImageUrl
    } else {
        logger.i {
            "Image not hosted by Stream's CDN or not containing original width and height query parameters " +
                "was not resized"
        }
        this
    }
}

/**
 * Checks if a string contains resizing related query parameters.
 *
 * @return true if the URL contains resizing parameters, false otherwise.
 */
private fun Uri.wasImagePreviouslyResized(): Boolean =
    queryParameterNames.intersect(
        listOf(
            ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_RESIZED_WIDTH,
            ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_RESIZED_HEIGHT,
            ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_RESIZE_MODE,
            ResizeImageQueryParameterKeys.QUERY_PARAMETER_KEY_CROP_MODE,
        ),
    ).isNotEmpty()

/**
 * A convenience method which evaluates if [value] is null or not and appends
 * it with the accompanying parameter key name in the form of a query parameter.
 *
 * @param key Query parameter key.
 * @param value Query parameter value.
 */
private fun Uri.Builder.appendValueAsQueryParameterIfNotNull(key: String, value: String?): Uri.Builder {
    return if (value != null) {
        this.appendQueryParameter(key, value)
    } else {
        this
    }
}

/**
 * A convenience method which evaluates if [value] is null or not and appends
 * it with the accompanying parameter key name in the form of a query parameter.
 *
 * @param key Query parameter key.
 * @param value Query parameter value.
 */
private fun Uri.Builder.appendValueAsQueryParameterIfNotNull(key: String, value: Int?): Uri.Builder {
    return if (value != null) {
        this.appendQueryParameter(key, value.toString())
    } else {
        this
    }
}
