
package network.ermis.sample.feature.channel.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import network.ermis.state.utils.EventObserver
import network.ermis.ui.viewmodel.messages.MessageComposerViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModelFactory
import network.ermis.ui.viewmodel.messages.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.initToolbar
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentAddChannelBinding
import network.ermis.sample.util.extensions.useAdjustResize

class AddChannelFragment : Fragment() {

    private var _binding: FragmentAddChannelBinding? = null
    private val binding get() = _binding!!
    private val addChannelViewModel: AddChannelViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        bindAddChannelView()
    }

    override fun onResume() {
        super.onResume()
        useAdjustResize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeChannel(cid: String) {
        val factory = MessageListViewModelFactory(requireContext(), cid)
        val messageListViewModel = factory.create(MessageListViewModel::class.java)
        val messageComposerViewModel = factory.create(MessageComposerViewModel::class.java)
        binding.addChannelView.apply {
            messageListViewModel.bindView(messageListView, viewLifecycleOwner)
            messageComposerViewModel.bindView(messageComposerView, viewLifecycleOwner)

            messageComposerView.sendMessageButtonClickListener = {
                messageComposerViewModel.sendMessage()
                addChannelViewModel.onEvent(AddChannelViewModel.Event.MessageSent)
            }
        }
    }

    private fun bindAddChannelView() {
        addChannelViewModel.apply {
            bindView(binding.addChannelView, viewLifecycleOwner)
            state.observe(viewLifecycleOwner) { state ->
                // Handle unique states
                when (state) {
                    is AddChannelViewModel.State.InitializeChannel -> {
                        // initializeChannel(state.cid)
                        findNavController().navigateSafely(
                            AddChannelFragmentDirections.actionOpenChat(state.cid, null),
                        )
                    }
                    is AddChannelViewModel.State.NavigateToChannel -> findNavController().navigateSafely(
                        AddChannelFragmentDirections.actionOpenChat(state.cid, null),
                    )
                    AddChannelViewModel.State.Loading,
                    is AddChannelViewModel.State.Result,
                    is AddChannelViewModel.State.ResultMoreUsers,
                    -> Unit
                }
            }
            errorEvents.observe(
                viewLifecycleOwner,
                EventObserver {
                    when (it) {
                        is AddChannelViewModel.ErrorEvent.CreateChannelError -> R.string.add_channel_error_create_channel
                    }.let(::showToast)
                },
            )
        }
        binding.addChannelView.apply {
            setMembersChangedListener {
                addChannelViewModel.onEvent(AddChannelViewModel.Event.MembersChanged(it))
            }
            setOnCreateGroupButtonListener {
                findNavController().navigateSafely(R.id.action_addChannelFragment_to_addGroupChannelFragment)
            }
        }
    }
}
