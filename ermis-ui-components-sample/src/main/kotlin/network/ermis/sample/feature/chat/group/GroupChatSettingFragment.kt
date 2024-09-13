package network.ermis.sample.feature.chat.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import network.ermis.client.ErmisClient
import network.ermis.client.events.ChannelHiddenEvent
import network.ermis.client.events.ChannelVisibleEvent
import network.ermis.client.events.NotificationChannelMutesUpdatedEvent
import network.ermis.client.subscribeFor
import network.ermis.state.utils.EventObserver
import network.ermis.ui.viewmodel.messages.MessageListHeaderViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModelFactory
import network.ermis.ui.viewmodel.messages.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentGroupChatSettingBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.feature.chat.info.ChatInfoItem
import network.ermis.sample.feature.chat.group.member.GroupChatInfoMemberOptionsDialogFragment
import network.ermis.sample.feature.chat.group.member.GroupChatMembersFragment
import network.ermis.sample.feature.common.ConfirmationDialogFragment
import network.ermis.sample.util.FilePath
import network.ermis.sample.util.extensions.autoScrollToTop
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger
import java.io.File
import java.io.IOException

class GroupChatSettingFragment : Fragment() {

    private val logger by taggedLogger("GroupChatInfo-View")

    private val args: GroupChatSettingFragmentArgs by navArgs()
    private val viewModel: GroupChatInfoViewModel by viewModels { ChatViewModelFactory(args.cid) }
    private val headerViewModel: MessageListHeaderViewModel by viewModels {
        MessageListViewModelFactory(requireContext(), args.cid)
    }
    private val adapter: GroupChatInfoAdapter = GroupChatInfoAdapter()

