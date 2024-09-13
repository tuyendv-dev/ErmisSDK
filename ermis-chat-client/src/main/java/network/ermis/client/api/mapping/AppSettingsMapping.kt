package network.ermis.client.api.mapping

import network.ermis.client.api.model.response.AppDto
import network.ermis.client.api.model.response.AppSettingsResponse
import network.ermis.client.api.model.response.FileUploadConfigDto
import network.ermis.core.models.App
import network.ermis.core.models.AppSettings
import network.ermis.core.models.FileUploadConfig

internal fun AppSettingsResponse.toDomain(): AppSettings {
    return AppSettings(
        app = app.toDomain(),
    )
}

internal fun AppDto.toDomain(): App {
    return App(
        name = name,
        fileUploadConfig = file_upload_config.toDomain(),
        imageUploadConfig = image_upload_config.toDomain(),
    )
}

internal fun FileUploadConfigDto.toDomain(): FileUploadConfig {
    return FileUploadConfig(
        allowedFileExtensions = allowed_file_extensions,
        allowedMimeTypes = allowed_mime_types,
        blockedFileExtensions = blocked_file_extensions,
        blockedMimeTypes = blocked_mime_types,
    )
}
