
package network.ermis.ui.utils.extensions

import android.content.Context
import android.text.SpannableString
import network.ermis.client.utils.message.isEphemeral
import network.ermis.client.utils.message.isError
import network.ermis.core.models.Attachment
import network.ermis.core.models.AttachmentType
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.User
import network.ermis.ui.ChatUI
import network.ermis.ui.components.R
import network.ermis.ui.helper.CurrentUserProvider
import network.ermis.core.models.SyncStatus
import java.util.Date

@Deprecated(
    message = "Use the `isErrorOrFailed()` from `stream-chat-android-client` module instead.",
    replaceWith = ReplaceWith(
        expression = "Message.isErrorOrFailed()",
        imports = ["io.getstream.chat.android.client.utils.message.isErrorOrFailed"],
    ),
    level = DeprecationLevel.WARNING,
)
public fun Message.isFailed(): Boolean {
    return this.syncStatus == SyncStatus.FAILED_PERMANENTLY || isError()
}

public fun Message.hasNoAttachments(): Boolean = attachments.isEmpty()

public fun Message.isGiphyNotEphemeral(): Boolean = isEphemeral().not() && command == AttachmentType.GIPHY

public fun Message.getCreatedAtOrNull(): Date? = createdAt ?: createdLocallyAt

public fun Message.getUpdatedAtOrNull(): Date? = updatedAt ?: updatedLocallyAt

public fun Message.getCreatedAtOrThrow(): Date = checkNotNull(getCreatedAtOrNull()) {
    "a message needs to have a non null value for either createdAt or createdLocallyAt"
}

public fun Message.hasSingleReaction(): Boolean {
    return supportedReactionCounts.size == 1
}

public fun Message.hasReactions(): Boolean {
    return supportedReactionCounts.isNotEmpty()
}

public val Message.supportedLatestReactions: List<Reaction>
    get() {
        return if (latestReactions.isEmpty()) {
            latestReactions
        } else {
            latestReactions.filter { ChatUI.supportedReactions.isReactionTypeSupported(it.type) }
        }
    }

public val Message.supportedReactionCounts: Map<String, Int>
    get() {
        return if (reactionCounts.isEmpty()) {
            reactionCounts
        } else {
            reactionCounts.filterKeys { ChatUI.supportedReactions.isReactionTypeSupported(it) }
        }
    }

public fun Message.hasText(): Boolean = text.isNotEmpty()

internal fun Message.getSenderDisplayName(context: Context, isDirectMessaging: Boolean = false): String? =
    when {
        user.isCurrentUser() -> context.getString(R.string.ermis_ui_channel_list_you)
        isDirectMessaging -> null
        else -> user.asMention(context)
    }

internal fun Message.getPinnedText(context: Context): String? {
    val pinnedBy = pinnedBy ?: return null

    val user = if (pinnedBy.isCurrentUser()) {
        context.getString(R.string.ermis_ui_message_list_pinned_message_you)
    } else {
        pinnedBy.name
    }
    return context.getString(R.string.ermis_ui_message_list_pinned_message, user)
}

/**
 * Returns a string representation of message attachments or null if the attachment list is empty.
 */
internal fun Message.getAttachmentsText(): SpannableString? {
    return attachments.takeIf { it.isNotEmpty() }
        ?.mapNotNull { attachment ->
            attachment.title?.let { title ->
                val prefix = getAttachmentPrefix(attachment)
                if (prefix != null) {
                    "$prefix $title"
                } else {
                    title
                }
            } ?: attachment.name ?: attachment.fallback
        }
        ?.joinToString()
        ?.italicize()
}

private fun getAttachmentPrefix(attachment: Attachment): String? =
    when (attachment.type) {
        AttachmentType.GIPHY -> "/giphy"
        else -> null
    }

internal fun Message.getTranslatedText(currentUser: User?): String {
    return getTranslatedText { currentUser }
}

internal inline fun Message.getTranslatedText(getCurrentUser: () -> User?): String {
    return when (ChatUI.autoTranslationEnabled) {
        true -> getCurrentUser()?.language?.let { userLanguage ->
            getTranslation(userLanguage).ifEmpty { text }
        } ?: text
        else -> text
    }
}

internal fun Message.getTranslatedText(currentUserProvider: CurrentUserProvider = ChatUI.currentUserProvider): String {
    return getTranslatedText(currentUserProvider::getCurrentUser)
}