package network.ermis.sample.feature.chat.info.shared.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import network.ermis.client.ErmisClient
import network.ermis.client.api.model.response.isVideo
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType
import network.ermis.state.extensions.downloadAttachment
import network.ermis.ui.ChatUI
import network.ermis.ui.view.gallery.AttachmentGalleryDestination
import network.ermis.ui.view.gallery.AttachmentGalleryItem
import network.ermis.ui.view.gallery.toAttachment
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.initToolbar
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentChatInfoSharedMediaBinding
import network.ermis.sample.feature.chat.info.shared.ChatInfoSharedAttachmentsViewModel
import network.ermis.sample.feature.chat.info.shared.SharedAttachment

class ChatInfoSharedMediaFragment : Fragment() {

    companion object {
        const val KEY_CID_BUNDLE = "key_cid"
        const val KEY_SHOW_TOOLBAR = "key_show_toolbar"
    }

    private lateinit var cid: String
    private var showToolbar: Boolean = false
    private lateinit var viewModel: ChatInfoSharedAttachmentsViewModel

    private val attachmentGalleryDestination by lazy {
        AttachmentGalleryDestination(
            requireContext(),
            attachmentReplyOptionHandler = {
                showToast("Comming soon")
            },
            attachmentShowInChatOptionHandler = {
                showToast("Comming soon")
            },
            attachmentDownloadOptionHandler = {
                showToast(R.string.ermis_ui_message_list_download_started)
                ErmisClient.instance().downloadAttachment(requireContext(), it.toAttachment()).enqueue {

                }
            },
            attachmentDeleteOptionClickHandler = {
                showToast("Comming soon")
            },
        )
    }

    private var _binding: FragmentChatInfoSharedMediaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatInfoSharedMediaBinding.inflate(inflater, container, false)
        cid = arguments?.getString(KEY_CID_BUNDLE) ?: ""
        showToolbar = arguments?.getBoolean(KEY_SHOW_TOOLBAR) ?: false
        viewModel = ChatInfoSharedAttachmentsViewModel(cid, ChatInfoSharedAttachmentsViewModel.AttachmentsType.MEDIA)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (showToolbar) {
            initToolbar(binding.toolbar)
        } else {
            binding.toolbar.isVisible = false
        }
        activity?.activityResultRegistry?.let { registry ->
            attachmentGalleryDestination.register(registry)
        }
        binding.mediaAttachmentGridView.setMediaClickListener {
            val attachmentGalleryItems = binding.mediaAttachmentGridView.getAttachments()
            attachmentGalleryDestination.setData(attachmentGalleryItems, it)
            ChatUI.navigator.navigate(attachmentGalleryDestination)
        }
        binding.mediaAttachmentGridView.setOnLoadMoreListener {
            viewModel.onAction(ChatInfoSharedAttachmentsViewModel.Action.LoadMoreRequested)
        }

        if (cid != null) {
            bindViewModel()
        } else {
            showEmptyState()
        }
    }

    override fun onDestroyView() {
        attachmentGalleryDestination.unregister()
        _binding = null
        super.onDestroyView()
    }

    private fun bindViewModel() {
        viewModel.state.switchMap { state ->
            ErmisClient.instance().clientState.user.asLiveData().map { user ->
                user to state
            }
        }.observe(viewLifecycleOwner) { (user, state) ->
            if (state.isLoading) {
                showLoading()
            } else {
                val results = state.results.filterIsInstance<SharedAttachment.AttachmentGetItem>()
                    .mapNotNull {
                        val attachmentGet = it.attachmentGet
                        val attachment = if (attachmentGet.isVideo()) {
                            Attachment(
                                fileSize = attachmentGet.content_length.toInt(),
                                image = attachmentGet.thumb_url,
                                url = attachmentGet.url,
                                mimeType = attachmentGet.content_type,
                                type = AttachmentType.getTypeFromMimeType(attachmentGet.content_type),
                                thumbUrl = attachmentGet.thumb_url,
                                assetUrl = attachmentGet.url
                            )
                        } else {
                            Attachment(
                                fileSize = attachmentGet.content_length.toInt(),
                                image = attachmentGet.url,
                                url = attachmentGet.url,
                                mimeType = attachmentGet.content_type,
                                imageUrl = attachmentGet.url,
                                type = AttachmentType.getTypeFromMimeType(attachmentGet.content_type),
                            )
                        }
                        AttachmentGalleryItem(
                            attachment = attachment,
                            user = null,
                            createdAt = attachmentGet.created_at!!,
                            messageId = attachmentGet.message_id,
                            cid = attachmentGet.cid,
                            isMine = false,
                        )
                    }
                if (results.isEmpty()) {
                    showEmptyState()
                } else {
                    showResults(results)
                }
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            separator.isVisible = true
            progressBar.isVisible = true
            mediaAttachmentGridView.isVisible = false
            emptyStateView.isVisible = false
            mediaAttachmentGridView.disablePagination()
        }
    }

    private fun showResults(attachmentGalleryItems: List<AttachmentGalleryItem>) {
        with(binding) {
            separator.isVisible = false
            progressBar.isVisible = false
            mediaAttachmentGridView.isVisible = true
            emptyStateView.isVisible = false
            mediaAttachmentGridView.enablePagination()
            mediaAttachmentGridView.setAttachments(attachmentGalleryItems)
        }
    }

    private fun showEmptyState() {
        with(binding) {
            separator.isVisible = true
            progressBar.isVisible = false
            emptyStateView.isVisible = true
            mediaAttachmentGridView.isVisible = false
            mediaAttachmentGridView.disablePagination()
        }
    }
}
