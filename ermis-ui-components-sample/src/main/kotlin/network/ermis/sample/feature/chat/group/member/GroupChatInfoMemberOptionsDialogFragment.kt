
package network.ermis.sample.feature.chat.group.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import network.ermis.core.models.ChannelCapabilities
import network.ermis.core.models.User
import network.ermis.state.utils.EventObserver
import network.ermis.ui.utils.extensions.getLastSeenText
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupMemberOptionsFragmentBinding
import network.ermis.sample.feature.chat.info.UserData
import network.ermis.sample.feature.chat.info.toUser
import network.ermis.sample.feature.chat.info.toUserData
import network.ermis.sample.feature.common.ConfirmationDialogFragment

class GroupChatInfoMemberOptionsDialogFragment : BottomSheetDialogFragment() {

    private val cid: String by lazy {
        requireArguments().getString(ARG_CID)!!
    }
    private val userData: UserData by lazy {
        requireArguments().getSerializable(ARG_USER_DATA) as UserData
    }
    private val channelName: String by lazy {
        requireArguments().getString(ARG_CHANNEL_NAME)!!
    }
    private val user: User by lazy {
        userData.toUser()
    }
    private val ownCapabilities: Set<String> by lazy {
        requireArguments().getStringArrayList(ARG_OWN_CAPABILITIES)?.toSet() ?: setOf()
    }

    private val viewModel: GroupChatInfoMemberOptionsViewModel by viewModels {
        GroupChatInfoMemberOptionsViewModelFactory(cid, user.id)
    }
    private var _binding: ChatInfoGroupMemberOptionsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ChatInfoGroupMemberOptionsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int = R.style.ermisUiBottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        binding.apply {
            userNameTextView.text = user.name
            lastSeenTextView.text = user.getLastSeenText(requireContext())
            userAvatarView.setUser(user)
            optionViewInfo.setOnOptionClickListener {
                // findNavController().navigateSafely(
                //     GroupChatInfoFragmentDirections.actionOpenChatInfo(
                //         userData = userData,
                //         cid = viewModel.state.value!!.directChannelCid,
                //     ),
                // )
                // dismiss()
            }
            optionMessage.setOnClickListener {
                viewModel.onAction(GroupChatInfoMemberOptionsViewModel.Action.MessageClicked)
            }

            if (isAnonymousChannel(cid) || !ownCapabilities.contains(ChannelCapabilities.UPDATE_CHANNEL_MEMBERS)) {
                optionRemove.isVisible = false
            } else {
                optionRemove.setOnClickListener {
                    ConfirmationDialogFragment.newInstance(
                        iconResId = R.drawable.ic_delete,
                        iconTintResId = R.color.red,
                        title = getString(R.string.chat_group_info_user_remove_title, user.name),
                        description = getString(R.string.chat_group_info_user_remove_description),
                        confirmText = getString(R.string.remove),
                        cancelText = getString(R.string.cancel),
                    ).apply {
                        confirmClickListener = ConfirmationDialogFragment.ConfirmClickListener {
                            viewModel.onAction(GroupChatInfoMemberOptionsViewModel.Action.RemoveFromChannel(user.name))
                        }
                    }.show(parentFragmentManager, ConfirmationDialogFragment.TAG)
                }
            }
            optionCancel.setOnOptionClickListener {
                dismiss()
            }
        }
    }

    private fun isAnonymousChannel(cid: String): Boolean = cid.contains("messaging")

    private fun initViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (!state.loading) {
                binding.apply {
                    optionMessage.isVisible = true
                    optionViewInfo.isVisible = state.directChannelCid != null
                }
            }
        }
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    GroupChatInfoMemberOptionsViewModel.UiEvent.Dismiss -> dismiss()
                    is GroupChatInfoMemberOptionsViewModel.UiEvent.RedirectToChat -> {
                        // findNavController().navigateSafely(
                        //     GroupChatInfoFragmentDirections.actionOpenChat(cid = it.cid),
                        // )
                        // dismiss()
                    }
                    GroupChatInfoMemberOptionsViewModel.UiEvent.RedirectToChatPreview -> {
                        // findNavController().navigateSafely(
                        //     GroupChatInfoFragmentDirections.actionOpenChatPreview(userData),
                        // )
                        // dismiss()
                    }
                }
            },
        )
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatInfoMemberOptionsViewModel.ErrorEvent.RemoveMemberError -> R.string.chat_group_info_error_remove_member
                }.let(::showToast)
            },
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "GroupChatInfoMemberOptionsDialogFragment"
        private const val ARG_CID = "cid"
        private const val ARG_CHANNEL_NAME = "channel_name"
        private const val ARG_USER_DATA = "user_data"
        private const val ARG_OWN_CAPABILITIES = "own_capabilities"

        fun newInstance(cid: String, channelName: String, user: User, ownCapabilities: Set<String>) =
            GroupChatInfoMemberOptionsDialogFragment().apply {
                arguments =
                    bundleOf(
                        ARG_CID to cid,
                        ARG_CHANNEL_NAME to channelName,
                        ARG_USER_DATA to user.toUserData(),
                        ARG_OWN_CAPABILITIES to ownCapabilities.toList(),
                    )
            }
    }
}