    private var _binding: FragmentGroupChatSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupChatSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerView.setBackButtonClickListener {
            requireActivity().onBackPressed()
        }
        binding.optionsRecyclerView.adapter = adapter
        binding.optionsRecyclerView.autoScrollToTop()
        headerViewModel.bindView(binding.headerView, viewLifecycleOwner)
        bindGroupInfoViewModel()
        binding.btnSave.setOnClickListener {
            viewModel.onAction(GroupChatInfoViewModel.Action.SaveUpdateChannel)
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

    // Distinct channel == channel created without id (based on members).
    // There is no possibility to modify distinct channel members.
    private fun isAnonymousChannel(): Boolean = args.cid.contains("messaging")

    private fun bindGroupInfoViewModel() {
        subscribeForChannelMutesUpdatedEvents()
        subscribeForChannelVisibilityEvents()
        setOnClickListeners()

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
                    GroupChatInfoViewModel.UiEvent.LeaveChannelSuccess -> findNavController().popBackStack(
                        R.id.homeFragment,
                        false,
                    )
                    GroupChatInfoViewModel.UiEvent.UpdateChannelSuccess -> {
                        showToast(R.string.update_channel_success)
                    }
                }
            },
        )
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.channelNameEdit.isBlank() ||
                state.channelNameEdit.isEmpty() && state.channelAvatarEdit.isEmpty() && state.channelDescriptionEdit.isEmpty() ||
                state.channelNameEdit == state.channel.name && state.channelDescriptionEdit == state.channel.description ||
                state.channelNameEdit.length > 255 || state.channelDescriptionEdit.length > 255) {
                binding.btnSave.isEnabled = false
            } else {
                binding.btnSave.isEnabled = true
            }
            val avatarChannel = state.channelAvatarEdit.ifEmpty { state.channel.image }
            adapter.submitList(
                    listOf(
                        ChatInfoItem.ChannelAvatar(state.channel.copy(image = avatarChannel)),
                        ChatInfoItem.ChannelName(state.channel.name, state.channel.description),
                        ChatInfoItem.Separator,
                        ChatInfoItem.Option.ChannelMembers,
                        ChatInfoItem.Option.ChannelPermissions,
                        ChatInfoItem.Option.ChannelAdminstrator,
                        ChatInfoItem.Option.BannedUser,

                        // ChatInfoItem.Separator,
                        // ChatInfoItem.ChannelName(state.channelName),
                        // ChatInfoItem.Option.Stateful.MuteChannel(isChecked = state.channelMuted),
                        // ChatInfoItem.Option.HideChannel(isHidden = state.channelHidden),
                        // ChatInfoItem.Option.PinnedMessages,
                        // ChatInfoItem.Option.SharedMedia,
                        // ChatInfoItem.Option.SharedFiles,
                        // ChatInfoItem.Option.LeaveGroup,
                    ),
            )
        }
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

    private fun setOnClickListeners() {
        adapter.setChatInfoStatefulOptionChangedListener { option, isChecked ->
            logger.d { "[onStatefulOptionChanged] option: $option, isChecked: $isChecked" }

            when (option) {
                is ChatInfoItem.Option.Stateful.MuteChannel -> viewModel.onAction(
                    GroupChatInfoViewModel.Action.MuteChannelClicked(isChecked),
                )
                else -> throw IllegalStateException("Chat info option $option is not supported!")
            }
        }
        adapter.setChatInfoOptionClickListener { option ->
            when (option) {
                ChatInfoItem.Option.ChannelMembers -> {
                    val bundle = Bundle()
                    bundle.putString(GroupChatMembersFragment.KEY_CID_BUNDLE, args.cid)
                    bundle.putBoolean(GroupChatMembersFragment.KEY_IS_EDIT_MEMBERS, true)
                    findNavController().navigateSafely(
                        R.id.action_groupChatSettingFragment_to_groupChatMembersFragment, bundle
                    )
                }
                ChatInfoItem.Option.ChannelPermissions -> findNavController().navigateSafely(
                    GroupChatSettingFragmentDirections.actionGroupChatSettingFragmentToGroupChatPermissionsFragment(args.cid),
                )
                ChatInfoItem.Option.ChannelAdminstrator -> findNavController().navigateSafely(
                    GroupChatSettingFragmentDirections.actionGroupChatSettingFragmentToGroupChatAdminsFragment(args.cid),
                )
                ChatInfoItem.Option.BannedUser -> findNavController().navigateSafely(
                    GroupChatSettingFragmentDirections.actionGroupChatSettingFragmentToGroupChatBannedFragment(args.cid),
                )


                // ChatInfoItem.Option.PinnedMessages -> findNavController().navigateSafely(
                //     GroupChatSettingFragmentDirections.actionGroupChatSettingFragmentToPinnedMessageListFragment(args.cid),
                // )
                // ChatInfoItem.Option.SharedMedia -> Unit
                // ChatInfoItem.Option.SharedFiles -> Unit
                // ChatInfoItem.Option.LeaveGroup -> {
                //     val channelName = viewModel.state.value!!.channel.name
                //     ConfirmationDialogFragment.newLeaveChannelInstance(requireContext(), channelName)
                //         .apply {
                //             confirmClickListener = ConfirmationDialogFragment.ConfirmClickListener {
                //                 viewModel.onAction(GroupChatInfoViewModel.Action.LeaveChannelClicked)
                //             }
                //         }
                //         .show(parentFragmentManager, ConfirmationDialogFragment.TAG)
                // }
                // is ChatInfoItem.Option.HideChannel -> prepareHideChannelClickedAction {
                //     viewModel.onAction(it)
                // }
                else -> throw IllegalStateException("Group chat info option $option is not supported!")
            }
        }
        adapter.setMemberClickListener { viewModel.onAction(GroupChatInfoViewModel.Action.MemberClicked(it)) }
        adapter.setMembersSeparatorClickListener { viewModel.onAction(GroupChatInfoViewModel.Action.MembersSeparatorClicked) }
        adapter.setNameChangedListener { name, description ->
            viewModel.onAction(GroupChatInfoViewModel.Action.NameAndDescriptionChanged(name, description))
        }
        adapter.setAvatarChangedListener {
            imagePickerResultActivityLauncher.launch("image/*")
        }
    }

    private fun subscribeForChannelMutesUpdatedEvents() {
        ErmisClient.instance().subscribeFor<NotificationChannelMutesUpdatedEvent>(viewLifecycleOwner) {
            viewModel.onAction(GroupChatInfoViewModel.Action.ChannelMutesUpdated(it.me.channelMutes))
        }
    }

    private fun subscribeForChannelVisibilityEvents() {
        ErmisClient.instance().subscribeFor<ChannelHiddenEvent>(viewLifecycleOwner) {
            viewModel.onAction(
                GroupChatInfoViewModel.Action.ChannelHiddenUpdated(
                    cid = it.cid,
                    hidden = true,
                    clearHistory = it.clearHistory,
                ),
            )
        }
        ErmisClient.instance().subscribeFor<ChannelVisibleEvent>(viewLifecycleOwner) {
            viewModel.onAction(
                GroupChatInfoViewModel.Action.ChannelHiddenUpdated(
                    cid = it.cid,
                    hidden = false,
                ),
            )
        }
    }

    private fun prepareHideChannelClickedAction(
        onReady: (GroupChatInfoViewModel.Action.HideChannelClicked) -> Unit,
    ) {
        val curValue = viewModel.state.value!!.channelHidden
        val newValue = curValue.not()
        val action = GroupChatInfoViewModel.Action.HideChannelClicked(newValue)
        if (newValue) {
            val channelName = viewModel.state.value!!.channel.name
            ConfirmationDialogFragment.newHideChannelInstance(requireContext(), channelName)
                .apply {
                    confirmClickListener = ConfirmationDialogFragment.ConfirmClickListener {
                        onReady(action.copy(clearHistory = true))
                    }
                    cancelClickListener = ConfirmationDialogFragment.CancelClickListener {
                        onReady(action)
                    }
                }
                .show(parentFragmentManager, ConfirmationDialogFragment.TAG)
        } else {
            onReady(action)
        }
    }

    private var imagePickerResultActivityLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        //Do whatever you want to do with the result. the result is a Uri?
        if (imageUri != null) {
            // Handle the selected image
            try {
                val selectedFilePath: String = FilePath.getPath(requireContext(), imageUri).toString()
                val file = File(selectedFilePath)
                viewModel.onAction(GroupChatInfoViewModel.Action.UploadAvatarChannel(file))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
