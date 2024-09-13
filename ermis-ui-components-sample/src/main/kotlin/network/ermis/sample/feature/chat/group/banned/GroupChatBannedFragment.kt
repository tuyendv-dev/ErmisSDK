package network.ermis.sample.feature.chat.group.banned

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import network.ermis.client.ErmisClient
import network.ermis.state.utils.EventObserver
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.FragmentChannelBannedBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.feature.chat.info.ChatInfoItem
import network.ermis.sample.feature.chat.group.GroupChatInfoAdapter
import network.ermis.sample.feature.chat.group.member.GroupChatPickMemberDialogFragment
import network.ermis.sample.util.extensions.autoScrollToTop
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger

class GroupChatBannedFragment : Fragment() {

    private val logger by taggedLogger("GroupChatBannedFragment")

    private val args: GroupChatBannedFragmentArgs by navArgs()
    private val viewModel: GroupChatBannedViewModel by viewModels { ChatViewModelFactory(args.cid) }
    private val adapter: GroupChatInfoAdapter = GroupChatInfoAdapter()

    private var _binding: FragmentChannelBannedBinding? = null
    private val binding get() = _binding!!
    private var isTabInvited = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelBannedBinding.inflate(inflater, container, false)
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
        viewModel.members.observe(viewLifecycleOwner) { members ->
            val currentMember = members.filter { it.getUserId() == ErmisClient.instance().getCurrentUser()?.id }.firstOrNull()
            val itemMembers = members
                .filter { it.banned }
                .map { ChatInfoItem.MemberBannedItem(it, currentMember) }
            adapter.submitList(itemMembers)
        }
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatBannedViewModel.ErrorEvent.UnBanMemberError -> it.error
                }.let(::showToast)
            },
        )
    }

    private fun setOnClickListeners() {
        adapter.setMemberClickListener { viewModel.onAction(GroupChatBannedViewModel.Action.MemberClicked(it)) }
        binding.tvAddBan.setOnClickListener {
            GroupChatPickMemberDialogFragment.newInstance(args.cid, GroupChatPickMemberDialogFragment.ARG_MODE_ADD_BAN)
                .show(parentFragmentManager, GroupChatPickMemberDialogFragment.TAG)
        }
    }

}
