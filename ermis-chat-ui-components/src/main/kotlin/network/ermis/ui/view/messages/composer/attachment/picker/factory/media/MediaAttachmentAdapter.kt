
package network.ermis.ui.view.messages.composer.attachment.picker.factory.media

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.AttachmentType
import network.ermis.ui.components.R
import network.ermis.ui.common.state.messages.composer.AttachmentMetaData
import network.ermis.ui.common.utils.MediaStringUtil
import network.ermis.ui.components.databinding.ItemAttachmentMediaBinding
import network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogStyle
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.applyTint
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.load
import network.ermis.ui.utils.loadVideoThumbnail

internal class MediaAttachmentAdapter(
    private val style: AttachmentsPickerDialogStyle,
    private val onAttachmentSelected: (attachmentMetaData: AttachmentMetaData) -> Unit,
) : RecyclerView.Adapter<MediaAttachmentAdapter.MediaAttachmentViewHolder>() {

    private val attachments: MutableList<AttachmentMetaData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaAttachmentViewHolder {
        return ItemAttachmentMediaBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { MediaAttachmentViewHolder(it, onAttachmentSelected, style) }
    }

    override fun onBindViewHolder(holder: MediaAttachmentViewHolder, position: Int) {
        holder.bind(attachments[position])
    }

    override fun getItemCount(): Int = attachments.size

    fun setAttachments(attachments: List<AttachmentMetaData>) {
        this.attachments.clear()
        this.attachments.addAll(attachments)
        notifyDataSetChanged()
    }

    fun selectAttachment(attachment: AttachmentMetaData) = toggleAttachmentSelection(attachment, true)

    fun deselectAttachment(attachment: AttachmentMetaData) = toggleAttachmentSelection(attachment, false)

    fun clearAttachments() {
        attachments.clear()
        notifyDataSetChanged()
    }

    private fun toggleAttachmentSelection(attachment: AttachmentMetaData, isSelected: Boolean) {
        val index = attachments.indexOf(attachment)
        if (index != -1) {
            attachments[index].isSelected = isSelected
            notifyItemChanged(index)
        }
    }

    class MediaAttachmentViewHolder(
        private val binding: ItemAttachmentMediaBinding,
        private val onAttachmentSelected: (attachmentMetaData: AttachmentMetaData) -> Unit,
        private val style: AttachmentsPickerDialogStyle,
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var attachment: AttachmentMetaData

        init {
            binding.root.setOnClickListener { onAttachmentSelected(attachment) }
        }

        fun bind(attachment: AttachmentMetaData) {
            this.attachment = attachment

            bindMediaImage(attachment)
            bindSelectionMark(attachment)
            bindSelectionOverlay(attachment)
            bindAttachmentType(attachment)
        }

        private fun bindMediaImage(attachment: AttachmentMetaData) {
            if (attachment.type == AttachmentType.VIDEO) {
                binding.mediaThumbnailImageView.loadVideoThumbnail(
                    uri = attachment.uri,
                    placeholderResId = R.drawable.placeholder,
                )
                val color = ContextCompat.getColor(itemView.context, R.color.ui_white_smoke)
                binding.mediaThumbnailImageView.setBackgroundColor(color)
            } else {
                binding.mediaThumbnailImageView.load(data = attachment.uri)
                binding.mediaThumbnailImageView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        private fun bindSelectionMark(attachment: AttachmentMetaData) {
            binding.selectionMarkImageView.isVisible = attachment.isSelected
        }

        private fun bindSelectionOverlay(attachment: AttachmentMetaData) {
            binding.selectionOverlayView.isVisible = attachment.isSelected
        }

        private fun bindAttachmentType(attachment: AttachmentMetaData) {
            if (attachment.type == AttachmentType.VIDEO) {
                binding.videoInformationConstraintLayout.isVisible =
                    style.videoLengthTextVisible || style.videoIconVisible
                binding.videoLengthTextView.isVisible = style.videoLengthTextVisible
                binding.videoLogoImageView.isVisible = style.videoIconVisible
                binding.videoLogoImageView.setImageDrawable(
                    style.videoIconDrawable.applyTint(style.videoIconDrawableTint),
                )
                binding.videoLengthTextView.setTextStyle(style.videoLengthTextStyle)
                binding.videoLengthTextView.text = MediaStringUtil.convertVideoLength(attachment.videoLength)
            } else {
                binding.videoInformationConstraintLayout.isVisible = false
                binding.videoLengthTextView.text = ""
            }
        }
    }
}
