
package network.ermis.ui.view.messages.composer.internal

import android.content.Context
import network.ermis.core.models.Attachment
import network.ermis.ui.common.helper.StorageHelper
import network.ermis.ui.common.state.messages.composer.AttachmentMetaData

internal fun AttachmentMetaData.toAttachment(context: Context): Attachment {
    val fileFromUri = StorageHelper().getCachedFileFromUri(context, this)
    return Attachment(
        upload = fileFromUri,
        type = type,
        name = title ?: fileFromUri?.name ?: "",
        fileSize = size.toInt(),
        mimeType = mimeType,
        title = title,
    )
}
