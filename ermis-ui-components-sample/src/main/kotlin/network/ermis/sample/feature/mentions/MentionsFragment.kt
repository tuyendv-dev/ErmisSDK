
package network.ermis.sample.feature.mentions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import network.ermis.ui.viewmodel.mentions.MentionListViewModel
import network.ermis.ui.viewmodel.mentions.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.navigateSafely
import network.ermis.chat.ui.sample.databinding.FragmentMentionsBinding
import network.ermis.sample.feature.home.HomeFragmentDirections

class MentionsFragment : Fragment() {

    private val viewModel: MentionListViewModel by viewModels()

    private var _binding: FragmentMentionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMentionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.bindView(binding.mentionsListView, viewLifecycleOwner)
        binding.mentionsListView.setMentionSelectedListener { message ->
            requireActivity().findNavController(R.id.hostFragmentContainer)
                .navigateSafely(HomeFragmentDirections.actionOpenChat(message.cid, message.id))
        }
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        activity?.apply {
            onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                finish()
            }
        }
    }
}
