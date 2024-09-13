package network.ermis.client.api.mapping

import network.ermis.client.api.model.dto.AttachmentDto
import network.ermis.core.models.Attachment

internal fun Attachment.toDto(): AttachmentDto =
    AttachmentDto(
        asset_url = assetUrl,
        author_name = authorName,
        fallback = fallback,
        file_size = fileSize,
        image = image,
        image_url = imageUrl,
        mime_type = mimeType,
        name = name,
        og_scrape_url = ogUrl,
        text = text,
        thumb_url = thumbUrl,
        title = title,
        title_link = titleLink,
        author_link = authorLink,
        type = type,
        url = url,
        original_height = originalHeight,
        original_width = originalWidth,
        extraData = extraData,
    )

internal fun AttachmentDto.toDomain(): Attachment =
    Attachment(
        assetUrl = asset_url,
        authorName = author_name,
        authorLink = author_link,
        fallback = fallback,
        fileSize = file_size,
        image = image,
        imageUrl = image_url,
        mimeType = mime_type,
        name = name,
        ogUrl = og_scrape_url,
        text = text,
        thumbUrl = thumb_url,
        title = title,
        titleLink = title_link,
        type = type,
        url = url,
        originalHeight = original_height,
        originalWidth = original_width,
        extraData = extraData.toMutableMap(),
    )
