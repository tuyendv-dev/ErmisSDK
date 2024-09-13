
@file:JvmName("MessageListViewModelBinding")

package network.ermis.ui.viewmodel.messages

import androidx.lifecycle.LifecycleOwner
import network.ermis.state.utils.EventObserver
import network.ermis.ui.view.gallery.toAttachment
import network.ermis.ui.view.MessageListView
import network.ermis.ui.utils.PermissionChecker
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.BottomEndRegionReached
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.DeleteMessage
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.DownloadAttachment
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.EndRegionReached
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.FlagMessage
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.GiphyActionSelected
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.LastMessageRead
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.MessageReaction
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.ReplyMessage
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.RetryMessage
import network.ermis.ui.viewmodel.messages.MessageListViewModel.Event.ThreadModeEntered

/**
 * Binds [MessageListView] with [MessageListViewModel], updating the view's state
 * based on data provided by the ViewModel, and forwarding View events to the ViewModel.
 *
 * This function sets listeners on the view and ViewModel. Call this method
 * before setting any additional listeners on these objects yourself.
 *
 * @param view The [MessageListView] to bind the ViewModel to.
 * @param lifecycleOwner Current owner of the lifecycle in which the events are handled.
 */
@JvmName("bind")
public fun MessageListViewModel.bindView(
    view: MessageListView,
    lifecycleOwner: LifecycleOwner,
) {
    deletedMessageVisibility.observe(lifecycleOwner) {
        view.setDeletedMessageVisibility(it)
    }

    channel.observe(lifecycleOwner) {
        view.init(it)
    }
    read.observe(lifecycleOwner) { read ->
        if (read == null) return@observe
        if (read.unreadMessages > 0 && read.lastReadMessageId.isNullOrEmpty().not()) {
            view.setChannelUserRead(unreadMessages = read.unreadMessages, lastReadMessageId = read.lastReadMessageId!!)
        }
    }
    view.setEndRegionReachedHandler { onEvent(EndRegionReached) }
    view.setBottomEndRegionReachedHandler { messageId -> onEvent(BottomEndRegionReached(messageId)) }
    view.setLastMessageReadHandler { onEvent(LastMessageRead) }
    view.setMessageDeleteHandler { onEvent(DeleteMessage(it, hard = false)) }
    view.setThreadStartHandler { onEvent(ThreadModeEntered(it)) }
    view.setMessageFlagHandler { onEvent(FlagMessage(it, view::handleFlagMessageResult)) }
    view.setMessagePinHandler { onEvent(MessageListViewModel.Event.PinMessage(it)) }
    view.setMessageUnpinHandler { onEvent(MessageListViewModel.Event.UnpinMessage(it)) }
    view.setMessageMarkAsUnreadHandler { onEvent(MessageListViewModel.Event.MarkAsUnreadMessage(it)) }
    view.setGiphySendHandler { giphyAction ->
        onEvent(GiphyActionSelected(giphyAction))
    }
    view.setMessageRetryHandler { onEvent(RetryMessage(it)) }
    view.setMessageReactionHandler { message, reactionType ->
        onEvent(MessageReaction(message, reactionType, channelType = channel.value?.type ?: "", channelId = channel.value?.id ?: ""))
    }
    view.setMessageReplyHandler { cid, message -> onEvent(ReplyMessage(cid, message)) }
    view.setAttachmentDownloadHandler { downloadAttachmentCall ->
        PermissionChecker().checkWriteStoragePermissions(view) {
            onEvent(DownloadAttachment(downloadAttachmentCall))
        }
    }
    view.setReplyMessageClickListener { replyTo ->
        onEvent(
            MessageListViewModel.Event.ShowMessage(
                messageId = replyTo.id,
                parentMessageId = replyTo.parentId,
            ),
        )
    }
    view.setOnScrollToBottomHandler { scrollToBottom { view.scrollToBottom() } }
    view.setOnScrollToUnreadMessages { messageId ->
        onEvent(
            MessageListViewModel.Event.ShowMessage(
                messageId = messageId,
                parentMessageId = null,
            ),
        )
    }

    ownCapabilities.observe(lifecycleOwner) {
        view.setOwnCapabilities(it)
    }

    state.observe(lifecycleOwner) { state ->
        when (state) {
            is MessageListViewModel.State.Loading -> {
                view.hideEmptyStateView()
                view.showLoadingView()
            }
            is MessageListViewModel.State.Result -> {
                if (state.messageListItem.items.isEmpty()) {
                    view.showEmptyStateView()
                } else {
                    view.hideEmptyStateView()
                }
                view.displayNewMessages(state.messageListItem)
                view.hideLoadingView()
            }
            MessageListViewModel.State.NavigateUp -> Unit // Not handled here
        }
    }
    loadMoreLiveData.observe(lifecycleOwner, view::setLoadingMore)
    targetMessage.observe(lifecycleOwner, view::scrollToMessage)
    insideSearch.observe(lifecycleOwner, view::shouldRequestMessagesAtBottom)
    unreadCount.observe(lifecycleOwner, view::setUnreadCount)

    view.setAttachmentReplyOptionClickHandler { result ->
        onEvent(MessageListViewModel.Event.ReplyAttachment(result.cid, result.messageId))
    }
    view.setAttachmentShowInChatOptionClickHandler { result ->
        onEvent(
            MessageListViewModel.Event.ShowMessage(
                messageId = result.messageId,
                parentMessageId = result.parentId,
            ),
        )
    }
    view.setAttachmentDeleteOptionClickHandler { result ->
        onEvent(
            MessageListViewModel.Event.RemoveAttachment(
                result.messageId,
                result.toAttachment(),
            ),
        )
    }
    errorEvents.observe(
        lifecycleOwner,
        EventObserver {
            view.showError(it)
        },
    )
    typingUsers.observe(lifecycleOwner, view::showTypingStateLabel)
}
