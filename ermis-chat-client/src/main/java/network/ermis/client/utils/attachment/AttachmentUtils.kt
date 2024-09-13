package network.ermis.client.utils.attachment

import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType

/**
 * @return If the attachment type is image.
 */
public fun Attachment.isImage(): Boolean = type == AttachmentType.IMAGE

/**
 * @return If the attachment type is video.
 */
public fun Attachment.isVideo(): Boolean = type == AttachmentType.VIDEO

/**
 * @return If the attachment type is video.
 */
public fun Attachment.isAudio(): Boolean = type == AttachmentType.AUDIO

/**
 * @return If the attachment type is file.
 */
public fun Attachment.isFile(): Boolean = type == AttachmentType.FILE

/**
 * @return If the attachment type is giphy.
 */
public fun Attachment.isGiphy(): Boolean = type == AttachmentType.GIPHY

/**
 * @return If the attachment type is imgur.
 */
public fun Attachment.isImgur(): Boolean = type == AttachmentType.IMGUR

/**
 * @return If the attachment type is link.
 */
public fun Attachment.isLink(): Boolean = type == AttachmentType.LINK

/**
 * @return If the attachment type is audio recording.
 */
public fun Attachment.isAudioRecording(): Boolean = type == AttachmentType.AUDIO_RECORDING
