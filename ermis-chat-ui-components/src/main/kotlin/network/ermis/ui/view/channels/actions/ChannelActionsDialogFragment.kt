
package network.ermis.ui.view.channels.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import network.ermis.core.models.Channel
import network.ermis.core.models.Member
import network.ermis.ui.ChatUI
import network.ermis.ui.components.R
import network.ermis.ui.common.state.channels.ChannelAction
import network.ermis.ui.common.utils.extensions.isDirectMessaging
import network.ermis.ui.components.databinding.FragmentChannelActionsBinding
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.getMembersStatusText
import network.ermis.ui.utils.extensions.isCurrentUser
import network.ermis.ui.utils.extensions.setStartDrawable
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.viewmodel.channels.ChannelActionsViewModel
import network.ermis.ui.viewmodel.channels.ChannelActionsViewModelFactory

/**
 * A bottom sheet with the list of actions users can take with the selected channel.
 */
internal class ChannelActionsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChannelActionsBinding? = null
    private val binding get() = _binding!!

    /**
     * Style for the dialog.
     */
    private lateinit var style: ChannelActionsDialogViewStyle

    /**
     * The selected channel.
     */
    private lateinit var channel: Channel

    /**
     * A callback for clicks on channel options.
     */
    private var channelOptionClickListener: ChannelOptionClickListener? = null

    /**
     * A callback for clicks on members.
     */
    private var channelMemberClickListener: ChannelMemberClickListener? = null

    /**
     * The full channel id, i.e. "messaging:123".
     */
    private val cid get() = channel.cid

    /**
     * [ViewModel] for the dialog.
     */
    private val channelActionsViewModel: ChannelActionsViewModel by viewModels {
        ChannelActionsViewModelFactory(cid)
    }

    /**
     * An [RecyclerView.Adapter] for the list of channel members.
     */
    private val membersAdapter: ChannelMembersAdapter = ChannelMembersAdapter {
        channelMemberClickListener?.onMemberClick(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentChannelActionsBinding.inflate(requireContext().streamThemeInflater, container, false)
            .apply { _binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isInitialized = ::channel.isInitialized && ::style.isInitialized
        if (savedInstanceState == null && isInitialized) {
            setupDialog()
        } else {
            // The process has been killed
            dismiss()
        }
    }

    /**
     * Initializes the dialog.
     */
    private fun setupDialog() {
        binding.recyclerView.adapter = membersAdapter
        binding.channelActionsContainer.background = style.background
        binding.channelMembersTextView.setTextStyle(style.memberNamesTextStyle)
        binding.channelMembersInfoTextView.setTextStyle(style.memberInfoTextStyle)

        channelActionsViewModel.channel.observe(viewLifecycleOwner) { channel ->
            this.channel = channel
            bindChannelInfo()
            bindChannelOptions()
        }
    }

    /**
     * Updates the channel name, the member status text and the list of channel members.
     */
    private fun bindChannelInfo() {
        binding.channelMembersTextView.text = ChatUI.channelNameFormatter.formatChannelName(
            channel = channel,
            currentUser = ChatUI.currentUserProvider.getCurrentUser(),
        )
        binding.channelMembersInfoTextView.text = channel.getMembersStatusText(requireContext())

        val channelMembers = channel.members
        val membersToDisplay = if (channel.isDirectMessaging()) {
            channelMembers.filter { !it.user.isCurrentUser() }
        } else {
            channelMembers
        }
        membersAdapter.submitList(membersToDisplay)
    }

    /**
     * Updates the list of channel options.
     */
    private fun bindChannelOptions() {
        binding.optionsContainer.removeAllViews()

        ChannelOptionItemsFactory.defaultFactory(requireContext())
            .createChannelOptionItems(
                selectedChannel = channel,
                ownCapabilities = channel.ownCapabilities,
                style = style,
            ).forEach { option ->
                val channelOptionTextView = requireContext().streamThemeInflater.inflate(
                    R.layout.channel_option_item,
                    binding.optionsContainer,
                    false,
                ) as TextView
                channelOptionTextView.text = option.optionText
                channelOptionTextView.setStartDrawable(option.optionIcon)
                channelOptionTextView.setOnClickListener {
                    channelOptionClickListener?.onChannelOptionClick(option.channelAction)
                    dismiss()
                }
                channelOptionTextView.setTextStyle(
                    if (option.isWarningItem) {
                        style.warningItemTextStyle
                    } else {
                        style.itemTextStyle
                    },
                )

                binding.optionsContainer.addView(channelOptionTextView)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int = R.style.ermisUiBottomSheetDialogTheme

    /**
     * Initializes the dialog with the selected channel.
     *
     * @param channel The selected channel.
     */
    fun setChannel(channel: Channel) {
        this.channel = channel
    }

    /**
     * Initializes the dialog with the style.
     *
     * @param style Style for the dialog.
     */
    fun setStyle(style: ChannelActionsDialogViewStyle) {
        this.style = style
    }

    /**
     * Allows clients to set a click listener for channel option items.
     *
     * @param channelOptionClickListener The callback to be invoked on channel option item click.
     */
    fun setChannelOptionClickListener(channelOptionClickListener: ChannelOptionClickListener) {
        this.channelOptionClickListener = channelOptionClickListener
    }

    /**
     * Allows clients to set a click listener for channel member items.
     *
     * @param channelMemberClickListener The callback to be invoked on channel member item click.
     */
    fun setChannelMemberClickListener(channelMemberClickListener: ChannelMemberClickListener) {
        this.channelMemberClickListener = channelMemberClickListener
    }

    /**
     * A listener for member clicks.
     */
    fun interface ChannelMemberClickListener {
        fun onMemberClick(member: Member)
    }

    /**
     * A listener for channel option clicks.
     */
    fun interface ChannelOptionClickListener {
        fun onChannelOptionClick(channelAction: ChannelAction)
    }

    companion object {
        /**
         * Creates a new instance of [ChannelActionsDialogFragment].
         *
         * @param channel The selected channel.
         * @param style The style for the dialog.
         */
        fun newInstance(
            channel: Channel,
            style: ChannelActionsDialogViewStyle,
        ): ChannelActionsDialogFragment {
            return ChannelActionsDialogFragment().apply {
                setChannel(channel)
                setStyle(style)
            }
        }
    }
}
