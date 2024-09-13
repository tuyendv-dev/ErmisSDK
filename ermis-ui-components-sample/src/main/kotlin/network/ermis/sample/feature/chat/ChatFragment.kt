package network.ermis.sample.feature.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import network.ermis.client.ErmisClient
import network.ermis.core.models.Flag
import network.ermis.core.models.Message
import network.ermis.state.utils.EventObserver
import network.ermis.ui.common.feature.messages.composer.mention.DefaultUserLookupHandler
import network.ermis.ui.common.feature.messages.composer.mention.DefaultUserQueryFilter
import network.ermis.ui.common.feature.messages.composer.mention.RemoteUserLookupHandler
import network.ermis.ui.common.feature.messages.composer.transliteration.DefaultStreamTransliterator
import network.ermis.ui.common.state.messages.Edit
import network.ermis.ui.common.state.messages.MessageMode
import network.ermis.ui.common.state.messages.Reply
import network.ermis.ui.common.state.messages.list.DeleteMessage
import network.ermis.ui.common.state.messages.list.DeletedMessageVisibility
import network.ermis.ui.common.state.messages.list.EditMessage
import network.ermis.ui.common.state.messages.list.SendAnyway
import network.ermis.ui.utils.extensions.getCreatedAtOrThrow
import network.ermis.ui.viewmodel.messages.MessageComposerViewModel
import network.ermis.ui.viewmodel.messages.MessageListHeaderViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModel
import network.ermis.ui.viewmodel.messages.MessageListViewModelFactory
import network.ermis.ui.viewmodel.messages.bindView
import network.ermis.chat.ui.sample.R
import network.ermis.sample.common.navigateSafely
import network.ermis.chat.ui.sample.databinding.FragmentChatBinding
import network.ermis.sample.feature.chat.composer.CustomMessageComposerLeadingContent
import network.ermis.sample.feature.common.ConfirmationDialogFragment
import network.ermis.sample.util.extensions.useAdjustResize
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import kotlinx.coroutines.launch
import java.util.Calendar

class ChatFragment : Fragment() {

    private val logger by taggedLogger("ChatFragment")

    private val args: ChatFragmentArgs by navArgs()

    private val defaultUserLookupHandler by lazy {
        DefaultUserLookupHandler(
            chatClient = ErmisClient.instance(),
            channelCid = args.cid,
            localFilter = DefaultUserQueryFilter(
                transliterator = DefaultStreamTransliterator(transliterationId = "Cyrl-Latn"),
            ),
        )
    }

    private val remoteUserLookupHandler by lazy {
        RemoteUserLookupHandler(
            chatClient = ErmisClient.instance(),
            channelCid = args.cid,
        )
    }

