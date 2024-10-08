
package network.ermis.ui.navigation.destinations

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import network.ermis.client.utils.attachment.isAudio
import network.ermis.client.utils.attachment.isFile
import network.ermis.client.utils.attachment.isImage
import network.ermis.client.utils.attachment.isVideo
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType
import network.ermis.core.models.Message
import network.ermis.ui.common.R
import network.ermis.ui.common.feature.AttachmentDocumentActivity
import network.ermis.ui.view.gallery.AttachmentActivity
import network.ermis.ui.view.gallery.AttachmentMediaActivity
import network.ermis.ui.utils.load
import network.ermis.ui.utils.model.MimeType
import io.getstream.log.taggedLogger
import io.getstream.photoview.dialog.PhotoViewDialog

public open class AttachmentDestination(
    public var message: Message,
    public var attachment: Attachment,
    context: Context,
) : ChatDestination(context) {

    private val logger by taggedLogger("Chat:AttachmentDestination")

    override fun navigate() {
        showAttachment(message, attachment)
    }

    public fun showAttachment(message: Message, attachment: Attachment) {
        if (attachment.isFile() ||
            attachment.isVideo() ||
            attachment.isAudio() ||
            attachment.mimeType?.contains(VIDEO_MIME_TYPE_PREFIX) == true
        ) {
            loadFile(attachment)
            return
        }

        var url: String? = null
        var type: String? = attachment.type

        when (attachment.type) {
            AttachmentType.IMAGE -> {
                when {
                    attachment.titleLink != null || attachment.ogUrl != null || attachment.assetUrl != null -> {
                        url = attachment.titleLink ?: attachment.ogUrl ?: attachment.assetUrl
                        type = AttachmentType.LINK
                    }

                    attachment.isGif() -> {
                        url = attachment.imageUrl
                        type = AttachmentType.GIPHY
                    }

                    else -> {
                        showImageViewer(message, attachment)
                        return
                    }
                }
            }

            AttachmentType.GIPHY -> url = attachment.thumbUrl
            AttachmentType.PRODUCT -> url = attachment.url
        }

        if (url.isNullOrEmpty()) {
            logger.e { "Wrong URL for attachment. Attachment: $attachment" }
            Toast.makeText(
                context,
                context.getString(R.string.ermis_ui_message_list_attachment_invalid_url),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        val intent = Intent(context, AttachmentActivity::class.java).apply {
            putExtra("type", type)
            putExtra("url", url)
        }
        start(intent)
    }

    private fun loadFile(attachment: Attachment) {
        val mimeType = attachment.mimeType
        val url = attachment.assetUrl
        val type = attachment.type
        val title = attachment.title ?: attachment.name ?: ""

        if (mimeType == null && type == null) {
            logger.e { "MimeType is null for url $url" }
            Toast.makeText(
                context,
                context.getString(R.string.ermis_ui_message_list_attachment_invalid_mime_type, attachment.name),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        // Media
        when {
            playableContent(mimeType, type) -> {
                val intent = AttachmentMediaActivity.createIntent(
                    context = context,
                    url = url ?: "",
                    title = title,
                    mimeType = mimeType,
                    type = type,
                )
                start(intent)
            }

            docMimeType(mimeType) -> {
                val intent = Intent(context, AttachmentDocumentActivity::class.java).apply {
                    putExtra("url", url)
                }
                start(intent)
            }

            else -> {
                logger.e { "Could not load attachment. Mimetype: $mimeType. Type: $type" }
                Toast.makeText(
                    context,
                    context.getString(R.string.ermis_ui_message_list_attachment_invalid_mime_type, attachment.name),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun playableContent(mimeType: String?, type: String?): Boolean {
        return mimeType?.contains("audio") == true ||
            mimeType?.contains(VIDEO_MIME_TYPE_PREFIX) == true ||
            mimeType?.contains(MP4_MIME_TYPE) == true ||
            mimeType?.contains(MPEG_MIME_TYPE) == true ||
            mimeType?.contains(QUICKTIME_MIME_TYPE) == true ||
            type == AUDIO_TYPE ||
            type == VIDEO_TYPE
    }

    private fun docMimeType(mimeType: String?): Boolean {
        return mimeType == MimeType.MIME_TYPE_DOC ||
            mimeType == MimeType.MIME_TYPE_TXT ||
            mimeType == MimeType.MIME_TYPE_PDF ||
            mimeType == MimeType.MIME_TYPE_HTML ||
            mimeType?.contains("application/vnd") == true
    }

    protected open fun showImageViewer(
        message: Message,
        attachment: Attachment,
    ) {
        val imageUrls: List<String> = message.attachments
            .filter { it.isImage() && !it.imageUrl.isNullOrEmpty() }
            .mapNotNull(Attachment::imageUrl)

        if (imageUrls.isEmpty()) {
            Toast.makeText(context, "Invalid image(s)!", Toast.LENGTH_SHORT).show()
            return
        }

        val attachmentIndex = message.attachments.indexOf(attachment)

        PhotoViewDialog
            .Builder(context, imageUrls, ImageView::load)
            .withStartPosition(
                if (attachmentIndex in imageUrls.indices) attachmentIndex else 0,
            )
            .show()
    }

    private fun Attachment.isGif() = mimeType?.contains("gif") ?: false

    private companion object {
        private const val VIDEO_MIME_TYPE_PREFIX = "video"
        private const val MP4_MIME_TYPE = "mp4"
        private const val MPEG_MIME_TYPE = "audio/mpeg"
        private const val QUICKTIME_MIME_TYPE = "quicktime"
        private const val VIDEO_TYPE = "video"
        private const val AUDIO_TYPE = "audio"
    }
}
