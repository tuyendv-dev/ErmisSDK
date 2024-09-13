package network.ermis.client.uploader

import network.ermis.client.api.RetrofitCdnApi
import network.ermis.client.api.mapping.toUploadedFile
import network.ermis.client.utils.extensions.getMediaType
import network.ermis.client.utils.ProgressCallback
import network.ermis.core.models.UploadedFile
import network.ermis.core.models.UploadedImage
import io.getstream.result.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

internal class ErmisFileUploader(
    private val retrofitCdnApi: RetrofitCdnApi,
) : FileUploader {

    private val filenameSanitizer = FilenameSanitizer()

    override fun sendFile(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
        callback: ProgressCallback,
    ): Result<UploadedFile> {
        val body = file.asRequestBody(file.getMediaType())
        val filename = filenameSanitizer.sanitize(file.name)
        val part = MultipartBody.Part.createFormData("file", filename, body)

        return retrofitCdnApi.sendFile(
            channelType = channelType,
            channelId = channelId,
            file = part,
            progressCallback = callback,
        ).execute().map {
            it.toUploadedFile()
        }
    }

    override fun sendFile(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
    ): Result<UploadedFile> {
        val body = file.asRequestBody(file.getMediaType())
        val filename = filenameSanitizer.sanitize(file.name)
        val part = MultipartBody.Part.createFormData("file", filename, body)

        return retrofitCdnApi.sendFile(
            channelType = channelType,
            channelId = channelId,
            file = part,
            progressCallback = null,
        ).execute().map {
            it.toUploadedFile()
        }
    }

    override fun sendImage(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
        callback: ProgressCallback,
    ): Result<UploadedImage> {
        val body = file.asRequestBody(file.getMediaType())
        val filename = filenameSanitizer.sanitize(file.name)
        val part = MultipartBody.Part.createFormData("file", filename, body)

        return retrofitCdnApi.sendFile(
            channelType = channelType,
            channelId = channelId,
            file = part,
            progressCallback = callback,
        ).execute().map {
            UploadedImage(file = it.file)
        }
    }

    override fun sendImage(
        channelType: String,
        channelId: String,
        userId: String,
        file: File,
    ): Result<UploadedImage> {
        val body = file.asRequestBody(file.getMediaType())
        val filename = filenameSanitizer.sanitize(file.name)
        val part = MultipartBody.Part.createFormData("file", filename, body)

        return retrofitCdnApi.sendFile(
            channelType = channelType,
            channelId = channelId,
            file = part,
            progressCallback = null,
        ).execute().map {
            UploadedImage(file = it.file)
        }
    }

    override fun deleteFile(
        channelType: String,
        channelId: String,
        userId: String,
        url: String,
    ): Result<Unit> {
        return retrofitCdnApi.deleteFile(
            channelType = channelType,
            channelId = channelId,
            url = url,
        ).execute().toUnitResult()
    }

    override fun deleteImage(
        channelType: String,
        channelId: String,
        userId: String,
        url: String,
    ): Result<Unit> {
        return retrofitCdnApi.deleteImage(
            channelType = channelType,
            channelId = channelId,
            url = url,
        ).execute().toUnitResult()
    }
}

private class FilenameSanitizer {
    companion object {
        private const val MAX_NAME_LEN = 255
        private const val EMPTY = ""
    }

    private val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '_'

    fun sanitize(filename: String): String = try {
        sanitizeInternal(filename)
    } catch (_: Throwable) {
        filename
    }

    private fun sanitizeInternal(filename: String): String {
        // Separate the extension and the base name
        val extension = filename.substringAfterLast(delimiter = '.', missingDelimiterValue = EMPTY)
        val baseName = if (extension.isNotEmpty()) filename.removeSuffix(suffix = ".$extension") else filename

        // Replace invalid characters in the base name
        var sanitizedBaseName = baseName.map { if (it in allowedChars) it else '_' }.joinToString(EMPTY)

        // Truncate the base name if it is too long
        val maxBaseNameLength = MAX_NAME_LEN - extension.length - 1 // Adjust for the extension and dot
        if (sanitizedBaseName.length > maxBaseNameLength) {
            sanitizedBaseName = sanitizedBaseName.substring(0, maxBaseNameLength)
        }

        // Reconstruct the filename with the extension
        return if (extension.isNotEmpty()) "$sanitizedBaseName.$extension" else sanitizedBaseName
    }
}
