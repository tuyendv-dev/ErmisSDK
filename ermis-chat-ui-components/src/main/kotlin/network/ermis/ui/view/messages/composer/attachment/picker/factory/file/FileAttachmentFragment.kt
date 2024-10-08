
package network.ermis.ui.view.messages.composer.attachment.picker.factory.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import network.ermis.core.internal.coroutines.DispatcherProvider
import network.ermis.ui.components.R
import network.ermis.ui.common.contract.SelectFilesContract
import network.ermis.ui.common.helper.AttachmentFilter
import network.ermis.ui.common.helper.StorageHelper
import network.ermis.ui.common.state.messages.composer.AttachmentMetaData
import network.ermis.ui.components.databinding.FragmentAttachmentFileBinding
import network.ermis.ui.view.messages.composer.attachment.picker.AttachmentsPickerDialogStyle
import network.ermis.ui.view.messages.composer.attachment.picker.factory.AttachmentsPickerTabListener
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.PermissionChecker
import network.ermis.ui.utils.extensions.streamThemeInflater
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Represents the tab of the attachment picker with a list of files.
 */
internal class FileAttachmentFragment : Fragment() {

    private var _binding: FragmentAttachmentFileBinding? = null
    private val binding get() = _binding!!

    private val storageHelper: StorageHelper = StorageHelper()
    private val permissionChecker: PermissionChecker = PermissionChecker()
    private val attachmentFilter: AttachmentFilter = AttachmentFilter()
    private var activityResultLauncher: ActivityResultLauncher<Unit>? = null

    /**
     * Style for the attachment picker dialog.
     */
    private lateinit var style: AttachmentsPickerDialogStyle

    /**
     * A listener invoked when attachments are selected in the attachment tab.
     */
    private var attachmentsPickerTabListener: AttachmentsPickerTabListener? = null

    /**
     * Initializes the dialog with the style.
     *
     * @param style Style for the dialog.
     */
    fun setStyle(style: AttachmentsPickerDialogStyle) {
        this.style = style
    }

    private val fileAttachmentsAdapter: FileAttachmentAdapter by lazy {
        FileAttachmentAdapter(style) {
            updateFileAttachment(it)
        }
    }

    private var selectedAttachments: Set<AttachmentMetaData> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAttachmentFileBinding.inflate(requireContext().streamThemeInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (::style.isInitialized) {
            setupViews()
            setupResultListener()
            checkPermissions()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityResultLauncher?.unregister()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        attachmentsPickerTabListener = null
    }

    private fun updateFileAttachment(attachmentMetaData: AttachmentMetaData) {
        if (attachmentMetaData.isSelected) {
            attachmentMetaData.isSelected = false
            selectedAttachments = selectedAttachments - attachmentMetaData
            fileAttachmentsAdapter.deselectAttachment(attachmentMetaData)
        } else {
            attachmentMetaData.isSelected = true
            selectedAttachments = selectedAttachments + attachmentMetaData
            fileAttachmentsAdapter.selectAttachment(attachmentMetaData)
        }
        attachmentsPickerTabListener?.onSelectedAttachmentsChanged(selectedAttachments.toList())
    }

    private fun setupViews() {
        binding.apply {
            grantPermissionsInclude.grantPermissionsImageView.setImageDrawable(style.allowAccessToFilesIconDrawable)
            grantPermissionsInclude.grantPermissionsTextView.text = style.allowAccessToFilesButtonText
            grantPermissionsInclude.grantPermissionsTextView.setTextStyle(style.allowAccessButtonTextStyle)
            grantPermissionsInclude.grantPermissionsTextView.setOnClickListener {
                checkPermissions()
            }
            recentFilesRecyclerView.adapter = fileAttachmentsAdapter
            fileManagerImageView.setImageDrawable(style.fileManagerIconDrawable)
            recentFilesTextView.text = style.recentFilesText
            recentFilesTextView.setTextStyle(style.recentFilesTextStyle)
            fileManagerImageView.setOnClickListener {
                activityResultLauncher?.launch(Unit)
            }
        }
    }

    private fun checkPermissions() {
        if (!permissionChecker.isGrantedFilePermissions(requireContext())) {
            permissionChecker.checkFilePermissions(
                binding.root,
                onPermissionDenied = ::onPermissionDenied,
                onPermissionGranted = ::onPermissionGranted,
            )
            return
        }
        onPermissionGranted()
    }

    private fun setupResultListener() {
        activityResultLauncher = activity?.activityResultRegistry
            ?.register(LauncherRequestsKeys.SELECT_FILES, SelectFilesContract()) {
                lifecycleScope.launch(DispatcherProvider.Main) {
                    val attachments = withContext(DispatcherProvider.IO) {
                        storageHelper.getAttachmentsFromUriList(requireContext(), it)
                    }
                    val filteredAttachments = attachmentFilter.filterAttachments(attachments)

                    if (filteredAttachments.size < attachments.size) {
                        Toast.makeText(
                            context,
                            getString(R.string.ermis_ui_message_composer_file_not_supported),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                    attachmentsPickerTabListener?.onSelectedAttachmentsChanged(filteredAttachments)
                    attachmentsPickerTabListener?.onSelectedAttachmentsSubmitted()
                }
            }
    }

    /**
     * Sets the listener invoked when attachments are selected in the attachment tab.
     */
    fun setAttachmentsPickerTabListener(attachmentsPickerTabListener: AttachmentsPickerTabListener) {
        this.attachmentsPickerTabListener = attachmentsPickerTabListener
    }

    private fun onPermissionGranted() {
        binding.grantPermissionsInclude.grantPermissionsContainer.isVisible = false
        populateAttachments()
    }

    private fun onPermissionDenied() {
        binding.grantPermissionsInclude.grantPermissionsContainer.isVisible = true
    }

    private fun populateAttachments() {
        lifecycleScope.launch(DispatcherProvider.Main) {
            binding.progressBar.isVisible = true

            val attachments = withContext(DispatcherProvider.IO) {
                storageHelper.getFileAttachments(requireContext())
            }
            val filteredAttachments = attachmentFilter.filterAttachments(attachments)

            if (filteredAttachments.isEmpty()) {
                binding.emptyPlaceholderTextView.setTextStyle(style.fileAttachmentsNoFilesTextStyle)
                binding.emptyPlaceholderTextView.text = style.fileAttachmentsNoFilesText
                binding.emptyPlaceholderTextView.isVisible = true
            } else {
                fileAttachmentsAdapter.setAttachments(filteredAttachments)
            }
            binding.progressBar.isVisible = false
        }
    }

    private object LauncherRequestsKeys {
        const val SELECT_FILES = "select_files_request_key"
    }

    companion object {
        /**
         * Creates a new instance of [FileAttachmentFragment].
         *
         * @param style The style for the attachment picker dialog.
         * @return A new instance of the Fragment.
         */
        fun newInstance(style: AttachmentsPickerDialogStyle): FileAttachmentFragment {
            return FileAttachmentFragment().apply {
                setStyle(style)
            }
        }
    }
}
