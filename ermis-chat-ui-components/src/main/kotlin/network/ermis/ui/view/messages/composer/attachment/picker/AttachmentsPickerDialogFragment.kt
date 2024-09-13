
package network.ermis.ui.view.messages.composer.attachment.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.FrameLayout
import androidx.core.view.descendants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import network.ermis.core.models.Attachment
import network.ermis.ui.components.R
import network.ermis.ui.common.state.messages.composer.AttachmentMetaData
import network.ermis.ui.components.databinding.DialogAttachmentBinding
import network.ermis.ui.view.messages.composer.attachment.picker.factory.AttachmentsPickerTabFactories
import network.ermis.ui.view.messages.composer.attachment.picker.factory.AttachmentsPickerTabFactory
import network.ermis.ui.view.messages.composer.attachment.picker.factory.AttachmentsPickerTabListener
import network.ermis.ui.view.messages.composer.attachment.picker.internal.AttachmentDialogPagerAdapter
import network.ermis.ui.view.messages.composer.internal.toAttachment
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * Represent the bottom sheet dialog that allows users to pick attachments.
 */
public class AttachmentsPickerDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogAttachmentBinding? = null
    private val binding get() = _binding!!

    /**
     * Style for the dialog.
     */
    private lateinit var style: AttachmentsPickerDialogStyle

    /**
     * The list of factories for the tabs that will be displayed in the attachment picker.
     */
    private lateinit var attachmentsPickerTabFactories: List<AttachmentsPickerTabFactory>

    /**
     * A listener that is invoked when attachment picking has been completed
     */
    private var attachmentSelectionListener: AttachmentSelectionListener? = AttachmentSelectionListener { attachments ->
        attachmentsSelectionListener?.onAttachmentsSelected(attachments.map { it.toAttachment(requireContext()) })
    }

    /**
     * A listener that is invoked when attachment picking has been completed
     */
    private var attachmentsSelectionListener: AttachmentsSelectionListener? = null

    /**
     * The list of currently selected attachments.
     */
    private var selectedAttachments: List<AttachmentMetaData> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogAttachmentBinding.inflate(requireContext().streamThemeInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null && ::style.isInitialized && ::attachmentsPickerTabFactories.isInitialized) {
            setupDialog()
        } else {
            dismiss()
        }
    }

    /**
     * Initializes the dialog.
     */
    private fun setupDialog() {
        binding.container.setBackgroundColor(style.attachmentsPickerBackgroundColor)

        setupAttachButton()
        setupTabs()
        setupPages()
    }

    /**
     * Initializes the submit attachments button.
     */
    private fun setupAttachButton() {
        binding.attachButton.setImageDrawable(style.submitAttachmentsButtonIconDrawable)
        binding.attachButton.isEnabled = false
        binding.attachButton.setOnClickListener {
            attachmentSelectionListener?.onAttachmentsSelected(selectedAttachments)
            dismiss()
        }
    }

    /**
     * Initializes the tabs of the picker.
     */
    private fun setupTabs() {
        attachmentsPickerTabFactories.forEachIndexed { index, factory ->
            val frameLayout = layoutInflater.inflate(
                R.layout.dialog_attachment_tab,
                binding.attachmentButtonsContainer,
                false,
            ) as FrameLayout

            val checkedTextView = frameLayout.findViewById<CheckedTextView>(R.id.checkedTextView)
            checkedTextView.background = factory.createTabIcon(style)
            checkedTextView.backgroundTintList = style.attachmentTabToggleButtonStateList
            checkedTextView.isChecked = index == 0

            frameLayout.setOnClickListener {
                setSelectedTab(checkedTextView, index)
            }

            binding.attachmentButtonsContainer.addView(frameLayout)
        }
    }

    /**
     * Initializes the content of the picker.
     */
    private fun setupPages() {
        val attachmentsPickerTabListener: AttachmentsPickerTabListener = object : AttachmentsPickerTabListener {
            override fun onSelectedAttachmentsChanged(attachments: List<AttachmentMetaData>) {
                selectedAttachments = attachments

                val hasSelectedAttachments = selectedAttachments.isNotEmpty()
                setAttachButtonEnabled(hasSelectedAttachments)
                setUnselectedButtonsEnabled(!hasSelectedAttachments)
            }

            override fun onSelectedAttachmentsSubmitted() {
                attachmentSelectionListener?.onAttachmentsSelected(selectedAttachments)
                dismiss()
            }
        }

        binding.attachmentPager.adapter = AttachmentDialogPagerAdapter(
            fragment = this,
            style = style,
            attachmentsPickerTabFactories = attachmentsPickerTabFactories,
            attachmentsPickerTabListener = attachmentsPickerTabListener,
        )
        binding.attachmentPager.isUserInputEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        attachmentSelectionListener = null
    }

    override fun getTheme(): Int = R.style.ermisUiAttachmentBottomSheetDialog

    /**
     * Initializes the dialog with the style.
     *
     * @param style Style for the dialog.
     */
    public fun setStyle(style: AttachmentsPickerDialogStyle) {
        this.style = style
    }

    /**
     * Sets the list of factories for the tabs that will be displayed in the attachment picker.
     */
    public fun setAttachmentsPickerTabFactories(attachmentsPickerTabFactories: List<AttachmentsPickerTabFactory>) {
        this.attachmentsPickerTabFactories = attachmentsPickerTabFactories
    }

    /**
     * Sets the listener that will be notified when picking attachments has been completed.
     */
    @Suppress("MaxLineLength")
    @Deprecated(
        message = "Use the new [AttachmentsSelectionListener] interface instead",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith(
            "setAttachmentsSelectionListener(attachmentsSelectionListener)",
            "network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogFragment.AttachmentsSelectionListener",
        ),
    )
    public fun setAttachmentSelectionListener(attachmentSelectionListener: AttachmentSelectionListener) {
        this.attachmentSelectionListener = attachmentSelectionListener
    }

    /**
     * Sets the listener that will be notified when picking attachments has been completed.
     */
    public fun setAttachmentsSelectionListener(attachmentsSelectionListener: AttachmentsSelectionListener) {
        this.attachmentsSelectionListener = attachmentsSelectionListener
    }

    private fun setSelectedTab(checkedTextView: CheckedTextView, pagePosition: Int) {
        binding.attachmentPager.setCurrentItem(pagePosition, false)
        binding.attachmentButtonsContainer.descendants.forEach {
            (it as? CheckedTextView)?.isChecked = it == checkedTextView
        }
    }

    private fun setAttachButtonEnabled(isEnabled: Boolean) {
        binding.attachButton.isEnabled = isEnabled
    }

    private fun setUnselectedButtonsEnabled(isEnabled: Boolean) {
        binding.attachmentButtonsContainer.descendants.forEach {
            if (it is CheckedTextView && !it.isChecked) {
                it.isEnabled = isEnabled
            }
        }
    }

    public companion object {
        public const val TAG: String = "attachment_dialog_fragment"

        /**
         * Creates a new instance of [AttachmentsPickerDialogFragment].
         *
         * @param style Style for the dialog.
         * @param attachmentsPickerTabFactories The list of factories for the tabs in the attachment picker.
         * @return A new instance of [AttachmentsPickerDialogFragment].
         */
        public fun newInstance(
            style: AttachmentsPickerDialogStyle,
            attachmentsPickerTabFactories: List<AttachmentsPickerTabFactory> = AttachmentsPickerTabFactories
                .defaultFactories(
                    mediaAttachmentsTabEnabled = style.mediaAttachmentsTabEnabled,
                    fileAttachmentsTabEnabled = style.fileAttachmentsTabEnabled,
                    cameraAttachmentsTabEnabled = style.cameraAttachmentsTabEnabled,
                ),
        ): AttachmentsPickerDialogFragment {
            return AttachmentsPickerDialogFragment().apply {
                setStyle(style)
                setAttachmentsPickerTabFactories(attachmentsPickerTabFactories)
            }
        }
    }

    public fun interface AttachmentsSelectionListener {
        /**
         * Called when attachment picking has been completed.
         *
         * @param attachments The list of selected attachments.
         */
        public fun onAttachmentsSelected(attachments: List<Attachment>)
    }
}
