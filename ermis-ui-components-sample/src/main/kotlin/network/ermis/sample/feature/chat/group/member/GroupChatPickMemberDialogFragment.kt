package network.ermis.sample.feature.chat.group.member

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import network.ermis.core.models.Member
import network.ermis.state.utils.EventObserver
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupAddUsersDialogFragmentBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.feature.chat.info.ChatInfoItem
import network.ermis.sample.feature.chat.group.GroupChatInfoAdapter
import io.getstream.log.taggedLogger

class GroupChatPickMemberDialogFragment : DialogFragment() {

    private val logger by taggedLogger("GroupChatPickMemberDialogFragment-View")
    private val cid: String by lazy { requireArguments().getString(ARG_CID)!! }
    private val mode: String by lazy { requireArguments().getString(ARG_MODE)!! }
    private val adapter: GroupChatInfoAdapter = GroupChatInfoAdapter()
    private val viewModel: GroupChatPickMembersViewModel by viewModels { ChatViewModelFactory(cid) }

    private var _binding: ChatInfoGroupAddUsersDialogFragmentBinding? = null
    private val binding get() = _binding!!
    private var textSearch = ""


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ChatInfoGroupAddUsersDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (mode) {
            ARG_MODE_ADD_MODERATOR -> {
                binding.tvConfirm.setText(R.string.action_add)
                binding.tvConfirm.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            }
            ARG_MODE_ADD_BAN -> {
                binding.tvConfirm.setText(R.string.action_Ban)
                binding.tvConfirm.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_error))
            }
        }
        binding.progressBar.isVisible = false
        binding.recyclerView.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            filterAndShowMember(state.members, state.membersSelected)
            state.membersSelected.isNotEmpty()
            if (state.membersSelected.isEmpty()) {
                binding.tvConfirm.isEnabled = false
                binding.tvConfirm.alpha = 0.3f
            } else {
                binding.tvConfirm.isEnabled = true
                binding.tvConfirm.alpha = 1f
            }
        }
        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    GroupChatPickMembersViewModel.UiEvent.BannedMemberSuccess -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.ban_member_of_channel_success),
                            Toast.LENGTH_SHORT,
                        ).show()
                        dismiss()
                    }
                    GroupChatPickMembersViewModel.UiEvent.PromoteMemberSuccess -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.promote_member_of_channel_success),
                            Toast.LENGTH_SHORT,
                        ).show()
                        dismiss()
                    }
                }
            },
        )
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatPickMembersViewModel.ErrorEvent.UpdateMemberError -> R.string.chat_group_info_error_add_member
                }.let(::showToast)
            },
        )

        adapter.setMemberClickListener { member ->
            viewModel.onAction(GroupChatPickMembersViewModel.Action.MemberClicked(member))
        }
        binding.tvConfirm.setOnClickListener {
            when (mode) {
                ARG_MODE_ADD_MODERATOR -> viewModel.onAction(GroupChatPickMembersViewModel.Action.MemberPromote)
                ARG_MODE_ADD_BAN -> viewModel.onAction(GroupChatPickMembersViewModel.Action.MemberBanned)
            }
        }
        binding.searchInputView.setDebouncedInputChangedListener { query ->
            textSearch = query
            filterAndShowMember(viewModel.state.value!!.members, viewModel.state.value!!.membersSelected)
        }
    }

    private fun filterAndShowMember(members: List<Member>, selectedIds: Set<String>) {
        val itemMembers = members
            .filter { member ->
                when (mode) {
                    ARG_MODE_ADD_MODERATOR -> member.channelRole in listOf("member")
                    ARG_MODE_ADD_BAN -> member.channelRole in listOf("member") && member.banned.not()
                    else -> true
                }
            }
            .filter { it.user.name.contains(textSearch, true) }
            .map { ChatInfoItem.PickMemberItem(it, selectedIds.contains(it.getUserId())) }
        adapter.submitList(itemMembers)
        if (itemMembers.isEmpty()) {
            binding.recyclerView.isVisible = false
            binding.emptyView.isVisible = true
        } else {
            binding.recyclerView.isVisible = true
            binding.emptyView.isVisible = false
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "GroupChatPickMemberDialogFragment"
        private const val ARG_CID = "cid"
        private const val ARG_MODE = "mode"

        const val ARG_MODE_ADD_MODERATOR = "add_moderator"
        const val ARG_MODE_ADD_BAN = "add_ban"

        fun newInstance(cid: String, mode: String): GroupChatPickMemberDialogFragment {
            return GroupChatPickMemberDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CID, cid)
                    putString(ARG_MODE, mode)
                }
            }
        }
    }
}
