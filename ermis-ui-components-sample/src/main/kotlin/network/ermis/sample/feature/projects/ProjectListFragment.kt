package network.ermis.sample.feature.projects

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import network.ermis.client.ErmisClient
import network.ermis.client.api.model.response.ErmisProject
import network.ermis.state.utils.EventObserver
import network.ermis.ui.common.utils.Utils
import network.ermis.chat.ui.sample.R
import network.ermis.sample.application.AppConfig
import network.ermis.sample.common.navigateSafely
import network.ermis.chat.ui.sample.databinding.FragmentProjectListBinding
import network.ermis.sample.feature.EXTRA_CHANNEL_ID
import network.ermis.sample.feature.EXTRA_CHANNEL_TYPE
import network.ermis.sample.feature.EXTRA_MESSAGE_ID
import network.ermis.sample.feature.EXTRA_PARENT_MESSAGE_ID
import network.ermis.sample.feature.projects.adapter.ClientListAdapter
import io.getstream.log.taggedLogger

class ProjectListFragment : Fragment() {
    private val logger by taggedLogger("Chat:ProjectListFragment")
    private val viewModel: ProjectListViewModel by viewModels()
    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!
    private val clientAdapter = ClientListAdapter()
    private var isTabJoined = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUnreadAllChannels()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseNotificationData()
        setupOnBackPressed()
        viewModel.state.observe(viewLifecycleOwner, ::renderState)
        viewModel.events.observe(viewLifecycleOwner, EventObserver(::renderEvent))
        initSearch()
        initTablayout()
        binding.recyclerviewClient.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@ProjectListFragment.clientAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL,
                ).apply {
                    setDrawable(AppCompatResources.getDrawable(context, R.drawable.divider)!!)
                },
            )
        }
        clientAdapter.onClickClientListener = { client ->
            viewModel.expanClient(client)
        }

        clientAdapter.onClickProjectListener = { project ->
            if (isTabJoined) {
                gotoProjectChannels(project)
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.join_project_confirm_title, project.project_name))
                    .setMessage(project.description)
                    .setPositiveButton(R.string.join_project_confirm_join) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.joinNewProject(project)
                    }
                    .setNegativeButton(R.string.join_project_confirm_join_later) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        binding.tvSDK.setOnClickListener {
            SwitchPlatformDialogFragment.newInstance(
                selectedIndex = 0
            ).apply {
                ermisClickListener = {
                    ErmisClient.instance().setProjectId(ErmisClient.ERMIS_PROJECT_ID, ErmisClient.ERMIS_PROJECT_NAME)
                    findNavController().navigateSafely(R.id.action_projectListFragment_to_homeFragment)
                }
            }.show(parentFragmentManager, SwitchPlatformDialogFragment.TAG)
        }
    }

    private fun gotoProjectChannels(project: ErmisProject) {
        ErmisClient.instance().setProjectId(project.project_id, project.project_name)
        findNavController().navigateSafely(R.id.action_projectListFragment_to_homeFragment)
    }

    private fun initSearch() {
        binding.searchProject.apply {
            setDebouncedInputChangedListener { query ->
                viewModel.querySearchProject(query)
            }
            setSearchStartedListener { query ->
                Utils.hideSoftKeyboard(binding.searchProject)
            }
        }
    }

    private fun initTablayout() {
        binding.tabLayoutProject.addTab(binding.tabLayoutProject.newTab().setText(R.string.your_project))
        binding.tabLayoutProject.addTab(binding.tabLayoutProject.newTab().setText(R.string.new_project))
        binding.tabLayoutProject.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        isTabJoined = true
                        viewModel.filterJoined(true)
                    }
                    1 -> {
                        isTabJoined = false
                        viewModel.filterJoined(false)
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab) {
            }

            override fun onTabReselected(p0: TabLayout.Tab) {
            }
        })
    }

    private fun renderState(state: ProjectListViewModel.UiState) {
        binding.tvChain.setOnClickListener {
            PopupMenu(it.context, it).let { popupMenu ->
                val sampleChains = AppConfig.getListChainByIds(state.allChainIdFilter)
                sampleChains.forEach { chain ->
                    popupMenu.menu.add(chain.name)
                }
                popupMenu.setOnMenuItemClickListener { item ->
                    val chainSelected = AppConfig.getChainByName(item.title.toString())
                    if (chainSelected != null) {
                        viewModel.selectChain(chainSelected.chainId)
                        return@setOnMenuItemClickListener true
                    }
                    false
                }
                popupMenu.show()
            }
        }
        binding.tvChain.text = AppConfig.getChainById(state.chainIdSelected)?.name
        clientAdapter.submitList(state.listClientDisplay)
    }

    private fun renderEvent(uiEvent: ProjectListViewModel.UiEvent) {
        when (uiEvent) {
            is ProjectListViewModel.UiEvent.NavigateToChannelListInProject -> {
                binding.tabLayoutProject.selectTab(binding.tabLayoutProject.getTabAt(0))
                gotoProjectChannels(uiEvent.project)
            }
        }
    }

    private fun setupOnBackPressed() {
        activity?.apply {
            onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                finish()
            }
        }
    }

    private fun parseNotificationData() {
        requireActivity().intent?.let {
            if (it.hasExtra(EXTRA_CHANNEL_ID) && it.hasExtra(EXTRA_MESSAGE_ID) && it.hasExtra(EXTRA_CHANNEL_TYPE)) {
                val channelType = it.getStringExtra(EXTRA_CHANNEL_TYPE)
                val channelId = it.getStringExtra(EXTRA_CHANNEL_ID)
                val cid = "$channelType:$channelId"
                val messageId = it.getStringExtra(EXTRA_MESSAGE_ID)
                val parentMessageId = it.getStringExtra(EXTRA_PARENT_MESSAGE_ID)
                requireActivity().intent = null
                findNavController().navigateSafely(
                    ProjectListFragmentDirections.actionProjectListFragmentOpenChat(cid, messageId, parentMessageId),
                )
            }
        }
    }
}