package network.ermis.ui.common.utils.extensions

import network.ermis.core.models.Attachment
import network.ermis.ui.common.helper.StorageHelper
import network.ermis.ui.common.utils.StringUtils

public fun Attachment.getDisplayableName(): String? {
    return StringUtils.removeTimePrefix(title ?: name ?: upload?.name, StorageHelper.TIME_FORMAT)
}

public val Attachment.imagePreviewUrl: String?
    get() = thumbUrl ?: imageUrl
