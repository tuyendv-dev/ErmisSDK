package network.ermis.client.attachment

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.webkit.MimeTypeMap
import network.ermis.client.ErmisClient
import network.ermis.client.utils.extensions.uploadId
import network.ermis.client.uploader.ImageMimeTypes
import network.ermis.client.utils.ProgressCallback
import network.ermis.core.models.Attachment
import io.getstream.log.taggedLogger
import io.getstream.result.Result
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

public class AttachmentUploader(
    private val appContext: Context,
    private val client: ErmisClient = ErmisClient.instance()
) {

    private val logger by taggedLogger("Chat:Uploader")

    /**
     * Uploads the given attachment.
     *
     * @param channelType The type of the channel.
     * @param channelId The ID of the channel.
     * @param attachment The attachment to be uploaded.
     * @param progressCallback Used to listen to file upload
     * progress, success, and failure.
     *
     * @return The resulting uploaded attachment.
     */
    public suspend fun uploadAttachment(
        channelType: String,
        channelId: String,
        attachment: Attachment,
        progressCallback: ProgressCallback? = null,
    ): Result<Attachment> {
        val file = checkNotNull(attachment.upload) { "An attachment needs to have a non null attachment.upload value" }

        val mimeType: String = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
            ?: attachment.mimeType ?: ""
        val attachmentType = mimeType.toAttachmentType()

        return if (attachmentType == AttachmentType.IMAGE) {
            logger.d { "[uploadAttachment] #uploader; uploading ${attachment.uploadId} as image" }
            uploadImage(
                channelType = channelType,
                channelId = channelId,
                file = file,
                progressCallback = progressCallback,
                attachment = attachment,
                mimeType = mimeType,
                attachmentType = attachmentType,
            )
        } else {
            logger.d { "[uploadAttachment] #uploader; uploading ${attachment.uploadId} as file" }
            uploadFile(
                channelType = channelType,
                channelId = channelId,
                file = file,
                progressCallback = progressCallback,
                attachment = attachment,
                mimeType = mimeType,
                attachmentType = attachmentType,
            )
        }
    }

    /**
     * Uploads an image attachment.
     *
     * @param channelType The type of the channel.
     * @param channelId The ID of the channel.
     * @param file The file that will be uploaded.
     * @param attachment The attachment to be uploaded.
     * @param progressCallback Used to listen to file upload
     * progress, success, and failure.
     * @param mimeType The mime type of the attachment that will be uploaded,
     * e.g. image/jpeg.
     * @param attachmentType The type of the attachment, e.g. "video", "audio", etc.
     *
     * @return The resulting uploaded attachment.
     */
    @Suppress("LongParameterList")
    private suspend fun uploadImage(
        channelType: String,
        channelId: String,
        file: File,
        progressCallback: ProgressCallback?,
        attachment: Attachment,
        mimeType: String,
        attachmentType: AttachmentType,
    ): Result<Attachment> {
        logger.d {
            "[uploadImage] #uploader; mimeType: $mimeType, attachmentType: $attachmentType, " +
                "file: $file, cid: $channelType:$$channelId, attachment: $attachment"
        }
        val result = client.sendImage(channelType, channelId, file, progressCallback)
            .await()
        logger.v { "[uploadImage] #uploader; result: $result" }
        return when (result) {
            is Result.Success -> {
                val augmentedAttachment = attachment.augmentAttachmentOnSuccess(
                    file = file,
                    mimeType = mimeType,
                    attachmentType = attachmentType,
                    url = result.value.file,
                    thumbUrl = result.value.thumbUrl,
                )

                onSuccessfulUpload(
                    augmentedAttachment = augmentedAttachment,
                    progressCallback = progressCallback,
                )
            }
            is Result.Failure -> {
                onFailedUpload(
                    attachment = attachment,
                    result = result,
                    progressCallback = progressCallback,
                )
            }
        }
    }

    private fun getThumnailVideoFile(fileVideo: File) : File? {
        //create a file to write bitmap data
        val f = File(appContext.cacheDir, "file_thumnail_video")
        f.createNewFile()
        //Convert bitmap to byte array
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(fileVideo.path)
        val thummbnailBitmap = mmr.frameAtTime ?: return null
        val bos = ByteArrayOutputStream()
        thummbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 80 , bos)
        val bitmapdata = bos.toByteArray()
        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(f)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitmapdata)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return f
    }

    /**
     * Uploads a file attachment.
     *
     * @param channelType The type of the channel.
     * @param channelId The ID of the channel.
     * @param file The file that will be uploaded.
     * @param attachment The attachment to be uploaded.
     * @param progressCallback Used to listen to file upload
     * progress, success, and failure.
     * @param mimeType The mime type of the attachment that will be uploaded,
     * e.g. image/jpeg.
     * @param attachmentType The type of the attachment, e.g. "video", "audio", etc.
     *
     * @return The resulting uploaded attachment.
     */
    @Suppress("LongParameterList")
    private suspend fun uploadFile(
        channelType: String,
        channelId: String,
        file: File,
        progressCallback: ProgressCallback?,
        attachment: Attachment,
        mimeType: String,
        attachmentType: AttachmentType,
    ): Result<Attachment> {
        logger.d {
            "[uploadFile] #uploader; mimeType: $mimeType, attachmentType: $attachmentType, " +
                "file: $file, cid: $channelType:$$channelId, attachment: $attachment"
        }
        val result = client.sendFile(channelType, channelId, file, progressCallback)
            .await()
        logger.v { "[uploadFile] #uploader; result: $result" }
        return when (result) {
            is Result.Success -> {
                var thumbUrlResult = result.value.thumbUrl
                if (mimeType.toAttachmentType() == AttachmentType.VIDEO) {
                    getThumnailVideoFile(file)?.let {  thumbnaiFile ->
                        val thumnailResult = client.sendImage(channelType, channelId, thumbnaiFile, progressCallback)
                            .await()
                        if (thumnailResult is Result.Success) {
                            thumbUrlResult = thumnailResult.value.file
                        }
                    }
                }
                val augmentedAttachment = attachment.augmentAttachmentOnSuccess(
                    file = file,
                    mimeType = mimeType,
                    attachmentType = attachmentType,
                    url = result.value.file,
                    thumbUrl = thumbUrlResult,
                )

                onSuccessfulUpload(
                    augmentedAttachment = augmentedAttachment,
                    progressCallback = progressCallback,
                )
            }
            is Result.Failure -> {
                onFailedUpload(
                    attachment = attachment,
                    result = result,
                    progressCallback = progressCallback,
                )
            }
        }
    }

    /**
     * Updates the upload state and calls the appropriate [ProgressCallback]
     * method.
     *
     * @param augmentedAttachment The attachment pre filled with
     * the appropriate fields after the file contained in the attachment
     * was uploaded.
     * @param progressCallback Used to listen to file upload
     * progress, success, and failure.
     *
     * @return The resulting successfully uploaded attachment.
     * */
    private fun onSuccessfulUpload(
        augmentedAttachment: Attachment,
        progressCallback: ProgressCallback?,
    ): Result<Attachment> {
        logger.d { "[onSuccessfulUpload] #uploader; attachment ${augmentedAttachment.uploadId} uploaded successfully" }
        progressCallback?.onSuccess(augmentedAttachment.url)
        return Result.Success(augmentedAttachment.copy(uploadState = Attachment.UploadState.Success))
    }

    /**
     * Updates the upload state and calls the appropriate [ProgressCallback]
     * method.
     *
     * @param attachment The attachment that has failed to upload.
     * @param result The result of the failed upload.
     * @param progressCallback Used to listen to file upload
     * progress, success, and failure.
     *
     * @return Returns a [Result] containing a [io.getstream.result.Error]
     * */
    private fun onFailedUpload(
        attachment: Attachment,
        result: Result.Failure,
        progressCallback: ProgressCallback?,
    ): Result<Attachment> {
        logger.e { "[onFailedUpload] #uploader; attachment ${attachment.uploadId} upload failed: ${result.value}" }
        progressCallback?.onError(result.value)
        return Result.Failure(result.value)
    }

    /**
     * Augment an attachment instance with data from uploaded file, mimeType, attachmentType and obtained from backend
     * url.
     *
     * @param file A file that has been uploaded.
     * @param mimeType MimeType of uploaded attachment.
     * @param attachmentType File, video or picture enum instance.
     * @param url URL obtained from BE.
     * @param thumbUrl The thumbnail obtained from the BE.
     * Usually returned for uploaded videos, can be null otherwise.
     */
    private fun Attachment.augmentAttachmentOnSuccess(
        file: File,
        mimeType: String,
        attachmentType: AttachmentType,
        url: String,
        thumbUrl: String? = null,
    ): Attachment {
        return copy(
            name = file.name,
            fileSize = file.length().toInt(),
            mimeType = mimeType,
            url = url,
            uploadState = Attachment.UploadState.Success,
            title = title.takeUnless { it.isNullOrBlank() } ?: file.name,
            thumbUrl = thumbUrl,
            type = type ?: attachmentType.toString(),
            imageUrl = when (attachmentType) {
                AttachmentType.IMAGE -> url
                AttachmentType.VIDEO -> thumbUrl
                else -> imageUrl
            },
            assetUrl = when (attachmentType) {
                AttachmentType.IMAGE -> assetUrl
                else -> url
            },
        )
    }

    private fun String?.toAttachmentType(): AttachmentType {
        if (this == null) {
            return AttachmentType.FILE
        }
        return when {
            ImageMimeTypes.isImageMimeTypeSupported(this) -> AttachmentType.IMAGE
            this.contains("video") -> AttachmentType.VIDEO
            else -> AttachmentType.FILE
        }
    }

    private enum class AttachmentType(private val value: String) {
        IMAGE("image"),
        VIDEO("video"),
        FILE("file"),
        ;

        override fun toString(): String {
            return value
        }
    }
}
