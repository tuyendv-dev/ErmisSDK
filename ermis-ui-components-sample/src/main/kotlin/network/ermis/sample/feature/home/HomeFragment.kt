package network.ermis.sample.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import network.ermis.client.ErmisClient
import network.ermis.state.utils.EventObserver
import network.ermis.ui.viewmodel.channels.ChannelListHeaderViewModel
import network.ermis.ui.viewmodel.channels.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.navigateSafely
import network.ermis.chat.ui.sample.databinding.FragmentHomeBinding
import network.ermis.sample.feature.EXTRA_CHANNEL_ID
import network.ermis.sample.feature.EXTRA_CHANNEL_TYPE
import network.ermis.sample.feature.EXTRA_MESSAGE_ID
import network.ermis.sample.feature.EXTRA_PARENT_MESSAGE_ID
import network.ermis.sample.feature.projects.SwitchPlatformDialogFragment
import network.ermis.sample.feature.userlogin.UserLoginViewModel
import network.ermis.sample.util.extensions.useAdjustNothing
import io.getstream.log.taggedLogger

class HomeFragment : Fragment() {

    private val logger by taggedLogger("HomeViewModel")
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseNotificationData()
        setupBottomNavigation()
        setupHeader()
        homeViewModel.state.observe(viewLifecycleOwner, ::renderState)
        homeViewModel.events.observe(
            viewLifecycleOwner,
            EventObserver(::handleHomeEvents),
        )
        binding.channelListHeaderView.apply {
            channelListHeaderViewModel.bindView(this, viewLifecycleOwner)
            setOnlineTitle(ErmisClient.instance().getProjectName() ?: "")
            setShowAvatar(ErmisClient.instance().isInErmisProject())
            setOnActionButtonClickListener {
                navigateSafely(R.id.action_homeFragment_to_addChannelFragment)
            }
            setOnUserAvatarClickListener {
                navigateSafely(R.id.action_homeFragment_to_userProfileFragment)
            }
        }
    }

    private fun setupHeader() {
        if (ErmisClient.instance().isInErmisProject()) {
            binding.tvSdk.isVisible = true
            binding.ivBack.isVisible = false
            binding.tvSdk.setOnClickListener {
                SwitchPlatformDialogFragment.newInstance(
                    selectedIndex = 1
                ).apply {
                    sdkClickListener = {
                        findNavController().navigateSafely(
                            HomeFragmentDirections.actionHomeFragmentToProjectListFragment()
                        )
                    }
                }.show(parentFragmentManager, SwitchPlatformDialogFragment.TAG)
            }
        } else {
            binding.tvSdk.isVisible = false
            binding.ivBack.isVisible = true
            binding.ivBack.setOnClickListener {
                // requireActivity().findNavController(R.id.hostFragmentContainer).navigateUp()
                val navigationHappened = findNavController().navigateUp()
                if (!navigationHappened) {
                    findNavController()
                        .navigateSafely(HomeFragmentDirections.actionHomeFragmentToProjectListFragment())
                }
            }
        }
    }

    private fun handleHomeEvents(uiEvent: HomeViewModel.UiEvent) {
        when (uiEvent) {
            HomeViewModel.UiEvent.NavigateToLoginScreenLogout -> {
                navigateSafely(
                    R.id.action_to_userLoginFragment,
                    Bundle().apply {
                        this.putBoolean(UserLoginViewModel.EXTRA_SWITCH_USER, false)
                    },
                )
            }
            HomeViewModel.UiEvent.NavigateToLoginScreenSwitchUser -> {
                navigateSafely(
                    R.id.action_to_userLoginFragment,
                    Bundle().apply {
                        this.putBoolean(UserLoginViewModel.EXTRA_SWITCH_USER, true)
                    },
                )
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
                    HomeFragmentDirections.actionOpenChat(cid, messageId, parentMessageId),
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        useAdjustNothing()
        homeViewModel.getUserInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.hostFragmentContainer) as NavHostFragment
        binding.bottomNavigationView.apply {
            setupWithNavController(navHostFragment.navController)
            setBackgroundResource(R.drawable.shape_bottom_navigation_background)
        }
    }

    private fun renderState(state: HomeViewModel.UiState) {
        state.user?.let {  user ->
            binding.channelListHeaderView.setUser(user)
        }
    }
}
