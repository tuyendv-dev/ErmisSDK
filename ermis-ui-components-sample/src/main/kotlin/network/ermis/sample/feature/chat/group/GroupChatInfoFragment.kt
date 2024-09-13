package network.ermis.sample.feature.chat.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import network.ermis.client.ErmisClient
import network.ermis.client.events.NotificationChannelMutesUpdatedEvent
import network.ermis.client.utils.extensions.cidIsChannelDirect
import network.ermis.client.subscribeFor
import network.ermis.state.utils.EventObserver
import network.ermis.ui.ChatUI
import network.ermis.ui.viewmodel.messages.MessageListHeaderViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModelFactory
import network.ermis.ui.viewmodel.messages.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentGroupChatInfoBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.feature.chat.group.member.GroupChatInfoMemberOptionsDialogFragment
import network.ermis.sample.feature.common.ConfirmationDialogFragment
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger

class GroupChatInfoFragment : Fragment() {

    private val logger by taggedLogger("GroupChatInfoFragment")

    private val args: GroupChatInfoFragmentArgs by navArgs()
    private val viewModel: GroupChatInfoViewModel by viewModels { ChatViewModelFactory(args.cid) }
    private val headerViewModel: MessageListHeaderViewModel by viewModels {
        MessageListViewModelFactory(requireContext(), args.cid)
    }

    private var _binding: FragmentGroupChatInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupChatInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerView.setBackButtonClickListener {
            requireActivity().onBackPressed()
        }
        headerViewModel.bindView(binding.headerView, viewLifecycleOwner)
        bindGroupInfoViewModel()
        initTabAndViewpage()
        setOnClickView()
    }

    private fun setOnClickView() {
        binding.btnEditChannel.setOnClickListener {
            findNavController().navigateSafely(
                GroupChatInfoFragmentDirections.actionGroupChatInfoFragmentToGroupChatSettingFragment(args.cid),
            )
        }
        binding.layoutLeave.setOnClickListener {
            leaveGroup()
        }
        binding.layoutSearch.setOnClickListener {
            findNavController().navigateSafely(
                GroupChatInfoFragmentDirections.actionGroupChatInfoFragmentToChannelSearchMessageFragment(args.cid),
            )
        }
    }

    private fun initTabAndViewpage() {
        val adapter = ViewPageChannelInforAdapter(this, cid = args.cid, args.cid.cidIsChannelDirect().not())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
            tab.text = adapter.getTitleTab(position)
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        useAdjustResize()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun bindGroupInfoViewModel() {
        subscribeForChannelMutesUpdatedEvents()
        viewModel.state.observe(viewLifecycleOwner) { state ->
            val canEditChannel = args.cid.cidIsChannelDirect().not() && state.memberShip != null && state.memberShip.channelRole in listOf("owner","moder")
            binding.btnEditChannel.isVisible = canEditChannel
            binding.avatarChannel.setChannel(state.channel)
            binding.tvNameChannel.text = ChatUI.channelNameFormatter.formatChannelName(channel = state.channel, null)
        }
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatInfoViewModel.UiEvent.ShowMemberOptions ->
                        GroupChatInfoMemberOptionsDialogFragment.newInstance(
                            args.cid,
                            it.channelName,
                            it.member.user,
                            viewModel.state.value!!.ownCapabilities,
                        )
                            .show(parentFragmentManager, GroupChatInfoMemberOptionsDialogFragment.TAG)

                    GroupChatInfoViewModel.UiEvent.LeaveChannelSuccess -> {
                        showToast(R.string.chat_info_leave_channel_success)
                        findNavController().popBackStack(
                            R.id.homeFragment,
                            false,
                        )
                    }

                    GroupChatInfoViewModel.UiEvent.UpdateChannelSuccess -> Unit
                }
            },
        )
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatInfoViewModel.ErrorEvent.ChangeGroupNameError -> getString(R.string.chat_group_info_error_change_name)
                    is GroupChatInfoViewModel.ErrorEvent.MuteChannelError -> getString(R.string.chat_group_info_error_mute_channel)
                    is GroupChatInfoViewModel.ErrorEvent.HideChannelError -> getString(R.string.chat_group_info_error_hide_channel)
                    is GroupChatInfoViewModel.ErrorEvent.LeaveChannelError -> getString(R.string.chat_group_info_error_leave_channel)
                    is GroupChatInfoViewModel.ErrorEvent.ErrorOther -> it.error
                }.let(::showToast)
            },
        )
    }

    private fun subscribeForChannelMutesUpdatedEvents() {
        ErmisClient.instance().subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
            viewModel.onAction(GroupChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
        }
    }

    private fun leaveGroup() {
        val channelName = viewModel.state.value!!.channel.name
        ConfirmationDialogFragment.newLeaveChannelInstance(requireContext(), channelName)
            .apply {
                confirmClickListener = ConfirmationDialogFragment.ConfirmClickListener {
                    viewModel.onAction(GroupChatInfoViewModel.Action.LeaveChannelClicked)
                }
            }
            .show(parentFragmentManager, ConfirmationDialogFragment.TAG)
    }

    private fun muteChannel() {
        viewModel.onAction(
            GroupChatInfoViewModel.Action.MuteChannelClicked(true),
        )
    }
}
