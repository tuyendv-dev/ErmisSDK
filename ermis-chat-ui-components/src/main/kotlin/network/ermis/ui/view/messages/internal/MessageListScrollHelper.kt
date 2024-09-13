
package network.ermis.ui.view.messages.internal

import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.ermis.client.utils.message.isDeleted
import network.ermis.core.models.Message
import io.getstream.chat.android.ui.common.extensions.internal.safeCast
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.adapter.BaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.internal.MessageListItemAdapter
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.getFragmentManager
import io.getstream.log.taggedLogger
import kotlin.properties.Delegates

internal class MessageListScrollHelper(
    private val recyclerView: RecyclerView,
    private val scrollButtonView: ScrollButtonView,
    private val disableScrollWhenShowingDialog: Boolean,
    private val callback: MessageReadListener,
) {

    private val logger by taggedLogger("MessageListScrollHelper")

    internal var alwaysScrollToBottom: Boolean by Delegates.notNull()
    internal var scrollToBottomButtonEnabled: Boolean by Delegates.notNull()

    private val layoutManager: LinearLayoutManager
        get() = recyclerView.layoutManager as LinearLayoutManager
    private val adapter: MessageListItemAdapter
        get() = recyclerView.adapter as MessageListItemAdapter

    private var onScrollToBottomHandler: MessageListView.OnScrollToBottomHandler =
        MessageListView.OnScrollToBottomHandler {
            recyclerView.scrollToPosition(currentList.lastIndex)
        }

    internal var unreadCountEnabled: Boolean = true

    private var areNewestMessagesLoaded: Boolean = true

    private var bottomOffset: Int = 0

    /**
     * True when the latest message is visible.
     *
     * Note: This does not mean the whole message is visible,
     * it will be true even if only a part of it is.
     */
    private var isAtBottom = false
        set(value) {
            logger.d { "[setIsAtBottom] value: $value" }
            if (value && !field) {
                callback.onLastMessageRead()
            }
            field = value
        }

    private val currentList: List<MessageListItem>
        get() {
            return adapter.currentList
        }

    init {
        scrollButtonView.setOnClickListener {
            onScrollToBottomHandler.onScrollToBottom()
        }
        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                /**
                 * Checks if we currently have a popup shown over the list.
                 *
                 * @param recyclerView The list that we're observing.
                 * @param newState The scroll state of the list.
                 */
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    logger.d { "[onScrollStateChanged] newState: $newState" }
                    super.onScrollStateChanged(recyclerView, newState)

                    if (disableScrollWhenShowingDialog) {
                        stopScrollIfPopupShown(recyclerView)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    logger.d { "[onScrolled] scrollToBottomButtonEnabled: $scrollToBottomButtonEnabled, currentList.size: ${currentList.size}" }
                    if (!scrollToBottomButtonEnabled || currentList.isEmpty()) {
                        return
                    }
                    val shouldScrollToBottomBeVisible = shouldScrollToBottomBeVisible()
                    logger.v { "[onScrolled] shouldScrollToBottomBeVisible: $shouldScrollToBottomBeVisible" }
                    scrollButtonView.isVisible = shouldScrollToBottomBeVisible
                }
            },
        )
    }

    /**
     * Calculates the bottom offset by comparing the position of the last visible item
     * with the position of the last potentially visible item.
     *
     * @return The bottom offset.
     */
    private fun calculateBottomOffset(): Int {
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val lastPotentiallyVisibleItemPosition = currentList.indexOfLast { it.isValid() }
        logger.v {
            "[calculateBottomOffset] lastVisibleItemPosition: $lastVisibleItemPosition, " +
                "lastPotentiallyVisibleItemPosition: $lastPotentiallyVisibleItemPosition"
        }
        return lastPotentiallyVisibleItemPosition - lastVisibleItemPosition
    }

    /**
     * Determines whether the scroll to bottom button should be visible or not.
     *
     * @return Whether the scroll to bottom button should be visible or not.
     */
    private fun shouldScrollToBottomBeVisible(): Boolean {
        bottomOffset = calculateBottomOffset()
        logger.d { "[shouldScrollToBottomBeVisible] bottomOffset: $bottomOffset, areNewestMessagesLoaded: $areNewestMessagesLoaded" }
        isAtBottom = bottomOffset <= 0 && areNewestMessagesLoaded

        return when {
            adapter.itemCount == 0 -> false
            !areNewestMessagesLoaded -> true
            else -> {
                val hasInvisibleUnreadMessage = !isAtBottom
                val hasScrolledUpEnough = bottomOffset > SCROLL_BUTTON_VISIBILITY_THRESHOLD

                hasInvisibleUnreadMessage || hasScrolledUpEnough
            }
        }
    }

    /**
     * Checks if we have any popups shown over the list and stops scrolling in case we do.
     *
     * @param recyclerView The list that's being observed for long taps and scrolling.
     */
    private fun stopScrollIfPopupShown(recyclerView: RecyclerView) {
        val fragmentManager = recyclerView.context.getFragmentManager() ?: return
        val hasDialogsShown = fragmentManager.fragments.any { it is DialogFragment }

        if (hasDialogsShown) {
            recyclerView.stopScroll()
        }
    }

    internal fun scrollToMessage(message: Message) {
        recyclerView.postDelayed(
            {
                currentList.indexOfFirst { it is MessageListItem.MessageItem && it.message.id == message.id }
                    .takeIf { it >= 0 }
                    ?.let {
                        if (message.pinned) {
                            this@MessageListScrollHelper.layoutManager
                                .scrollToPositionWithOffset(it, 8.dpToPx())
                        } else {
                            with(recyclerView) {
                                this@MessageListScrollHelper.layoutManager
                                    .scrollToPositionWithOffset(it, height / 3)
                                post {
                                    findViewHolderForAdapterPosition(it)
                                        ?.safeCast<BaseMessageItemViewHolder<*>>()
                                        ?.startHighlightAnimation()
                                }
                            }
                        }

                        if (it > SCROLL_BUTTON_VISIBILITY_THRESHOLD) scrollButtonView.isVisible = true
                    }
            },
            HIGHLIGHT_MESSAGE_DELAY,
        )
    }

    internal fun scrollToBottom() {
        recyclerView.scrollToPosition(currentList.lastIndex)
    }

    internal fun onMessageListChanged(
        isThreadStart: Boolean,
        hasNewMessages: Boolean,
        isInitialList: Boolean,
        areNewestMessagesLoaded: Boolean,
    ) {
        logger.d { "[onMessageListChanged] areNewestMessagesLoaded: $areNewestMessagesLoaded" }
        this.areNewestMessagesLoaded = areNewestMessagesLoaded
        scrollButtonView.isVisible = shouldScrollToBottomBeVisible()

        if (!isThreadStart && shouldKeepScrollPosition(areNewestMessagesLoaded, hasNewMessages)) {
            return
        }

        if (isThreadStart) {
            layoutManager.scrollToPosition(currentList.lastIndex)
            return
        }
        val shouldScrollToBottom = shouldScrollToBottom(isInitialList, areNewestMessagesLoaded, hasNewMessages)
        logger.v { "[onMessageListChanged] shouldScrollToBottom: $shouldScrollToBottom" }
        if (shouldScrollToBottom) {
            layoutManager.scrollToPosition(currentList.lastIndex)
            callback.onLastMessageRead()
        }
    }

    private fun shouldKeepScrollPosition(
        areNewestMessagesLoaded: Boolean,
        hasNewMessages: Boolean,
    ): Boolean {
        return !areNewestMessagesLoaded || !scrollToBottomButtonEnabled ||
            (!hasNewMessages || adapter.currentList.isEmpty())
    }

    private fun shouldScrollToBottom(
        isInitialList: Boolean,
        areNewestMessagesLoaded: Boolean,
        hasNewMessages: Boolean,
    ): Boolean {
        return hasNewMessages &&
            areNewestMessagesLoaded &&
            (isInitialList || isLastMessageMine() || isAtBottom || alwaysScrollToBottom)
    }

    private fun isLastMessageMine(): Boolean {
        return currentList
            .lastOrNull()
            ?.safeCast<MessageListItem.MessageItem>()
            ?.isMine
            ?: false
    }

    private fun MessageListItem.isValid(): Boolean {
        return (this is MessageListItem.MessageItem && !(this.isTheirs && this.message.isDeleted())) ||
            (this is MessageListItem.ThreadSeparatorItem)
    }

    internal fun setScrollToBottomHandler(onScrollToBottomHandler: MessageListView.OnScrollToBottomHandler) {
        this.onScrollToBottomHandler = onScrollToBottomHandler
    }

    internal fun interface MessageReadListener {
        fun onLastMessageRead()
    }

    private companion object {
        private const val HIGHLIGHT_MESSAGE_DELAY = 100L
        private const val SCROLL_BUTTON_VISIBILITY_THRESHOLD = 8
    }
}
