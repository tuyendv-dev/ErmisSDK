package network.ermis.sample.feature.chat.group.permission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import network.ermis.core.models.ChannelCapabilities
import network.ermis.state.utils.EventObserver
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentChannelPermissionBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.feature.chat.group.GroupChatInfoAdapter
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger

class GroupChatPermissionsFragment : Fragment() {

    private val logger by taggedLogger("GroupChatPermissionsFragment")

    private val args: GroupChatPermissionsFragmentArgs by navArgs()
    private val viewModel: GroupChatPermissionViewModel by viewModels { ChatViewModelFactory(args.cid) }
    private val adapter: GroupChatInfoAdapter = GroupChatInfoAdapter()

    private var _binding: FragmentChannelPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) { state ->
            val isSendMessage = state.memberCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
            val isEditMessage = state.memberCapabilities.contains(ChannelCapabilities.UPDATE_OWN_MESSAGE)
            val isDeleteMessage = state.memberCapabilities.contains(ChannelCapabilities.DELETE_OWN_MESSAGE)
            val isReactMessage = state.memberCapabilities.contains(ChannelCapabilities.SEND_REACTION)
            binding.switchSendMessage.isChecked = isSendMessage
            binding.switchEditMessage.isChecked = isEditMessage
            binding.switchDeleteMessage.isChecked = isDeleteMessage
            binding.switchReactionMessage.isChecked = isReactMessage
        }
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatPermissionViewModel.ErrorEvent.PermissionChangeError -> R.string.chat_group_info_error_change_permission
                }.let(::showToast)
            },
        )
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatPermissionViewModel.UiEvent.PermissionChangeSuccess -> {
                        // findNavController().navigateUp()
                    }
                }
            },
        )

        binding.tvSave.setOnClickListener {
            val isSendMessage = binding.switchSendMessage.isChecked
            val isEditMessage = binding.switchEditMessage.isChecked
            val isDeleteMessage = binding.switchDeleteMessage.isChecked
            val isReactMessage = binding.switchReactionMessage.isChecked
            var add = listOf<String>()
            var delete = listOf<String>()
            if (isSendMessage) add += ChannelCapabilities.SEND_MESSAGE else delete += ChannelCapabilities.SEND_MESSAGE
            if (isEditMessage) add += ChannelCapabilities.UPDATE_OWN_MESSAGE else delete += ChannelCapabilities.UPDATE_OWN_MESSAGE
            if (isDeleteMessage) add += ChannelCapabilities.DELETE_OWN_MESSAGE else delete += ChannelCapabilities.DELETE_OWN_MESSAGE
            if (isReactMessage) add += ChannelCapabilities.SEND_REACTION else delete += ChannelCapabilities.SEND_REACTION
            viewModel.onAction(
                GroupChatPermissionViewModel.Action.PermissionChange(
                    add = add,
                    delete = delete,
                )
            )
        }
    }


    override fun onResume() {
        super.onResume()
        useAdjustResize()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
