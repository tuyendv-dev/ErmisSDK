package network.ermis.sample.feature.chat.group.users

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import network.ermis.core.models.User
import network.ermis.state.utils.EventObserver
import network.ermis.ui.widgets.EndlessScrollListener
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.showToast
import network.ermis.chat.ui.sample.databinding.ChatInfoGroupAddUsersDialogFragmentBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.feature.chat.group.users.GroupChatInfoAddUsersDialogFragment.LoadMoreListener

class GroupChatInfoAddUsersDialogFragment : DialogFragment() {

    private val cid: String by lazy { requireArguments().getString(ARG_CID)!! }
    private val adapter: GroupChatInfoAddUsersAdapter = GroupChatInfoAddUsersAdapter()
    private val viewModel: GroupChatInfoAddUsersViewModel by viewModels { ChatViewModelFactory(cid) }
    private var loadMoreListener: LoadMoreListener? = null
    private val scrollListener = EndlessScrollListener(LOAD_MORE_THRESHOLD) {
        loadMoreListener?.loadMore()
    }

    private var _binding: ChatInfoGroupAddUsersDialogFragmentBinding? = null
    private val binding get() = _binding!!

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
        binding.tvTitle.setText(R.string.title_users)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(scrollListener)
        loadMoreListener = LoadMoreListener {
            viewModel.onAction(GroupChatInfoAddUsersViewModel.Action.LoadMoreRequested)
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                showLoading()
            } else {
                if (state.results.isEmpty()) {
                    showEmptyState()
                } else {
                    showResults(state.results, state.membersSelected)
                }
            }
            if (state.membersSelected.isEmpty()) {
                binding.tvConfirm.isEnabled = false
                binding.tvConfirm.alpha = 0.3f
            } else {
                binding.tvConfirm.isEnabled = true
                binding.tvConfirm.alpha = 1f
            }
        }
        viewModel.userAddedState.observe(viewLifecycleOwner) { isUserAdded ->
            if (isUserAdded) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.add_channel_success),
                    Toast.LENGTH_SHORT,
                ).show()
                dismiss()
            }
        }
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                when (it) {
                    is GroupChatInfoAddUsersViewModel.ErrorEvent.AddMemberError -> R.string.chat_group_info_error_add_member
                }.let(::showToast)
            },
        )

        adapter.setUserClickListener { user ->
            viewModel.onAction(GroupChatInfoAddUsersViewModel.Action.UserClicked(user))
        }
        binding.tvConfirm.setOnClickListener {
            viewModel.onAction(GroupChatInfoAddUsersViewModel.Action.AddMembers)
        }
        binding.searchInputView.setDebouncedInputChangedListener { query ->
            viewModel.onAction(GroupChatInfoAddUsersViewModel.Action.SearchQueryChanged(query))
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

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.emptyView.isVisible = false
        scrollListener.disablePagination()
    }

    private fun showResults(users: List<User>, setSelectedIds: Set<String>) {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = true
        binding.emptyView.isVisible = false
        scrollListener.enablePagination()
        val list = users.map { UserSelect(
            user = it, selected = setSelectedIds.contains(it.id)
        ) }
        adapter.submitList(list)
    }

    private fun showEmptyState() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        scrollListener.disablePagination()
        binding.emptyView.isVisible = true
    }

    fun interface LoadMoreListener {
        fun loadMore()
    }

    companion object {
        const val TAG = "GroupChatInfoAddMembersDialogFragment"
        private const val ARG_CID = "cid"
        private const val LOAD_MORE_THRESHOLD = 5

        fun newInstance(cid: String): GroupChatInfoAddUsersDialogFragment {
            return GroupChatInfoAddUsersDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CID, cid)
                }
            }
        }
    }
}
