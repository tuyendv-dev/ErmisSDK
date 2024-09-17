package network.ermis.ui.utils.extension

import network.ermis.client.utils.attachment.isAudio
import network.ermis.client.utils.attachment.isFile
import network.ermis.client.utils.attachment.isVideo
import network.ermis.client.utils.extensions.uploadId
import network.ermis.core.models.Attachment

/**
 * @return If the [Attachment] is a file or not.
 */
public fun Attachment.isAnyFileType(): Boolean {
    return uploadId != null ||
        upload != null ||
        isFile() ||
        isVideo() ||
        isAudio()
}

/**
 * @return If the attachment is currently being uploaded to the server.
 */
public fun Attachment.isUploading(): Boolean {
    return (uploadState is Attachment.UploadState.InProgress || uploadState is Attachment.UploadState.Idle) &&
        upload != null &&
        uploadId != null
}

/**
 * @return If the attachment has been failed when uploading to the server.
 */
public fun Attachment.isFailed(): Boolean {
    return (uploadState is Attachment.UploadState.Failed) &&
        upload != null &&
        uploadId != null
}

/**
 * @return If the [Attachment] is a link attachment or not.
 */
public fun Attachment.hasLink(): Boolean = titleLink != null || ogUrl != null