    private val factory: MessageListViewModelFactory by lazy {
        val chatClient = ErmisClient.instance()
        MessageListViewModelFactory(
            context = requireContext().applicationContext,
            cid = args.cid,
            messageId = args.messageId,
            parentMessageId = args.parentMessageId,
            chatClient = chatClient,
            userLookupHandler = defaultUserLookupHandler,
        )
    }
    private val chatViewModelFactory: ChatViewModelFactory by lazy { ChatViewModelFactory(args.cid) }
    private val headerViewModel: MessageListHeaderViewModel by viewModels { factory }
    private val messageListViewModel: MessageListViewModel by viewModels { factory }
    private val messageComposerViewModel: MessageComposerViewModel by viewModels { factory }
    private val chatViewModel: ChatViewModel by viewModels { chatViewModelFactory }

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        headerViewModel.bindView(binding.messagesHeaderView, viewLifecycleOwner)
        initChatViewModel()
        initMessageListViewModel()
        initMessageComposerViewModel()
        configureBackButtonHandling()
    }

    override fun onResume() {
        super.onResume()
        useAdjustResize()
    }

    private fun configureBackButtonHandling() {
        activity?.apply {
            onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
                    }
                },
            )
        }
        binding.messagesHeaderView.setBackButtonClickListener {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
    }

    private fun initChatViewModel() {
        chatViewModel.members.observe(viewLifecycleOwner) { members ->
            val currentMember = members.filter { it.user.id == ErmisClient.instance().getCurrentUser()?.id }
            val membership = if (currentMember.isEmpty()) null else currentMember.first()
            if (membership != null && membership.channelRole == "pending") {
                binding.layoutInvite.isVisible = true
                if (args.cid.startsWith("messaging")) {
                    binding.tvNoteInvite.text = getString(R.string.accept_the_invite_of_conversation)
                    binding.btnDecline.text = getString(R.string.channel_invite_skip)
                } else {
                    binding.tvNoteInvite.text = getString(R.string.accept_the_invite_of_channel)
                    binding.btnDecline.text = getString(R.string.channel_invite_decline)
                }
            } else {
                binding.layoutInvite.isVisible = false
                if (args.cid.startsWith("messaging")) { // channel is direct
                    val memberOthers = members.filter { it.user.id != ErmisClient.instance().getCurrentUser()?.id }
                    val memberDirect = if (memberOthers.isEmpty()) null else memberOthers.first()
                    if (memberDirect != null && memberDirect.channelRole == "pending") {
                        binding.tvInvitationSenderNote.text = getString(R.string.invitation_sender_note_label, memberDirect.user.name)
                        binding.tvInvitationSenderNote.isVisible = true
                    } else {
                        binding.tvInvitationSenderNote.isVisible = false
                    }
                }
            }
            binding.messagesHeaderView.apply {
                setAvatarClickListener {
                    chatViewModel.onAction(ChatViewModel.Action.HeaderClicked(members))
                }
                setTitleClickListener {
                    chatViewModel.onAction(ChatViewModel.Action.HeaderClicked(members))
                }
                setSubtitleClickListener {
                    chatViewModel.onAction(ChatViewModel.Action.HeaderClicked(members))
                }
            }
        }
        binding.btnAccept.setOnClickListener {
            chatViewModel.onAction(ChatViewModel.Action.AcceptChannelInvited())
        }
        binding.btnDecline.setOnClickListener {
            chatViewModel.onAction(ChatViewModel.Action.DeclineChannelInvited())
        }
        chatViewModel.navigationEvent.observe(
            viewLifecycleOwner,
            EventObserver { event ->
                when (event) {
                    is ChatViewModel.NavigationEvent.NavigateToChatInfo -> findNavController().navigateSafely(
                        ChatFragmentDirections.actionChatFragmentToGroupChatInfoFragment(event.cid),
                    )
                    is ChatViewModel.NavigationEvent.NavigateToGroupChatInfo -> findNavController().navigateSafely(
                        ChatFragmentDirections.actionChatFragmentToGroupChatInfoFragment(event.cid),
                    )
                    is ChatViewModel.NavigationEvent.NavigateUp -> {
                        val navigationHappened = findNavController().navigateUp()
                        if (!navigationHappened) {
                            findNavController()
                                .navigateSafely(ChatFragmentDirections.actionChatFragmentToHomeFragment())
                        }
                    }
                }
            },
        )
    }

    private fun initMessageComposerViewModel() {
        messageComposerViewModel.apply {
            bindView(binding.messageComposerView, viewLifecycleOwner)
            messageListViewModel.mode.observe(viewLifecycleOwner) {
                when (it) {
                    is MessageMode.MessageThread -> {
                        headerViewModel.setActiveThread(it.parentMessage)
                        messageComposerViewModel.setMessageMode(MessageMode.MessageThread(it.parentMessage))
                    }
                    is MessageMode.Normal -> {
                        headerViewModel.resetThread()
                        messageComposerViewModel.leaveThread()
                    }
                }
            }

            binding.messageListView.setMessageReplyHandler { _, message ->
                messageComposerViewModel.performMessageAction(Reply(message))
            }
            binding.messageListView.setMessageEditHandler { message ->
                messageComposerViewModel.performMessageAction(Edit(message))
            }
            binding.messageListView.setAttachmentReplyOptionClickHandler { result ->
                messageListViewModel.getMessageById(result.messageId)?.let { message ->
                    messageComposerViewModel.performMessageAction(Reply(message))
                }
            }
        }

        if (OVERRIDE_LEADING_CONTENT) {
            binding.messageComposerView.setLeadingContent(
                CustomMessageComposerLeadingContent(requireContext()),
            )
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                messageComposerViewModel.messageMode.collect { messageMode ->
                    when (messageMode) {
                        is MessageMode.Normal -> { /* no-op */ }
                        is MessageMode.MessageThread -> { /* no-op */ }
                    }
                    val modeText = messageMode.javaClass.simpleName
                    logger.d { "[onMessageModeChange] messageMode: $modeText" }
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                messageComposerViewModel.lastActiveAction.collect { messageAction ->
                    when (messageAction) {
                        is Edit -> { /* no-op */ }
                        is Reply -> { /* no-op */ }
                        else -> { /* no-op */ }
                    }
                    val actionText = messageAction?.javaClass?.simpleName
                    logger.d { "[onMessageActionChange] messageAction: $actionText" }
                }
            }
        }
    }

    private fun initMessageListViewModel() {
        val calendar = Calendar.getInstance()
        messageListViewModel.apply {
            messageListViewModel.setDeletedMessageVisibility(
                deletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,
            )
            bindView(binding.messageListView, viewLifecycleOwner)
            setDateSeparatorHandler { previousMessage, message ->
                if (previousMessage == null) {
                    true
                } else {
                    shouldShowDateSeparator(calendar, previousMessage, message)
                }
            }
            setThreadDateSeparatorHandler { previousMessage, message ->
                if (previousMessage == null) {
                    false
                } else {
                    shouldShowDateSeparator(calendar, previousMessage, message)
                }
            }
            state.observe(viewLifecycleOwner) {
                when (it) {
                    is MessageListViewModel.State.Loading -> Unit
                    is MessageListViewModel.State.Result -> Unit
                    is MessageListViewModel.State.NavigateUp -> {
                        val navigationHappened = findNavController().navigateUp()
                        if (!navigationHappened) {
                            findNavController()
                                .navigateSafely(ChatFragmentDirections.actionChatFragmentToHomeFragment())
                        }
                    }
                }
            }
        }
        binding.messageListView.apply {
            setConfirmDeleteMessageHandler { _, confirmCallback: () -> Unit ->
                ConfirmationDialogFragment.newDeleteMessageInstance(requireContext())
                    .apply {
                        confirmClickListener = ConfirmationDialogFragment.ConfirmClickListener(confirmCallback::invoke)
                    }.show(parentFragmentManager, null)
            }

            setConfirmFlagMessageHandler { _, confirmCallback: () -> Unit ->
                ConfirmationDialogFragment.newFlagMessageInstance(requireContext())
                    .apply {
                        confirmClickListener = ConfirmationDialogFragment.ConfirmClickListener(confirmCallback::invoke)
                    }.show(parentFragmentManager, null)
            }

            setFlagMessageResultHandler { result ->
                if (result is Result.Success || result.isAlreadyExistsError()) {
                    ConfirmationDialogFragment.newMessageFlaggedInstance(requireContext())
                        .show(parentFragmentManager, null)
                }
            }

            setModeratedMessageHandler { message, action ->
                when (action) {
                    DeleteMessage -> messageListViewModel.onEvent(MessageListViewModel.Event.DeleteMessage(message))
                    EditMessage -> messageComposerViewModel.performMessageAction(Edit(message))
                    SendAnyway -> messageListViewModel.onEvent(MessageListViewModel.Event.RetryMessage(message))
                    else -> Unit
                }
            }
        }
    }

    private fun shouldShowDateSeparator(calendar: Calendar, previousMessage: Message, message: Message): Boolean {
        val (previousYear, previousDayOfYear) = calendar.run {
            time = previousMessage.getCreatedAtOrThrow()
            get(Calendar.YEAR) to get(Calendar.DAY_OF_YEAR)
        }
        val (year, dayOfYear) = calendar.run {
            time = message.getCreatedAtOrThrow()
            get(Calendar.YEAR) to get(Calendar.DAY_OF_YEAR)
        }
        return previousYear != year || previousDayOfYear != dayOfYear
    }

    private fun Result<Flag>.isAlreadyExistsError(): Boolean {
        return when (this) {
            is Result.Success -> false
            is Result.Failure -> (value as Error.NetworkError).serverErrorCode == 4
        }
    }

    private companion object {
        private const val OVERRIDE_LEADING_CONTENT = false
    }
}
