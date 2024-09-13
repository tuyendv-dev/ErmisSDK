
package network.ermis.ui.utils

import android.webkit.MimeTypeMap
import android.widget.ImageView
import network.ermis.client.utils.attachment.isImage
import network.ermis.client.utils.attachment.isVideo
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType
import network.ermis.ui.ChatUI
import network.ermis.ui.common.disposable.Disposable
import network.ermis.ui.common.images.internal.ErmisImageLoader.ImageTransformation.RoundedCorners
import network.ermis.ui.common.images.resizing.applyStreamCdnImageResizingIfEnabled
import network.ermis.ui.common.state.messages.composer.AttachmentMetaData
import network.ermis.ui.common.utils.StreamFileUtil
import network.ermis.ui.common.utils.extensions.getDisplayableName
import network.ermis.ui.utils.extensions.dpToPxPrecise

private val FILE_THUMB_TRANSFORMATION = RoundedCorners(3.dpToPxPrecise())

internal fun ImageView.loadAttachmentThumb(attachment: Attachment): Disposable {
    return with(attachment) {
        when {
            isVideo() && ChatUI.videoThumbnailsEnabled && !thumbUrl.isNullOrBlank() ->
                load(
                    data = thumbUrl?.applyStreamCdnImageResizingIfEnabled(ChatUI.streamCdnImageResizing),
                    transformation = FILE_THUMB_TRANSFORMATION,
                )
            isImage() && !imageUrl.isNullOrBlank() ->
                load(
                    data = imageUrl?.applyStreamCdnImageResizingIfEnabled(ChatUI.streamCdnImageResizing),
                    transformation = FILE_THUMB_TRANSFORMATION,
                )
            else -> {
                // The mime type, or a best guess based on the extension
                val actualMimeType = mimeType ?: MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(attachment.getDisplayableName()?.substringAfterLast('.'))

                // We don't have icons for image types, but we can load the actual image in this case
                if (actualMimeType?.startsWith("image") == true && attachment.upload != null) {
                    load(
                        data = StreamFileUtil.getUriForFile(context, attachment.upload!!),
                        transformation = FILE_THUMB_TRANSFORMATION,
                    )
                } else {
                    load(data = ChatUI.mimeTypeIconProvider.getIconRes(actualMimeType))
                }
            }
        }
    }
}

internal fun ImageView.loadAttachmentThumb(attachment: AttachmentMetaData): Disposable {
    return with(attachment) {
        when (type) {
            AttachmentType.VIDEO -> loadVideoThumbnail(
                uri = uri,
                transformation = FILE_THUMB_TRANSFORMATION,
            )
            AttachmentType.IMAGE -> load(data = uri, transformation = FILE_THUMB_TRANSFORMATION)
            else -> load(data = ChatUI.mimeTypeIconProvider.getIconRes(mimeType))
        }
    }
}
