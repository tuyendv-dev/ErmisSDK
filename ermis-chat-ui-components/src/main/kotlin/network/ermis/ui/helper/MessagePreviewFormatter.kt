
package network.ermis.ui.helper

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import network.ermis.client.utils.message.isSystem
import network.ermis.core.models.Channel
import network.ermis.core.models.Message
import network.ermis.core.models.User
import io.getstream.chat.android.ui.common.extensions.internal.singletonList
import network.ermis.ui.common.utils.extensions.isDirectMessaging
import network.ermis.ui.utils.extensions.asMention
import network.ermis.ui.utils.extensions.bold
import network.ermis.ui.utils.extensions.getAttachmentsText
import network.ermis.ui.utils.extensions.getSenderDisplayName
import network.ermis.ui.utils.extensions.getTranslatedText
import network.ermis.ui.utils.extensions.italicize

/**
 * An interface that allows to generate a preview text for the given message.
 */
public fun interface MessagePreviewFormatter {

    /**
     * Generates a preview text for the given message.
     *
     * @param channel The channel containing the message.
     * @param message The message whose data is used to generate the preview text.
     * @param currentUser The currently logged in user.
     * @return The formatted text representation for the given message.
     */
    public fun formatMessagePreview(
        channel: Channel,
        message: Message,
        currentUser: User?,
    ): CharSequence

    public companion object {
        /**
         * Builds the default message preview text formatter.
         *
         * @param context The context to load string resources.
         * @return The default implementation of [MessagePreviewFormatter].
         *
         * @see [DefaultMessagePreviewFormatter]
         */
        public fun defaultFormatter(context: Context): MessagePreviewFormatter {
            return DefaultMessagePreviewFormatter(context = context)
        }
    }
}

/**
 * The default implementation of [MessagePreviewFormatter] that allows to generate a preview text for
 * a message with the following spans: sender name, message text, attachments preview text.
 *
 * @param context The context to load string resources.
 */
private class DefaultMessagePreviewFormatter(
    private val context: Context,
) : MessagePreviewFormatter {
    /**
     * Generates a preview text for the given message.
     *
     * @param channel The channel containing the message.
     * @param message The message whose data is used to generate the preview text.
     * @param currentUser The currently logged in user.
     * @return The formatted text representation for the given message.
     */
    override fun formatMessagePreview(
        channel: Channel,
        message: Message,
        currentUser: User?,
    ): CharSequence {
        val displayedText = message.getTranslatedText(currentUser)
        return if (message.isSystem()) {
            SpannableStringBuilder(displayedText.trim().italicize())
        } else {
            val sender = message.getSenderDisplayName(context, channel.isDirectMessaging())

            // bold mentions of the current user
            val currentUserMention = currentUser?.asMention(context)
            val previewText: SpannableString =
                displayedText.trim().bold(currentUserMention?.singletonList(), ignoreCase = true)

            val attachmentsText: SpannableString? = message.getAttachmentsText()

            listOf(sender, previewText, attachmentsText)
                .filterNot { it.isNullOrEmpty() }
                .joinTo(SpannableStringBuilder(), ": ")
        }
    }
}
