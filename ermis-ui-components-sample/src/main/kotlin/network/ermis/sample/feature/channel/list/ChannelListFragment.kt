
package network.ermis.sample.feature.channel.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import network.ermis.client.ErmisClient
import network.ermis.core.models.Filters
import network.ermis.ui.common.utils.Utils
import network.ermis.ui.viewmodel.channels.ChannelListViewModel
import network.ermis.ui.viewmodel.channels.ChannelListViewModelFactory
import network.ermis.ui.viewmodel.channels.bindView
import network.ermis.ui.viewmodel.search.SearchViewModel
import network.ermis.ui.viewmodel.search.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.application.App
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.data.user.SampleUser
import network.ermis.chat.ui.sample.databinding.FragmentChannelsBinding
import network.ermis.sample.feature.common.ConfirmationDialogFragment
import network.ermis.sample.feature.home.HomeFragmentDirections
import io.getstream.log.taggedLogger

class ChannelListFragment : Fragment() {

    private val logger by taggedLogger("ChannelListFragment")
    private val viewModel: ChannelListViewModel by viewModels {
        val user = App.instance.userRepository.getUser()
        val userId = if (user == SampleUser.None) {
            ErmisClient.instance().clientState.user.value?.id ?: ""
        } else {
            user.id
        }
        ChannelListViewModelFactory(
            filter = Filters.joindChannels(
                memberId = userId,
                projectId = ErmisClient.instance().getProjectId()
            ),
            chatEventHandlerFactory = CustomChatEventHandlerFactory(),
        )
    }
    private val searchViewModel: SearchViewModel by viewModels()

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupOnClickListeners()
        viewModel.bindView(binding.channelsView, viewLifecycleOwner)
        searchViewModel.bindView(binding.searchResultListView, this)

        binding.channelsView.apply {
            view as ViewGroup // for use as a parent in inflation

            val emptyView = layoutInflater.inflate(
                R.layout.channels_empty_view,
                view,
                false,
            )
            emptyView.findViewById<TextView>(R.id.startChatButton).setOnClickListener {
                requireActivity().findNavController(R.id.hostFragmentContainer)
                    .navigateSafely(HomeFragmentDirections.actionHomeFragmentToAddChannelFragment())
            }
            setEmptyStateView(emptyView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

            setChannelItemClickListener {
                requireActivity().findNavController(R.id.hostFragmentContainer)
                    .navigateSafely(HomeFragmentDirections.actionOpenChat(it.cid))
            }

            setChannelDeleteClickListener { channel ->
                ConfirmationDialogFragment.newDeleteChannelInstance(requireContext())
                    .apply {
                        confirmClickListener =
                            ConfirmationDialogFragment.ConfirmClickListener { viewModel.deleteChannel(channel) }
                    }
                    .show(parentFragmentManager, null)
            }

            setChannelInfoClickListener { channel ->
                val direction = HomeFragmentDirections.actionHomeFragmentToGroupChatInfoFragment(channel.cid)
                requireActivity()
                    .findNavController(R.id.hostFragmentContainer)
                    .navigateSafely(direction)
            }

            setChannelMarkAsReadClickListener { channel ->
                viewModel.channelMarkAsRead(channel.type, channel.id)
            }
        }

        binding.searchInputView.apply {
            setDebouncedInputChangedListener { query ->
                if (query.isEmpty()) {
                    binding.channelsView.isVisible = true
                    binding.searchResultListView.isVisible = false
                }
            }
            setSearchStartedListener { query ->
                Utils.hideSoftKeyboard(binding.searchInputView)
                searchViewModel.setQuery(query)
                binding.channelsView.isVisible = query.isEmpty()
                binding.searchResultListView.isVisible = query.isNotEmpty()
            }
        }

        binding.searchResultListView.setSearchResultSelectedListener { message ->
            requireActivity().findNavController(R.id.hostFragmentContainer)
                .navigateSafely(HomeFragmentDirections.actionOpenChat(message.cid, message.id))
        }
    }

    private fun setupOnClickListeners() {
        activity?.apply {
            onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (binding.searchInputView.clear()) {
                    return@addCallback
                }
                if (ErmisClient.instance().isInErmisProject()) {
                    finish()
                } else {
                    requireActivity().findNavController(R.id.hostFragmentContainer).navigateUp()
                }
            }
        }
    }
}
