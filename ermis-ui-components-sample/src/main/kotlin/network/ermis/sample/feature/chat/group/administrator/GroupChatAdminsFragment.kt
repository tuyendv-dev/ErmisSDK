package network.ermis.sample.feature.chat.group.administrator

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import network.ermis.client.ErmisClient
import network.ermis.state.utils.EventObserver
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentChannelAdminBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.feature.chat.info.ChatInfoItem
import network.ermis.sample.feature.chat.group.GroupChatInfoAdapter
import network.ermis.sample.feature.chat.group.member.GroupChatPickMemberDialogFragment
import network.ermis.sample.util.extensions.autoScrollToTop
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger

class GroupChatAdminsFragment : Fragment() {

    private val logger by taggedLogger("GroupChatAdminsFragment")

    private val args: GroupChatAdminsFragmentArgs by navArgs()
    private val viewModel: GroupChatAdminViewModel by viewModels { ChatViewModelFactory(args.cid) }
    private val adapter: GroupChatInfoAdapter = GroupChatInfoAdapter()
    private var _binding: FragmentChannelAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.memberRecyclerView.adapter = adapter
        binding.memberRecyclerView.autoScrollToTop()
        bindGroupInfoViewModel()
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
        setOnClickListeners()

        // viewModel.events.observe(
        //     viewLifecycleOwner,
        //     EventObserver {
        //         when (it) {
        //             GroupChatMembersViewModel.UiEvent.RedirectToHome -> findNavController().popBackStack(
        //                 R.id.homeFragment,
        //                 false,
        //             )
        //             GroupChatMembersViewModel.UiEvent.RemoveMemberSuccess -> TODO()
        //         }
        //     },
        // )
        viewModel.members.observe(viewLifecycleOwner) { members ->
            val currentMember =
                members.filter { it.getUserId() == ErmisClient.instance().getCurrentUser()?.id }.firstOrNull()
            val itemMembers = members
                .filter { it.channelRole in listOf("owner","moder") }
                .map { ChatInfoItem.MemberItem(it, currentMember, true) }
            adapter.submitList(itemMembers)
        }
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatAdminViewModel.ErrorEvent.DemoteMemberError -> it.error
                }.let(::showToast)
            },
        )
    }

    private fun setOnClickListeners() {
        adapter.setMemberClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.remove_moderator_confirm)
                .setPositiveButton(R.string.remove) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.onAction(GroupChatAdminViewModel.Action.MemberClicked(it))
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        binding.tvAddModerator.setOnClickListener {
            GroupChatPickMemberDialogFragment.newInstance(args.cid, GroupChatPickMemberDialogFragment.ARG_MODE_ADD_MODERATOR)
                .show(parentFragmentManager, GroupChatPickMemberDialogFragment.TAG)
        }
    }
}
