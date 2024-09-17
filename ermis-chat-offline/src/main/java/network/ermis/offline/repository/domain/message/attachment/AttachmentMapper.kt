package network.ermis.offline.repository.domain.message.attachment

import io.getstream.result.Error
import network.ermis.core.models.Attachment
import network.ermis.offline.repository.domain.message.attachment.UploadStateEntity.Companion.UPLOAD_STATE_FAILED
import network.ermis.offline.repository.domain.message.attachment.UploadStateEntity.Companion.UPLOAD_STATE_IN_PROGRESS
import network.ermis.offline.repository.domain.message.attachment.UploadStateEntity.Companion.UPLOAD_STATE_SUCCESS
import java.io.File

internal fun Attachment.toEntity(messageId: String, index: Int): AttachmentEntity {
    val mutableExtraData = extraData.toMutableMap()
    val generatedId = mutableExtraData.getOrPut(AttachmentEntity.EXTRA_DATA_ID_KEY) {
        AttachmentEntity.generateId(messageId, index)
    } as String
    return AttachmentEntity(
        id = generatedId,
        messageId = messageId,
        authorName = authorName,
        titleLink = titleLink,
        authorLink = authorLink,
        thumbUrl = thumbUrl,
        imageUrl = imageUrl,
        assetUrl = assetUrl,
        ogUrl = ogUrl,
        mimeType = mimeType,
        fileSize = fileSize,
        title = title,
        text = text,
        type = type,
        image = image,
        url = url,
        name = name,
        fallback = fallback,
        uploadFilePath = upload?.absolutePath,
        uploadState = uploadState?.toEntity(),
        originalHeight = originalHeight,
        originalWidth = originalWidth,
        extraData = mutableExtraData,
    )
}

internal fun Attachment.toReplyEntity(messageId: String, index: Int): ReplyAttachmentEntity {
    val mutableExtraData = extraData.toMutableMap()
    val generatedId = mutableExtraData.getOrPut(AttachmentEntity.EXTRA_DATA_ID_KEY) {
        AttachmentEntity.generateId(messageId, index)
    } as String
    return ReplyAttachmentEntity(
        id = generatedId,
        messageId = messageId,
        authorName = authorName,
        titleLink = titleLink,
        authorLink = authorLink,
        thumbUrl = thumbUrl,
        imageUrl = imageUrl,
        assetUrl = assetUrl,
        ogUrl = ogUrl,
        mimeType = mimeType,
        fileSize = fileSize,
        title = title,
        text = text,
        type = type,
        image = image,
        url = url,
        name = name,
        fallback = fallback,
        uploadFilePath = upload?.absolutePath,
        uploadState = uploadState?.toEntity(),
        originalHeight = originalHeight,
        originalWidth = originalWidth,
        extraData = mutableExtraData,
    )
}

internal fun AttachmentEntity.toModel(): Attachment = Attachment(
    authorName = authorName,
    titleLink = titleLink,
    authorLink = authorLink,
    thumbUrl = thumbUrl,
    imageUrl = imageUrl,
    assetUrl = assetUrl,
    ogUrl = ogUrl,
    mimeType = mimeType,
    fileSize = fileSize,
    title = title,
    text = text,
    type = type,
    image = image,
    url = url,
    name = name,
    fallback = fallback,
    upload = uploadFilePath?.let(::File),
    uploadState = uploadState?.toModel(uploadFilePath?.let(::File)),
    originalHeight = originalHeight,
    originalWidth = originalWidth,
    extraData = extraData,
)

internal fun ReplyAttachmentEntity.toModel(): Attachment = Attachment(
    authorName = authorName,
    titleLink = titleLink,
    authorLink = authorLink,
    thumbUrl = thumbUrl,
    imageUrl = imageUrl,
    assetUrl = assetUrl,
    ogUrl = ogUrl,
    mimeType = mimeType,
    fileSize = fileSize,
    title = title,
    text = text,
    type = type,
    image = image,
    url = url,
    name = name,
    fallback = fallback,
    upload = uploadFilePath?.let(::File),
    uploadState = uploadState?.toModel(uploadFilePath?.let(::File)),
    originalHeight = originalHeight,
    originalWidth = originalWidth,
    extraData = extraData,
)

private fun Attachment.UploadState.toEntity(): UploadStateEntity {
    val (statusCode, errorMessage) = when (this) {
        Attachment.UploadState.Success -> UPLOAD_STATE_SUCCESS to null
        Attachment.UploadState.Idle -> UPLOAD_STATE_IN_PROGRESS to null
        is Attachment.UploadState.InProgress -> UPLOAD_STATE_IN_PROGRESS to null
        is Attachment.UploadState.Failed -> UPLOAD_STATE_FAILED to (this.error.message)
    }
    return UploadStateEntity(statusCode, errorMessage)
}

private fun UploadStateEntity.toModel(uploadFile: File?): Attachment.UploadState = when (this.statusCode) {
    UPLOAD_STATE_SUCCESS -> Attachment.UploadState.Success
    UPLOAD_STATE_IN_PROGRESS -> Attachment.UploadState.InProgress(0, uploadFile?.length() ?: 0)
    UPLOAD_STATE_FAILED -> Attachment.UploadState.Failed(Error.GenericError(message = this.errorMessage ?: ""))
    else -> error("Integer value of $statusCode can't be mapped to UploadState")
}
