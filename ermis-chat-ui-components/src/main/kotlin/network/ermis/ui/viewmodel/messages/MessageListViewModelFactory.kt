
package network.ermis.ui.viewmodel.messages

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import network.ermis.client.ErmisClient
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Message
import network.ermis.ui.common.feature.messages.composer.MessageComposerController
import network.ermis.ui.common.feature.messages.composer.mention.CompatUserLookupHandler
import network.ermis.ui.common.feature.messages.composer.mention.DefaultUserLookupHandler
import network.ermis.ui.common.feature.messages.composer.mention.UserLookupHandler
import network.ermis.ui.common.feature.messages.composer.mention.toUserLookupHandler
import network.ermis.ui.common.feature.messages.list.DateSeparatorHandler
import network.ermis.ui.common.feature.messages.list.MessageListController
import network.ermis.ui.common.feature.messages.list.MessagePositionHandler
import network.ermis.ui.common.state.messages.list.DeletedMessageVisibility
import network.ermis.ui.common.state.messages.list.MessageFooterVisibility
import network.ermis.ui.common.utils.AttachmentConstants
import network.ermis.ui.common.recording.DefaulErmisMediaRecorder
import network.ermis.ui.common.recording.ErmisMediaRecorder
import java.io.File

/**
 * A ViewModel factory for MessageListViewModel, MessageListHeaderViewModel and MessageComposerViewModel.
 *
 * @param cid The current channel ID, to load the messages from.
 * @param messageId The message ID to which we want to scroll to when opening the message list.
 * @param parentMessageId The ID of the parent [Message] if the message we want to scroll to is in a thread. If the
 * message we want to scroll to is not in a thread, you can pass in a null value.
 * @param chatClient The client to use for API calls.
 * @param clientState The current state of the SDK.
 * @param mediaRecorder The media recorder for async voice messages.
 * @param messageLimit The limit of the messages to load in a single page.
 * @param enforceUniqueReactions Flag to enforce unique reactions or enable multiple from the same user.
 * @param maxAttachmentCount The maximum number of attachments that can be sent in a single message.
 * @param maxAttachmentSize The maximum file size of each attachment in bytes. By default, 100mb for Stream CDN.
 * @param showSystemMessages If we should show system message items in the list.
 * @param deletedMessageVisibility The behavior of deleted messages in the list and if they're visible or not.
 * @param messageFooterVisibility The behavior of message footers in the list and their visibility.
 * @param dateSeparatorHandler Handler that determines when the date separators should be visible.
 * @param threadDateSeparatorHandler Handler that determines when the thread date separators should be visible.
 * @param messagePositionHandler Determines the position of the message inside a group.
 * @param showDateSeparatorInEmptyThread Configures if we show a date separator when threads are empty.
 * Adds the separator item when the value is `true`.
 * @param showThreadSeparatorInEmptyThread Configures if we show a thread separator when threads are empty.
 * Adds the separator item when the value is `true`.
 *
 * @see MessageListHeaderViewModel
 * @see MessageListViewModel
 * @see MessageComposerViewModel
 */
