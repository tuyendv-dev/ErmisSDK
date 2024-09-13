package network.ermis.sample.feature.chat.info.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import network.ermis.state.utils.EventObserver
import network.ermis.ui.common.utils.Utils
import network.ermis.sample.common.navigateSafely
import network.ermis.chat.ui.sample.databinding.FragmentChannelSearchMessageBinding
import network.ermis.sample.feature.chat.ChatViewModelFactory
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger

class ChannelSearchMessageFragment : Fragment() {

    private val logger by taggedLogger("ChannelSearchMessageFragment")

    private val args: ChannelSearchMessageFragmentArgs by navArgs()
    private val viewModel: ChannelSearchMessageViewModel by viewModels { ChatViewModelFactory(args.cid) }

    private var _binding: FragmentChannelSearchMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChannelSearchMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchMessage.apply {
            setDebouncedInputChangedListener { query ->
                viewModel.setQuery(query)
            }
            setSearchStartedListener { query ->
                Utils.hideSoftKeyboard(binding.searchMessage)
            }
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when {
                state.isLoading -> {
                    binding.searchResultListView.showLoading()
                }

                else -> {
                    binding.searchResultListView.showMessages(state.query, state.results)
                    binding.searchResultListView.setPaginationEnabled(!state.isLoadingMore && state.results.isNotEmpty())
                }
            }
        }
        viewModel.members.observe(viewLifecycleOwner) { members ->
            logger.d { "size=${members.size}" }
        }
        viewModel.errorEvents.observe(
            viewLifecycleOwner,
            EventObserver {
                binding.searchResultListView.showError()
            },
        )
        binding.searchResultListView.setLoadMoreListener {
            viewModel.loadMore()
        }
        binding.searchResultListView.setSearchResultSelectedListener { message ->
            findNavController().navigateSafely(
                ChannelSearchMessageFragmentDirections.channelSearchMessageFragmentOpenChat(message.cid, message.id),
            )
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
}
