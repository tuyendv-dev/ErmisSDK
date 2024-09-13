
package network.ermis.sample.feature.chat.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import network.ermis.state.utils.EventObserver
import network.ermis.ui.viewmodel.messages.MessageComposerViewModel
import network.ermis.ui.viewmodel.messages.MessageListHeaderViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModelFactory
import network.ermis.ui.viewmodel.messages.bindView
import network.ermis.sample.common.navigateSafely
import network.ermis.chat.ui.sample.databinding.FragmentChatPreviewBinding
import network.ermis.sample.util.extensions.useAdjustResize

class ChatPreviewFragment : Fragment() {

    private val args: ChatPreviewFragmentArgs by navArgs()
    private val viewModel: ChatPreviewViewModel by viewModels { ChatPreviewViewModelFactory(args.userData.id) }

    private var _binding: FragmentChatPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.messagesHeaderView.setBackButtonClickListener {
            findNavController().navigateUp()
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.cid != null) {
                binding.progressBar.isVisible = false
                initializeChatPreview(state.cid)
            }
        }
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is ChatPreviewViewModel.UiEvent.NavigateToChat -> findNavController().navigateSafely(
                        ChatPreviewFragmentDirections.actionOpenChat(cid = it.cid),
                    )
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()
        useAdjustResize()
    }

    private fun initializeChatPreview(cid: String) {
        val factory = MessageListViewModelFactory(requireContext(), cid)
        val messageListHeaderViewModel = factory.create(MessageListHeaderViewModel::class.java)
        val messageListViewModel = factory.create(MessageListViewModel::class.java)
        val messageComposerViewModel = factory.create(MessageComposerViewModel::class.java)

        binding.messagesHeaderView.apply {
            messageListHeaderViewModel.bindView(this, viewLifecycleOwner)

            setAvatarClickListener {
                navigateToChatInfo(cid)
            }
            setTitleClickListener {
                navigateToChatInfo(cid)
            }
            setSubtitleClickListener {
                navigateToChatInfo(cid)
            }
        }

        binding.messageListView.apply {
            isVisible = true
            messageListViewModel.bindView(this, viewLifecycleOwner)
        }

        binding.messageComposerView.apply {
            isVisible = true
            messageComposerViewModel.bindView(this, viewLifecycleOwner)
            sendMessageButtonClickListener = {
                viewModel.onAction(ChatPreviewViewModel.Action.MessageSent)
            }
        }
    }

    private fun navigateToChatInfo(cid: String) {
        // findNavController().navigateSafely(
        //     ChatPreviewFragmentDirections.actionOpenChatInfo(userData = args.userData, cid = cid),
        // )
    }
}
