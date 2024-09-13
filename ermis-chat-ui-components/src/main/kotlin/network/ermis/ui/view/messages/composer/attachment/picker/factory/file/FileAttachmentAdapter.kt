
package network.ermis.ui.view.messages.composer.attachment.picker.factory.file

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import network.ermis.ui.common.state.messages.composer.AttachmentMetaData
import network.ermis.ui.common.utils.MediaStringUtil
import network.ermis.ui.components.databinding.ItemAttachmentFileBinding
import network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogStyle
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.loadAttachmentThumb

internal class FileAttachmentAdapter(
    private val style: AttachmentsPickerDialogStyle,
    private val onAttachmentSelected: (AttachmentMetaData) -> Unit,
) : RecyclerView.Adapter<FileAttachmentAdapter.FileAttachmentViewHolder>() {

    private var attachments: List<AttachmentMetaData> = emptyList()

    override fun onBindViewHolder(holder: FileAttachmentViewHolder, position: Int) {
        holder.bind(attachments[position])
    }

    override fun getItemCount(): Int = attachments.size

    fun selectAttachment(attachment: AttachmentMetaData) = toggleSelection(attachment, true)

    fun deselectAttachment(attachment: AttachmentMetaData) = toggleSelection(attachment, false)

    fun setAttachments(attachments: List<AttachmentMetaData>) {
        this.attachments = attachments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileAttachmentViewHolder {
        return ItemAttachmentFileBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { FileAttachmentViewHolder(it, onAttachmentSelected, style) }
    }

    private fun toggleSelection(attachment: AttachmentMetaData, isSelected: Boolean) {
        val index = attachments.indexOf(attachment)
        if (index != -1) {
            attachments[index].isSelected = isSelected

            if (isSelected) {
                attachments[index].selectedPosition = attachments.count { it.isSelected }
                notifyItemChanged(index)
            } else {
                val prevSelectedPosition = attachments[index].selectedPosition
                attachments[index].selectedPosition = 0
                attachments.filter { it.selectedPosition > prevSelectedPosition }.forEach {
                    it.selectedPosition = it.selectedPosition - 1
                }

                notifyDataSetChanged()
            }
        }
    }

    class FileAttachmentViewHolder(
        private val binding: ItemAttachmentFileBinding,
        private val onAttachmentClick: (AttachmentMetaData) -> Unit,
        private val style: AttachmentsPickerDialogStyle,
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var attachment: AttachmentMetaData

        init {
            binding.root.setOnClickListener {
                onAttachmentClick(attachment)
            }

            binding.selectionIndicator.setTextColor(style.fileAttachmentItemCheckboxTextColor)
            binding.fileNameTextView.setTextStyle(style.fileAttachmentItemNameTextStyle)
            binding.fileSizeTextView.setTextStyle(style.fileAttachmentItemSizeTextStyle)
        }

        fun bind(attachment: AttachmentMetaData) {
            this.attachment = attachment

            binding.fileTypeImageView.loadAttachmentThumb(attachment)
            binding.fileNameTextView.text = attachment.title
            binding.fileSizeTextView.text = MediaStringUtil.convertFileSizeByteCount(attachment.size)

            binding.selectionIndicator.background = getSelectionIndicatorBackground(attachment.isSelected, style)
            binding.selectionIndicator.isChecked = attachment.isSelected
            binding.selectionIndicator.text = attachment.selectedPosition.takeIf { it > 0 }?.toString() ?: ""
        }

        private fun getSelectionIndicatorBackground(selected: Boolean, style: AttachmentsPickerDialogStyle): Drawable {
            return if (selected) {
                style.fileAttachmentItemCheckboxSelectedDrawable
            } else {
                style.fileAttachmentItemCheckboxDeselectedDrawable
            }
        }
    }
}
