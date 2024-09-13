package network.ermis.sample.feature.chat.info.shared.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import network.ermis.client.ErmisClient
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType
import network.ermis.state.extensions.downloadAttachment
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.initToolbar
import network.ermis.chat.ui.sample.databinding.FragmentChatInfoSharedFilesBinding
import network.ermis.sample.feature.chat.info.shared.ChatInfoSharedAttachmentsViewModel
import network.ermis.sample.feature.chat.info.shared.SharedAttachment

class ChatInfoSharedFilesFragment : Fragment() {

    companion object {
        const val KEY_CID_BUNDLE = "key_cid"
        const val KEY_SHOW_TOOLBAR = "key_show_toolbar"
    }

    private lateinit var cid: String
    private var showToolbar: Boolean = false
    private lateinit var viewModel: ChatInfoSharedAttachmentsViewModel
    private val adapter: ChatInfoSharedFilesAdapter = ChatInfoSharedFilesAdapter()
    private var _binding: FragmentChatInfoSharedFilesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatInfoSharedFilesBinding.inflate(inflater, container, false)
        cid = arguments?.getString(KEY_CID_BUNDLE) ?: ""
        showToolbar = arguments?.getBoolean(KEY_SHOW_TOOLBAR) ?: false
        viewModel = ChatInfoSharedAttachmentsViewModel(cid, ChatInfoSharedAttachmentsViewModel.AttachmentsType.FILES)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (showToolbar) {
            initToolbar(binding.toolbar)
        } else {
            binding.toolbar.isVisible = false
        }
        binding.filesRecyclerView.adapter = adapter
        adapter.setAttachmentClickListener { attachmentItem ->
            Toast.makeText(requireContext(), R.string.ermis_ui_message_list_download_started, Toast.LENGTH_SHORT)
                .show()
            val attachmentGet = attachmentItem.attachmentGet
            val attachment = Attachment(
                fileSize = attachmentGet.content_length.toInt(),
                title = attachmentGet.file_name,
                url = attachmentGet.url,
                mimeType = attachmentGet.content_type,
                type = AttachmentType.getTypeFromMimeType(attachmentGet.content_type),
                assetUrl = attachmentGet.url
            )
            ErmisClient.instance().downloadAttachment(requireContext(), attachment).enqueue {

            }
        }
        bindViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun bindViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                showLoading()
            } else {
                if (state.results.isEmpty()) {
                    showEmptyState()
                } else {
                    showResults(state.results)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.filesRecyclerView.isVisible = false
        binding.emptyStateView.isVisible = false
    }

    private fun showResults(attachments: List<SharedAttachment>) {
        binding.progressBar.isVisible = false
        binding.filesRecyclerView.isVisible = true
        binding.emptyStateView.isVisible = false
        adapter.submitList(attachments)
    }

    private fun showEmptyState() {
        binding.progressBar.isVisible = false
        binding.filesRecyclerView.isVisible = false
        binding.emptyStateView.isVisible = true
    }
}
