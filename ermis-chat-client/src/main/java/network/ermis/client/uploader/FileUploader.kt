package network.ermis.client.uploader

import network.ermis.client.utils.ProgressCallback
import network.ermis.core.models.UploadedFile
import network.ermis.core.models.UploadedImage
import io.getstream.result.Result
import java.io.File

/**
 * The FileUploader is responsible for sending and deleting files from given channel
 */
public interface FileUploader {

    /**
     * Uploads a file for the given channel. Progress can be accessed via [callback].
     *
     * @return The [Result] object containing an instance of [UploadedFile] in the case of a successful upload
     * or an exception if the upload had failed.
     *
     * @see [Result.success]
     * @see [Result.error]
     */
    @Suppress("LongParameterList")
    public fun sendFile(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
        callback: ProgressCallback,
    ): Result<UploadedFile>

    /**
     * Uploads a file for the given channel.
     *
     * @return The [Result] object containing an instance of [UploadedFile] in the case of a successful upload
     * or an exception if the upload had failed.
     *
     * @see [Result.success]
     * @see [Result.error]
     */
    public fun sendFile(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
    ): Result<UploadedFile>

    /**
     * Uploads an image for the given channel. Progress can be accessed via [callback].
     *
     * @return The [Result] object containing an instance of [UploadedImage] in the case of a successful upload
     * or an exception if the upload had failed.
     *
     * @see [Result.success]
     * @see [Result.error]
     */
    @Suppress("LongParameterList")
    public fun sendImage(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
        callback: ProgressCallback,
    ): Result<UploadedImage>

    /**
     * Uploads an image for the given channel.
     *
     * @return The [Result] object containing an instance of [UploadedImage] in the case of a successful upload
     * or an exception if the upload had failed.
     *
     * @see [Result.success]
     * @see [Result.error]
     */
    public fun sendImage(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
    ): Result<UploadedImage>

    /**
     * Deletes the file represented by [url] from the given channel.
     *
     * @return The empty [Result] object, or [Result] object with exception if the operation failed.
     *
     * @see [Result.success]
     * @see [Result.error]
     */
    public fun deleteFile(
        channelType: String,
        channelId: String,
        userId: String,
        url: String,
    ): Result<Unit>

    /**
     * Deletes the image represented by [url] from the given channel.
     *
     * @return The empty [Result] object, or [Result] object with exception if the operation failed.
     *
     * @see [Result.success]
     * @see [Result.error]
     */
    public fun deleteImage(
        channelType: String,
        channelId: String,
        userId: String,
        url: String,
    ): Result<Unit>
}
