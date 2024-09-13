package network.ermis.sample.feature.chat.group.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import network.ermis.core.models.Member
import network.ermis.state.utils.EventObserver
import network.ermis.ui.common.utils.Utils
import network.ermis.sample.common.showToast
import network.ermis.sample.feature.chat.info.ChatInfoItem
import network.ermis.sample.feature.chat.group.GroupChatInfoAdapter
import network.ermis.sample.feature.chat.group.users.GroupChatInfoAddUsersDialogFragment
import network.ermis.sample.feature.common.ConfirmationDialogFragment
import network.ermis.sample.util.extensions.autoScrollToTop
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger
import network.ermis.chat.ui.sample.R
import network.ermis.chat.ui.sample.databinding.FragmentChannelMembersBinding

class GroupChatMembersFragment : Fragment() {

    companion object {
        const val KEY_CID_BUNDLE = "key_cid"
        const val KEY_IS_EDIT_MEMBERS = "key_edit_members"
    }

    private val logger by taggedLogger("GroupChatMembersFragment")

    private lateinit var cid: String
    private var isEditMemberView: Boolean = false
    private lateinit var viewModel: GroupChatMembersViewModel
    private val adapter: GroupChatInfoAdapter = GroupChatInfoAdapter()

    private var _binding: FragmentChannelMembersBinding? = null
    private val binding get() = _binding!!
    private var isTabInvited = false
    private var textSearch = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelMembersBinding.inflate(inflater, container, false)
        cid = arguments?.getString(KEY_CID_BUNDLE) ?: ""
        isEditMemberView = arguments?.getBoolean(KEY_IS_EDIT_MEMBERS) ?: false
        viewModel = GroupChatMembersViewModel(cid)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.memberRecyclerView.adapter = adapter
        binding.memberRecyclerView.autoScrollToTop()
        if (isEditMemberView) {
            binding.layoutEdit.isVisible = true
            binding.layoutView.isVisible = false
            initTablayout()
            initSearch()
        } else {
            binding.layoutEdit.isVisible = false
            binding.layoutView.isVisible = true
        }
        bindGroupInfoViewModel()
    }

    private fun initSearch() {
        binding.searchMember.apply {
            setDebouncedInputChangedListener { query ->
                textSearch = query
                viewModel.members.value?.let {
                    fiterMemberAndDisplay(viewModel.members.value!!)
                }
            }
            setSearchStartedListener { query ->
                Utils.hideSoftKeyboard(binding.searchMember)
            }
        }
    }

    private fun initTablayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.chat_group_info_members_tab_members))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.chat_group_info_members_tab_invited))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        isTabInvited = false
                        viewModel.members.value?.let {
                            fiterMemberAndDisplay(viewModel.members.value!!)
                        }
                    }
                    1 -> {
                        isTabInvited = true
                        viewModel.members.value?.let {
                            fiterMemberAndDisplay(viewModel.members.value!!)
                        }
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab) {
            }

            override fun onTabReselected(p0: TabLayout.Tab) {
            }
        })
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

        viewModel.events.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    GroupChatMembersViewModel.UiEvent.RedirectToHome -> Unit
                    GroupChatMembersViewModel.UiEvent.RemoveMemberSuccess -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.remove_member_of_channel_success),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            },
        )
        viewModel.members.observe(viewLifecycleOwner) { members ->
            fiterMemberAndDisplay(members)
        }
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatMembersViewModel.ErrorEvent.UpdateMemberError -> R.string.chat_group_info_error_add_member
                }.let(::showToast)
            },
        )
    }

    private fun fiterMemberAndDisplay(members: List<Member>) {
        val itemMembers = members
            .filter { member ->
                if (isTabInvited) {
                    member.channelRole == "pending"
                } else {
                    member.channelRole != "pending"
                }
            }
            .sortedBy { it.user.name }
            .filter { it.user.name.contains(textSearch, true) }
            .map { ChatInfoItem.MemberItem(it, null, isEditMemberView) }
        adapter.submitList(itemMembers)
    }

    private fun setOnClickListeners() {
        adapter.setMemberClickListener {
            showDialogConfirmDeleteMember(it)
        }
        binding.addMemberButton.setOnClickListener {
            showDialogAddMember()
        }
        binding.tvAddMember.setOnClickListener {
            showDialogAddMember()
        }
    }

    private fun showDialogAddMember() {
        GroupChatInfoAddUsersDialogFragment.newInstance(cid)
            .show(parentFragmentManager, GroupChatInfoAddUsersDialogFragment.TAG)
    }
    private fun showDialogConfirmDeleteMember(member: Member) {
        ConfirmationDialogFragment.newInstance(
            iconResId = R.drawable.ic_delete,
            iconTintResId = R.color.red,
            title = "",
            description = getString(R.string.chat_group_info_user_remove_description),
            confirmText = getString(R.string.remove),
            cancelText = getString(R.string.cancel),
        ).apply {
            confirmClickListener = ConfirmationDialogFragment.ConfirmClickListener {
                viewModel.onAction(GroupChatMembersViewModel.Action.MemberClicked(member))
            }
        }.show(parentFragmentManager, ConfirmationDialogFragment.TAG)
    }
}
