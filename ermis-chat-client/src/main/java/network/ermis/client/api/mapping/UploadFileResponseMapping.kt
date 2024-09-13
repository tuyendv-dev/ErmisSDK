package network.ermis.client.api.mapping

import network.ermis.client.api.models.UploadFileResponse
import network.ermis.core.models.UploadedFile

internal fun UploadFileResponse.toUploadedFile() =
    UploadedFile(
        file = this.file,
        thumbUrl = this.thumb_url,
    )
