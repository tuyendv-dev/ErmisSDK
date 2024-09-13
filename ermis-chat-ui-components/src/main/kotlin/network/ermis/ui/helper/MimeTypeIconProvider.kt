
package network.ermis.ui.helper

import android.util.Log
import network.ermis.core.models.AttachmentType
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.composer.attachment.picker.factory.file.FileAttachmentAdapter
import network.ermis.ui.utils.model.MimeType

/**
 * Provides icons for file attachments.
 *
 * @see [FileAttachmentAdapter.FileAttachmentViewHolder]
 */
public fun interface MimeTypeIconProvider {

    /**
     * Returns a drawable resource for the given MIME type.
     *
     * @param mimeType The MIME type (i.e. application/pdf).
     * @return The drawable resource for the given MIME type.
     */
    public fun getIconRes(mimeType: String?): Int
}

/**
 * Provides icons for file attachments.
 */
public class MimeTypeIconProviderImpl : MimeTypeIconProvider {

    private val mimeTypesToIconResMap: Map<String, Int> = mapOf(
        MimeType.MIME_TYPE_PDF to R.drawable.ic_file_pdf,
        MimeType.MIME_TYPE_CSV to R.drawable.ic_file_csv,
        MimeType.MIME_TYPE_TAR to R.drawable.ic_file_tar,
        MimeType.MIME_TYPE_ZIP to R.drawable.ic_file_zip,
        MimeType.MIME_TYPE_RAR to R.drawable.ic_file_rar,
        MimeType.MIME_TYPE_7Z to R.drawable.ic_file_7z,
        MimeType.MIME_TYPE_DOC to R.drawable.ic_file_doc,
        MimeType.MIME_TYPE_DOCX to R.drawable.ic_file_docx,
        MimeType.MIME_TYPE_TXT to R.drawable.ic_file_txt,
        MimeType.MIME_TYPE_RTF to R.drawable.ic_file_rtf,
        MimeType.MIME_TYPE_HTML to R.drawable.ic_file_html,
        MimeType.MIME_TYPE_MD to R.drawable.ic_file_md,
        MimeType.MIME_TYPE_ODT to R.drawable.ic_file_odt,
        MimeType.MIME_TYPE_XLS to R.drawable.ic_file_xls,
        MimeType.MIME_TYPE_XLSX to R.drawable.ic_file_xlsx,
        MimeType.MIME_TYPE_PPT to R.drawable.ic_file_ppt,
        MimeType.MIME_TYPE_PPTX to R.drawable.ic_file_pptx,
        MimeType.MIME_TYPE_MOV to R.drawable.ic_file_mov,
        MimeType.MIME_TYPE_QUICKTIME to R.drawable.ic_file_mov,
        MimeType.MIME_TYPE_VIDEO_QUICKTIME to R.drawable.ic_file_mov,
        MimeType.MIME_TYPE_MP4 to R.drawable.ic_file_mov,
        MimeType.MIME_TYPE_VIDEO_MP4 to R.drawable.ic_file_mp4,
        MimeType.MIME_TYPE_AUDIO_MP4 to R.drawable.ic_file_mp3,
        MimeType.MIME_TYPE_M4A to R.drawable.ic_file_m4a,
        MimeType.MIME_TYPE_MP3 to R.drawable.ic_file_mp3,
        MimeType.MIME_TYPE_AAC to R.drawable.ic_file_aac,
    )

    /**
     * Returns a drawable resource for the given MIME type.
     *
     * @param mimeType The MIME type (i.e. application/pdf).
     * @return The drawable resource for the given MIME type.
     */
    public override fun getIconRes(mimeType: String?): Int {
        if (mimeType == null) {
            return R.drawable.ic_file
        }

        Log.d("MimeTypeIconProvider", "mimeType: $mimeType")

        return mimeTypesToIconResMap[mimeType] ?: when {
            mimeType.contains(AttachmentType.AUDIO) -> R.drawable.ic_file_audio_generic
            mimeType.contains(AttachmentType.VIDEO) -> R.drawable.ic_file_video_generic
            else -> R.drawable.ic_file
        }
    }
}
