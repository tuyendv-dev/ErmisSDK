
package network.ermis.ui.view.messages.preview

import android.content.Context
import android.text.Html
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.FrameLayout
import network.ermis.core.models.Message
import network.ermis.ui.ChatUI
import network.ermis.ui.components.R
import io.getstream.chat.android.ui.common.extensions.internal.singletonList
import network.ermis.ui.components.databinding.MessagePreviewItemBinding
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.bold
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.getAttachmentsText
import network.ermis.ui.utils.extensions.getTranslatedText
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class MessagePreviewView : FrameLayout {

    private val binding = MessagePreviewItemBinding.inflate(streamThemeInflater, this, true)

    constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        parseAttrs(attrs)
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        attrs ?: return
    }

    fun styleView(messagePreviewStyle: MessagePreviewStyle) {
        messagePreviewStyle.run {
            binding.senderNameLabel.setTextStyle(messageSenderTextStyle)
            binding.messageLabel.setTextStyle(messageTextStyle)
            binding.messageTimeLabel.setTextStyle(messageTimeTextStyle)
        }
    }

    fun setMessage(message: Message, currentUserMention: String? = null) {
        binding.userAvatarView.setUser(message.user)
        binding.senderNameLabel.text = formatChannelName(message)
        binding.messageLabel.text = formatMessagePreview(message, currentUserMention)
        binding.messageTimeLabel.text = ChatUI.dateFormatter.formatDate(message.createdAt ?: message.createdLocallyAt)
    }

    private fun formatChannelName(message: Message): CharSequence {
        val channel = message.channelInfo
        return if (channel?.name != null && channel.memberCount > 2) {
            Html.fromHtml(
                context.getString(
                    R.string.ermis_ui_message_preview_sender,
                    message.user.name,
                    channel.name,
                ),
            )
        } else {
            message.user.name.bold()
        }
    }

    private fun formatMessagePreview(message: Message, currentUserMention: String?): CharSequence {
        val attachmentsText = message.getAttachmentsText()
        val displayedText = message.getTranslatedText()
        val previewText = displayedText.trim().let {
            if (currentUserMention != null) {
                // bold mentions of the current user
                it.bold(currentUserMention.singletonList(), ignoreCase = true)
            } else {
                it
            }
        }

        return listOf(previewText, attachmentsText)
            .filterNot { it.isNullOrEmpty() }
            .joinTo(SpannableStringBuilder(), " ")
    }
}
