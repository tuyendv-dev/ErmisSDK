
package network.ermis.sample.feature.chat.info.shared.files

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import network.ermis.ui.ChatUI
import network.ermis.ui.common.utils.MediaStringUtil
import network.ermis.chat.ui.sample.databinding.ChatInfoSharedFileDateDividerBinding
import network.ermis.chat.ui.sample.databinding.ChatInfoSharedFileItemBinding
import network.ermis.sample.feature.chat.info.shared.SharedAttachment
import java.text.DateFormat

abstract class BaseViewHolder<T : SharedAttachment>(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    /**
     * Workaround to allow a downcast of the SharedAttachment to T.
     */
    @Suppress("UNCHECKED_CAST")
    internal fun bindListItem(item: SharedAttachment) = bind(item as T)

    protected abstract fun bind(item: T)
}

class ChatInfoSharedFileViewHolder(
    private val binding: ChatInfoSharedFileItemBinding,
    attachmentClickListener: ChatInfoSharedFilesAdapter.AttachmentClickListener?,
) : BaseViewHolder<SharedAttachment.AttachmentGetItem>(binding.root) {

    private lateinit var attachmentItem: SharedAttachment.AttachmentGetItem

    init {
        binding.ivDownload.setOnClickListener { attachmentClickListener?.onClick(attachmentItem) }
    }

    override fun bind(item: SharedAttachment.AttachmentGetItem) {
        attachmentItem = item
        with(item.attachmentGet) {
            binding.fileTypeImageView.setImageResource(ChatUI.mimeTypeIconProvider.getIconRes(content_type))
            binding.fileNameTextView.text = file_name
            binding.fileSizeTextView.text = MediaStringUtil.convertFileSizeByteCount(content_length)
        }
    }
}

class ChatInfoSharedFileDateDividerViewHolder(
    private val binding: ChatInfoSharedFileDateDividerBinding,
    private val dateFormat: DateFormat,
) : BaseViewHolder<SharedAttachment.DateDivider>(binding.root) {

    override fun bind(item: SharedAttachment.DateDivider) {
        binding.dateLabel.text = dateFormat.format(item.date)
    }
}