public class MessageListViewModelFactory @JvmOverloads constructor(
    context: Context,
    private val cid: String,
    private val messageId: String? = null,
    private val parentMessageId: String? = null,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val clientState: ClientState = chatClient.clientState,
    private val mediaRecorder: ErmisMediaRecorder = DefaulErmisMediaRecorder(context.applicationContext),
    private val userLookupHandler: UserLookupHandler = DefaultUserLookupHandler(chatClient, cid),
    private val fileToUri: (File) -> String = { file -> file.toUri().toString() },
    private val messageLimit: Int = MessageListController.DEFAULT_MESSAGES_LIMIT,
    private val enforceUniqueReactions: Boolean = false,
    private val maxAttachmentCount: Int = AttachmentConstants.MAX_ATTACHMENTS_COUNT,
    private val maxAttachmentSize: Long = AttachmentConstants.MAX_UPLOAD_FILE_SIZE,
    private val showSystemMessages: Boolean = true,
    private val deletedMessageVisibility: DeletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,
    private val messageFooterVisibility: MessageFooterVisibility = MessageFooterVisibility.WithTimeDifference(),
    private val dateSeparatorHandler: DateSeparatorHandler = DateSeparatorHandler.getDefaultDateSeparatorHandler(),
    private val threadDateSeparatorHandler: DateSeparatorHandler =
        DateSeparatorHandler.getDefaultThreadDateSeparatorHandler(),
    private val messagePositionHandler: MessagePositionHandler = MessagePositionHandler.defaultHandler(),
    private val showDateSeparatorInEmptyThread: Boolean = false,
    private val showThreadSeparatorInEmptyThread: Boolean = false,
) : ViewModelProvider.Factory {

    private val factories: Map<Class<*>, () -> ViewModel> = mapOf(
        MessageListHeaderViewModel::class.java to { MessageListHeaderViewModel(cid, messageId = messageId) },
        MessageListViewModel::class.java to {
            MessageListViewModel(
                messageListController = MessageListController(
                    cid = cid,
                    clipboardHandler = {},
                    messageLimit = messageLimit,
                    messageId = messageId,
                    parentMessageId = parentMessageId,
                    chatClient = chatClient,
                    clientState = clientState,
                    enforceUniqueReactions = enforceUniqueReactions,
                    showSystemMessages = showSystemMessages,
                    deletedMessageVisibility = deletedMessageVisibility,
                    messageFooterVisibility = messageFooterVisibility,
                    dateSeparatorHandler = dateSeparatorHandler,
                    threadDateSeparatorHandler = threadDateSeparatorHandler,
                    messagePositionHandler = messagePositionHandler,
                    showDateSeparatorInEmptyThread = showDateSeparatorInEmptyThread,
                    showThreadSeparatorInEmptyThread = showThreadSeparatorInEmptyThread,
                ),
                chatClient = chatClient,
            )
        },
        MessageComposerViewModel::class.java to {
            MessageComposerViewModel(
                MessageComposerController(
                    channelCid = cid,
                    chatClient = chatClient,
                    mediaRecorder = mediaRecorder,
                    userLookupHandler = userLookupHandler,
                    maxAttachmentCount = maxAttachmentCount,
                    maxAttachmentSize = maxAttachmentSize,
                    fileToUri = fileToUri,
                    messageId = messageId,
                    messageLimit = messageLimit,
                ),
            )
        },
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = factories[modelClass]?.invoke()
            ?: throw IllegalArgumentException("MessageListViewModelFactory can only create instances of the following classes: ${factories.keys.joinToString { it.simpleName }}")

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }

    @Suppress("NEWER_VERSION_IN_SINCE_KOTLIN")
    public class Builder
    @SinceKotlin("99999.9")
    constructor(
        private val context: Context,
    ) {
        private var cid: String? = null
        private var messageId: String? = null
        private var chatClient: ErmisClient = ErmisClient.instance()
        private var enforceUniqueReactions: Boolean = false
        private var maxAttachmentCount: Int = AttachmentConstants.MAX_ATTACHMENTS_COUNT
        private var maxAttachmentSize: Long = AttachmentConstants.MAX_UPLOAD_FILE_SIZE
        private var showSystemMessages: Boolean = true
        private var deletedMessageVisibility: DeletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE
        private var messageFooterVisibility: MessageFooterVisibility = MessageFooterVisibility.WithTimeDifference()
        private var dateSeparatorHandler: DateSeparatorHandler = DateSeparatorHandler.getDefaultDateSeparatorHandler()
        private var threadDateSeparatorHandler: DateSeparatorHandler =
            DateSeparatorHandler.getDefaultThreadDateSeparatorHandler()
        private var messagePositionHandler: MessagePositionHandler = MessagePositionHandler.defaultHandler()
        private var mediaRecorder: ErmisMediaRecorder = DefaulErmisMediaRecorder(context.applicationContext)
        private var userLookupHandler: UserLookupHandler? = null
        private var userLookupHandlerCompat: CompatUserLookupHandler? = null

        /**
         * Sets the channel id in the format messaging:123.
         */
        public fun cid(cid: String): Builder = apply {
            this.cid = cid
        }

        /**
         * Sets the id of the target message to displayed.
         */
        public fun messageId(messageId: String): Builder = apply {
            this.messageId = messageId
        }

        public fun chatClient(chatClient: ErmisClient): Builder = apply {
            this.chatClient = chatClient
        }

        public fun mediaRecorder(mediaRecorder: ErmisMediaRecorder): Builder = apply {
            this.mediaRecorder = mediaRecorder
        }

        public fun userLookupHandlerCompat(userLookupHandler: CompatUserLookupHandler): Builder = apply {
            this.userLookupHandlerCompat = userLookupHandler
        }

        public fun userLookupHandler(userLookupHandler: UserLookupHandler): Builder = apply {
            this.userLookupHandler = userLookupHandler
        }

        public fun enforceUniqueReactions(enforceUniqueReactions: Boolean): Builder = apply {
            this.enforceUniqueReactions = enforceUniqueReactions
        }

        public fun maxAttachmentCount(maxAttachmentCount: Int): Builder = apply {
            this.maxAttachmentCount = maxAttachmentCount
        }

        public fun maxAttachmentSize(maxAttachmentSize: Long): Builder = apply {
            this.maxAttachmentSize = maxAttachmentSize
        }

        public fun showSystemMessages(showSystemMessages: Boolean): Builder = apply {
            this.showSystemMessages = showSystemMessages
        }

        public fun deletedMessageVisibility(deletedMessageVisibility: DeletedMessageVisibility): Builder = apply {
            this.deletedMessageVisibility = deletedMessageVisibility
        }

        public fun messageFooterVisibility(messageFooterVisibility: MessageFooterVisibility): Builder = apply {
            this.messageFooterVisibility = messageFooterVisibility
        }

        public fun dateSeparatorHandler(dateSeparatorHandler: DateSeparatorHandler): Builder = apply {
            this.dateSeparatorHandler = dateSeparatorHandler
        }

        public fun threadDateSeparatorHandler(threadDateSeparatorHandler: DateSeparatorHandler): Builder = apply {
            this.threadDateSeparatorHandler = threadDateSeparatorHandler
        }

        /**
         * Builds [MessageListViewModelFactory] instance.
         */
        public fun build(): ViewModelProvider.Factory {
            val cid = cid ?: error("Channel cid should not be null")
            return MessageListViewModelFactory(
                context = context,
                cid = cid,
                messageId = messageId,
                chatClient = chatClient,
                mediaRecorder = mediaRecorder,
                userLookupHandler = userLookupHandler
                    ?: userLookupHandlerCompat?.toUserLookupHandler()
                    ?: DefaultUserLookupHandler(chatClient, cid),
                enforceUniqueReactions = enforceUniqueReactions,
                maxAttachmentCount = maxAttachmentCount,
                maxAttachmentSize = maxAttachmentSize,
                showSystemMessages = showSystemMessages,
                deletedMessageVisibility = deletedMessageVisibility,
                messageFooterVisibility = messageFooterVisibility,
                dateSeparatorHandler = dateSeparatorHandler,
                threadDateSeparatorHandler = threadDateSeparatorHandler,
                messagePositionHandler = messagePositionHandler,
            )
        }
    }
}
