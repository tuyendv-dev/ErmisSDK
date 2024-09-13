
package network.ermis.ui.view.messages.adapter.viewholder.decorator.impl

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import network.ermis.client.utils.attachment.isAudioRecording
import network.ermis.core.models.Attachment
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.ItemMessageMediaAttachmentBinding
import network.ermis.ui.view.messages.common.AudioRecordPlayerViewStyle
import network.ermis.ui.view.messages.MessageViewStyle
import network.ermis.ui.view.messages.adapter.MessageListItem
import network.ermis.ui.view.messages.adapter.MessageListItemPayloadDiff
import network.ermis.ui.view.messages.adapter.MessageListListeners
import network.ermis.ui.view.messages.adapter.internal.DecoratedBaseMessageItemViewHolder
import network.ermis.ui.view.messages.adapter.view.internal.AttachmentClickListener
import network.ermis.ui.view.messages.adapter.view.internal.AttachmentLongClickListener
import network.ermis.ui.view.messages.adapter.viewholder.decorator.Decorator
import network.ermis.ui.view.messages.internal.LongClickFriendlyLinkMovementMethod
import network.ermis.ui.helper.transformer.ChatMessageTextTransformer
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.streamThemeInflater
import io.getstream.log.taggedLogger

/**
 * ViewHolder used for displaying messages that contain image and/or video attachments.
 *
 * @param parent The parent container.
 * @param decorators List of decorators applied to the ViewHolder.
 * @param listeners Listeners used by the ViewHolder.
 * @param messageTextTransformer Formats strings and sets them on the respective TextView.
 * @param binding Binding generated for the layout.
 */
public class MediaAttachmentsViewHolder internal constructor(
    parent: ViewGroup,
    decorators: List<Decorator>,
    private val listeners: MessageListListeners?,
    private val messageTextTransformer: ChatMessageTextTransformer,
    public val audioRecordViewStyle: MessageViewStyle<AudioRecordPlayerViewStyle>,
    public val binding: ItemMessageMediaAttachmentBinding = ItemMessageMediaAttachmentBinding.inflate(
        parent.streamThemeInflater,
        parent,
        false,
    ),
) : DecoratedBaseMessageItemViewHolder<MessageListItem.MessageItem>(binding.root, decorators) {

    private val logger by taggedLogger("Chat:MediaAttachmentsVH")

    /**
     * Initializes the ViewHolder class.
     */
    init {
        initializeListeners()
        setLinkMovementMethod()
    }

    override fun messageContainerView(): View = binding.messageContainer

    override fun bindData(data: MessageListItem.MessageItem, diff: MessageListItemPayloadDiff?) {
        logger.d { "[bindData] data: $data, diff: $diff" }
        super.bindData(data, diff)

        bindMessageText()
        bindHorizontalBias()
        if (diff?.attachments != false) {
            logger.v { "[bindData] has attachments" }
            bindMediaAttachments()
            bindAudioRecordAttachments()
        }

        bindUploadingIndicator()
    }

    /**
     * Updates the text section of the message.
     */
    private fun bindMessageText() {
        binding.messageText.isVisible = data.message.text.isNotEmpty()
        messageTextTransformer.transformAndApply(binding.messageText, data)
    }

    /**
     * Updates the horizontal bias of the message according to the owner
     * of the message.
     */
    private fun bindHorizontalBias() {
        binding.messageContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            this.horizontalBias = if (data.isMine) 1f else 0f
        }
    }

    /**
     * Updates the media attachments section of the message.
     */
    private fun bindMediaAttachments() {
        logger.d { "[bindMediaAttachments] no args" }
        binding.mediaAttachmentView.setPadding(1.dpToPx())
        binding.mediaAttachmentView.setupBackground(data)
        binding.mediaAttachmentView.showAttachments(data.message.attachments)
    }

    private fun bindAudioRecordAttachments() {
        logger.d { "[bindAudioRecordAttachments] no args" }
        if (data.message.attachments.any { attachment -> attachment.isAudioRecording() }) {
            binding.audioRecordsView.isVisible = true
            binding.audioRecordsView.showAudioAttachments(data.message.attachments)
        } else {
            binding.audioRecordsView.isVisible = false
        }

        val finalAudioRecordViewStyle = if (data.isMine) audioRecordViewStyle.own else audioRecordViewStyle.theirs
        finalAudioRecordViewStyle?.also {
            binding.audioRecordsView.setStyle(it)
        }
    }

    /**
     * Update the uploading status section of the message.
     */
    private fun bindUploadingIndicator() {
        val totalAttachmentsCount = data.message.attachments.size
        val completedAttachmentsCount =
            data.message.attachments.count {
                it.uploadState == null || it.uploadState == Attachment.UploadState.Success
            }
        if (completedAttachmentsCount == totalAttachmentsCount) {
            binding.sentFiles.isVisible = false
        } else {
            binding.sentFiles.text =
                context.getString(
                    R.string.ermis_ui_message_list_attachment_uploading,
                    completedAttachmentsCount,
                    totalAttachmentsCount,
                )
        }
    }

    /**
     * Initializes listeners that enable handling clicks on various
     * elements such as reactions, threads, message containers, etc.
     */
    private fun initializeListeners() {
        binding.run {
            listeners?.let { container ->
                messageContainer.setOnClickListener {
                    container.messageClickListener.onMessageClick(data.message)
                }
                reactionsView.setReactionClickListener {
                    container.reactionViewClickListener.onReactionViewClick(data.message)
                }
                footnote.setOnThreadClickListener {
                    container.threadClickListener.onThreadClick(data.message)
                }
                messageContainer.setOnLongClickListener {
                    container.messageLongClickListener.onMessageLongClick(data.message)
                    true
                }
                userAvatarView.setOnClickListener {
                    container.userClickListener.onUserClick(data.message.user)
                }
                mediaAttachmentView.attachmentClickListener = AttachmentClickListener { attachment ->
                    container.attachmentClickListener.onAttachmentClick(data.message, attachment)
                }
                mediaAttachmentView.attachmentLongClickListener = AttachmentLongClickListener {
                    container.messageLongClickListener.onMessageLongClick(data.message)
                }

                audioRecordsView.attachmentClickListener = AttachmentClickListener { attachment ->
                    container.attachmentClickListener.onAttachmentClick(data.message, attachment)
                }
                audioRecordsView.attachmentLongClickListener = AttachmentLongClickListener {
                    container.messageLongClickListener.onMessageLongClick(data.message)
                }
            }
        }
    }

    /**
     * Enables clicking on links.
     */
    private fun setLinkMovementMethod() {
        listeners?.let { container ->
            LongClickFriendlyLinkMovementMethod.set(
                textView = binding.messageText,
                longClickTarget = binding.messageContainer,
                onLinkClicked = container.linkClickListener::onLinkClick,
            )
        }
    }

    override fun onAttachedToWindow() {
        bindUploadingIndicator()
    }

    override fun unbind() {
        super.unbind()
        binding.audioRecordsView.unbind()
    }
}
