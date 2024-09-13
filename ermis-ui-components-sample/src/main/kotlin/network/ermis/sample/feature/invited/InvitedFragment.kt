package network.ermis.sample.feature.invited

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import network.ermis.client.ErmisClient
import network.ermis.core.models.Channel
import network.ermis.core.models.Filters
import network.ermis.ui.view.invite.ChannelInviteListView
import network.ermis.ui.viewmodel.invited.ChannelInvitedListViewModelFactory
import network.ermis.ui.viewmodel.invited.InvitedListViewModel
import network.ermis.ui.viewmodel.invited.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.application.App
import network.ermis.sample.common.navigateSafely
import network.ermis.sample.data.user.SampleUser
import network.ermis.chat.ui.sample.databinding.FragmentInvitedBinding
import network.ermis.sample.feature.channel.list.CustomChatEventHandlerFactory
import network.ermis.sample.feature.home.HomeFragmentDirections

class InvitedFragment : Fragment() {

    private var _binding: FragmentInvitedBinding? = null

    private val viewModel: InvitedListViewModel by viewModels {
        val user = App.instance.userRepository.getUser()
        val userId = if (user == SampleUser.None) {
            ErmisClient.instance().clientState.user.value?.id ?: ""
        } else {
            user.id
        }
        ChannelInvitedListViewModelFactory(
            filter = Filters.invitedChannels(
                memberId = userId,
                projectId = ErmisClient.instance().getProjectId()
            ),
            chatEventHandlerFactory = CustomChatEventHandlerFactory(),
        )
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInvitedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.bindView(binding.invitedListView, viewLifecycleOwner)
        binding.invitedListView.setChannelInviteListener(
            object : ChannelInviteListView.ChannelInviteListener {
                override fun onAcceptInvite(channel: Channel) {
                    viewModel.onAction(InvitedListViewModel.Action.AcceptChannelInvited(channel))
                }

                override fun onDeclineInvite(channel: Channel) {
                    viewModel.onAction(InvitedListViewModel.Action.DeclineChannelInvited(channel))
                }

                override fun onClickInvite(channel: Channel) {
                    requireActivity().findNavController(R.id.hostFragmentContainer)
                        .navigateSafely(HomeFragmentDirections.actionOpenChat(channel.cid))
                }
            }
        )
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        activity?.apply {
            onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (ErmisClient.instance().isInErmisProject()) {
                    finish()
                } else {
                    requireActivity().findNavController(R.id.hostFragmentContainer).navigateUp()
                }
            }
        }
    }
}
